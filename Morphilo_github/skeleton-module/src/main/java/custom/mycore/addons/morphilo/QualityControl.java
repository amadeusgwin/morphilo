/**
 * class calculates probability distribution of correctly annotated words
 * based on a given number of different users and confidence interval, i.e. if
 * e.g. 30 different users had the exact same annotations made on a word item, 
 * the word is copied to the master database 
 */

package custom.mycore.addons.morphilo;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import org.mycore.user2.MCRUserManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.jdom2.JDOMException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Content;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.filter.Filters;
import org.jdom2.Namespace;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.mycore.solr.MCRSolrClientFactory;
import org.mycore.solr.MCRSolrUtils;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jdom2.Namespace;
import org.mycore.datamodel.niofs.MCRPath;

public class QualityControl {
	/*
	 * Schematischer Entwurf
	 * Ermitteln der Nutzeranzahl --> nicht wirklich relevant
	 * gerade angelegtes Wort fuer einen jeden Ersteller (Nutzer) pruefen, ob
	 * das Textkorpus unterschiedlich ist und ob die JDOM-Struktur genau gleich ist (in gegebenen Zeitrahmen)
	 * = testen auf Gleichheit zweier MyCoRe-Objekte?import org.jdom2.Namespace;
	 * wenn bei n=30 die Strukturen gleich sind (result.size()), ersetze Nutzer durch "administrator" (dadurch gehoert es
	 * zur Morphilo-Datenbasis)
	 * Wenn Korpus gleich ist wird die occurrences nur einmal angepasst, ansonsten aufaddiert
	 */
	
	private MCRObject mycoreObject;
	private static Logger LOGGER = LogManager.getLogger();
	private ArrayList<MCRObject> relevantObjects = new ArrayList<MCRObject>();
	
	/*
	 * Constructor calls method to carry out quality control, i.e. if at least 20 different
	 * users agree 100% on the segments of the word under investigation
	 */
	public QualityControl(MCRObject mycoreObject) throws Exception
	{
		this.mycoreObject = mycoreObject;
		LOGGER.info("******************************************************************");
		LOGGER.info(getEqualObjectNumber());
		LOGGER.info("******************************************************************");
		if (getEqualObjectNumber() > 20)
		{
			addToMorphiloDB();
		}
	}
	/*
	 * retrieves the number of registered morphilo users
	 * (not really needed since it is implicitly contained in the number of equal objects - 
	 * each user can only have one object of a kind available)
	 */
	protected int getUserNumber()
	{
		return MCRUserManager.countUsers(null, null, null);
	}
	
	/*
	 * return an xml representation as a string from parts of a mycore object
	 */
	private String getXMLFromObject(MCRObject mcrobj, String xpath) 
	{
		String xmlRepres = "";
		Document jdomDoc = mcrobj.createXML();
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile(xpath, Filters.element());
		for (Element e : xp.evaluate(jdomDoc))
		{
			XMLOutputter outputter = new XMLOutputter();
		    outputter.setFormat(Format.getPrettyFormat());
		    xmlRepres += outputter.outputString(e.getContent());
		}
		return xmlRepres;
	}
	
	/*
	 * method takes two mycore objects and checks them for equality from the given xpath on
	 */
	private Boolean compareMCRObjects(MCRObject mcrobj1, MCRObject mcrobj2, String xpath) throws SAXException, IOException
	{
		Boolean isEqual = false;
		Boolean beginTime = false;
		Boolean endTime = false;
		Boolean occDiff = false;
		Boolean corpusDiff = false;
		
		String source = getXMLFromObject(mcrobj1, xpath);
		String target = getXMLFromObject(mcrobj2, xpath);

		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalizeWhitespace(true);
		
		//differences in occurrences, end, begin should be ignored
		try
		{
			Diff xmlDiff = new Diff(source, target);
			DetailedDiff dd = new DetailedDiff(xmlDiff);
			//counters for differences
			int i = 0;
			int j = 0;
			int k = 0;
			int l = 0;
			// list containing all differences
			List differences = dd.getAllDifferences();
			for (Object object : differences)
			{
				Difference difference = (Difference) object;
				//@begin,@end,... node is not in the difference list if the count is 0
				if (difference.getControlNodeDetail().getXpathLocation().endsWith("@begin")) i++;
				if (difference.getControlNodeDetail().getXpathLocation().endsWith("@end")) j++;
				if (difference.getControlNodeDetail().getXpathLocation().endsWith("@occurrence")) k++; 
				if (difference.getControlNodeDetail().getXpathLocation().endsWith("@corpus")) l++;
				//@begin and @end have different values: they must be checked if they fall right in the allowed time range		
				if ( difference.getControlNodeDetail().getXpathLocation().equals(difference.getTestNodeDetail().getXpathLocation()) 
					&& difference.getControlNodeDetail().getXpathLocation().endsWith("@begin") 
					&& (Integer.parseInt(difference.getControlNodeDetail().getValue()) < Integer.parseInt(difference.getTestNodeDetail().getValue())) ) 
				{
					beginTime = true;
				}
				if (difference.getControlNodeDetail().getXpathLocation().equals(difference.getTestNodeDetail().getXpathLocation()) 
					&& difference.getControlNodeDetail().getXpathLocation().endsWith("@end")
					&& (Integer.parseInt(difference.getControlNodeDetail().getValue()) > Integer.parseInt(difference.getTestNodeDetail().getValue())) )
				{
					endTime = true;
				}
				//attribute values of @occurrence and @corpus are ignored if they are different
				if (difference.getControlNodeDetail().getXpathLocation().equals(difference.getTestNodeDetail().getXpathLocation()) 
					&& difference.getControlNodeDetail().getXpathLocation().endsWith("@occurrence"))
				{
					occDiff = true;
				}
				if (difference.getControlNodeDetail().getXpathLocation().equals(difference.getTestNodeDetail().getXpathLocation()) 
					&& difference.getControlNodeDetail().getXpathLocation().endsWith("@corpus"))
				{
					corpusDiff = true;
				}						
			}
			//if any of @begin, @end ... is identical set Boolean to true
			if (i == 0) beginTime = true;
			if (j == 0) endTime = true;
			if (k == 0) occDiff = true;
			if (l == 0) corpusDiff = true;
			//if the size of differences is greater than the number of changes admitted in @begi, @end ... it must be something else different
			if (beginTime && endTime && occDiff && corpusDiff && (i + j + k + l) == dd.getAllDifferences().size()) isEqual = true;
		}
		catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

		return isEqual; 
	}
	
	/*
	 * returns word from given MyCoRe-Object
	 */
	private String getWordFromObject()
	{
		String word = "";
		Document jdomDoc = mycoreObject.createXML();
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xpe = xpfac.compile("//morphiloContainer/morphilo/w", Filters.element());
		Element e = xpe.evaluateFirst(jdomDoc);
		word = e.getText().toString().trim();
		
		return word;
	}
	
	/*
	 * initiates solr search for all Objects that contain the word of the original Object
	 */
	private ArrayList<MCRObject> getAllMCRObjects() throws Exception
	{
		ArrayList<MCRObject> allObjects = new ArrayList<MCRObject>();
		String word = getWordFromObject();
		//Search for word and write all solr hits to allObjects
		SolrClient solrClient = MCRSolrClientFactory.getSolrClient();
        SolrQuery query = new SolrQuery();
        query.setFields("w", "id");
        query.setQuery(word);
        query.setRows(50); //Integer.MAX_VALUE mehr als 50 verschiedene Eintraege = Nutzer sind sehr unwahrscheinlich
        SolrDocumentList results = solrClient.query(query).getResults();
        for (int entryNum = 0; entryNum < results.size(); entryNum++)
        {
        	String objectID = results.get(entryNum).getFieldValue("id").toString();
        	MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(objectID));
        	allObjects.add(mcrObj);
        }
  		return allObjects;
	}
	
	/*
	 * returns a list with all Objects that are equal
	 */
	private ArrayList<MCRObject> getAllEqualMCRObjects() throws Exception
	{
		ArrayList<MCRObject> allObjects = new ArrayList<MCRObject>();
		ArrayList<MCRObject> equalObjects = new ArrayList<MCRObject>();
		allObjects = getAllMCRObjects();
		for (MCRObject obj : allObjects)
		{
			if (compareMCRObjects(mycoreObject, obj, "//morphiloContainer/morphilo"))
			{
				equalObjects.add(obj);
			}
		}
		relevantObjects = equalObjects;
		return equalObjects;
	}
	
	/*
	 * extracts an attribute value from on MCR Object
	 */
	private String getAttributeValue(String xpath, String attr, MCRObject mcro)
	{
		Namespace xlinkNamespace = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
		Document jdomDoc = mcro.createXML();
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xpe = xpfac.compile(xpath, Filters.element());
		Element e = xpe.evaluateFirst(jdomDoc);
		return e.getAttributeValue(attr,xlinkNamespace).toString().trim();
	}
	
	/*
     * 
     */
    private ArrayList<String> readDerivateFile(Path file)
    {
    	ArrayList<String> content = new ArrayList<String>();
    	try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8))
    	{
    		Scanner wort = new Scanner(br);
    		//int i = 0;
        	while (wort.hasNext())
        	{
        		content.add(wort.next());
        		//i++;
        	}
    		
    	}
    	catch (IOException x) 
    	{
    	    System.err.println(x);
    	} 
    	return content;
    }
	
	/*
	 * returns the content of a file beginning with filename from the derivates
	 */
	public ArrayList getContentFromFile(String derivID, String fileName)
    {
		MCRPath root = MCRPath.getPath(derivID, "/");
		ArrayList content = new ArrayList();
    	try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) 
    	{
    	    for (Path file: stream) 
    	    {
    	    	String fs = file.getFileName().toString();
    	    	if (fs.startsWith(fileName))
    	    	{
    	    		content = readDerivateFile(file);    	    	
    	    	}
    	    }
    	} 
    	catch (IOException | DirectoryIteratorException x) 
    	{
    	    System.err.println(x);
    	}
    	return content;
    }
	
	/* 
	 * returns number of Occurrences if Objects are equal, zero otherwise
	 */
	private int getOccurrencesFromEqualTexts(MCRObject mcrobj1, MCRObject mcrobj2) throws SAXException, IOException
	{
		int occurrences = 1;
		//extract corpmeta ObjectIDs from morphilo-Objects
		String crpID1 = getAttributeValue("//corpuslink", "href", mcrobj1);
		String crpID2 = getAttributeValue("//corpuslink", "href", mcrobj2);
		//get these two corpmeta Objects
		MCRObject corpo1 = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(crpID1));
		MCRObject corpo2 = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(crpID2));
		//call above method compareMCRObjects(mcrobj1, mcrobj2) ???
		//if (compareMCRObjects(corpo1, corpo2, "//metadata"))
		//TODO: several derivates and several corpo-Objects???
			//are the texts equal? get list of 'original text' derivate
			String corp1DerivID = getAttributeValue("//structure/derobjects/derobject", "href", corpo1);
			String corp2DerivID = getAttributeValue("//structure/derobjects/derobject", "href", corpo2);
			
			ArrayList result = new ArrayList(getContentFromFile(corp1DerivID, "processed"));
			result.remove(getContentFromFile(corp2DerivID, "processed"));
			if (result.size() == 0) // the texts are equal
			{
				// extract occurrences of one the objects
				occurrences = Integer.parseInt(getAttributeValue("//morphiloContainer/morphilo/w", "occurrence", mcrobj1));
			}
			else
			{
				occurrences = 0; //project metadata happened to be the same, but texts are different
			}

		return occurrences;
	}
	
	/* 
	 * returns all equal Objects that stem from different corpora/texts
	 */
	private int getOccurrencesFromEqualTexts() throws Exception
	{
		ArrayList<MCRObject> equalObjects = new ArrayList<MCRObject>();
		equalObjects = getAllEqualMCRObjects();
		int occurrences = 0; 
		for (MCRObject obj : equalObjects)
		{
			occurrences = occurrences + getOccurrencesFromEqualTexts(mycoreObject, obj);			
		}
		return occurrences;
	}
	
	/*
	 * method retrieves number of equal mycore objects for all registered users
	 */
	private int getEqualObjectNumber() throws Exception
	{ 
		return getAllEqualMCRObjects().size();
	}
	
	/*
	 * method retrieves all occurrences of all equal mycore objects, creates a new mycore object 
	 * including createdby=administrator, occurrences of all equal mycore objects but the ones
	 * that belong to the same text/korpus
	 */
	private void addToMorphiloDB() throws Exception
	{
		int occurrences = 0;
		for (MCRObject obj : relevantObjects)
		{
			//lies occurrences aus und füge sie mit occurrences = occurrences + newOcc hinzu,
			Document jdomDoc = obj.createXML();
			XPathFactory xpfac = XPathFactory.instance();
			XPathExpression<Element> xp = xpfac.compile("//morphiloContainer/morphilo/w", Filters.element());
			for (Element e : xp.evaluate(jdomDoc))
			{
				int oc = 0;
				try
            	{
					oc = Integer.parseInt(e.getAttributeValue("occurrence"));
					occurrences = occurrences + oc;
            	}
				catch(NumberFormatException except)
            	{
            		//ignore for now
            	}
			}
		}
		occurrences = occurrences - getOccurrencesFromEqualTexts();
		
		//erstelle neues (gleiches) Objekt mit occurrences und createdby = administrator state = published
		MCRObject masterObject = mycoreObject;
		Document jdomDoc = masterObject.createXML();
		XPathFactory xpfac = XPathFactory.instance();

		//set createdby flag
		XPathExpression<Element> xp = xpfac.compile("//service/servflags/servflag[@type='createdby']", Filters.element());
		Element elemCreatedBy = xp.evaluateFirst(jdomDoc);
		elemCreatedBy.setText("administrator");
		
		//set state
		XPathExpression<Element> xpState = xpfac.compile("//service/servstates/servstate[@classid='state']", Filters.element());
		Element elemState = xpState.evaluateFirst(jdomDoc);
		elemState.setAttribute("categid", "published");

		//set occurrence attribute
		XPathExpression<Element> xpOcc = xpfac.compile("//morphiloContainer/morphilo/w", Filters.element());
		Element elemOcc = xpOcc.evaluateFirst(jdomDoc);
		elemOcc.setAttribute("occurrence", Integer.toString(occurrences));

		//make changes persistent
        masterObject = new MCRObject(jdomDoc);
        MCRMetadataManager.update(masterObject);
	}
}
