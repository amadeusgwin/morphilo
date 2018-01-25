/**
 * 
 */
package custom.mycore.addons.morphilo;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

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
import org.mycore.access.MCRAccessException;
import org.mycore.common.content.MCRFileContent;
import org.mycore.common.content.MCRStringContent;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.frontend.fileupload.MCRUploadHandlerIFS;
import org.mycore.datamodel.niofs.MCRPath;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRBase;
import org.mycore.datamodel.metadata.MCRMetaElement;
import org.mycore.datamodel.metadata.MCRObjectMetadata;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.common.MCRActiveLinkException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.mycore.solr.MCRSolrClientFactory;
import org.mycore.solr.MCRSolrUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;

/**
 * Gibt Inhalt eines oder mehrerer hochgeladener Textderivate auf dem Bildschirm aus
 * @author Hagen Peukert
 *
 */
public class ProcessCorpusServlet extends MCRServlet {

    /**
     * 
     */
	
    private static final long serialVersionUID = 3148314720092349972L;
    
    private static Logger LOGGER = LogManager.getLogger();

    private MCRServletJob job;
    
    @Override
    public void doGetPost(MCRServletJob job)
			throws ServletException, IOException, SAXException, TransformerException, 
			SolrServerException, MCRActiveLinkException, MCRAccessException, Exception
    {

	}

    
    /*
     * 
     */
    public String getCorpusMetadata(MCRServletJob job, String datafield)
    {
    	MCRObject mcrCorpObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(getURLParameter(job, "objID")));
    	MCRObjectMetadata  mtdObj = mcrCorpObj.getMetadata();
    	MCRMetaElement mtdElem = mtdObj.getMetadataElement(datafield);
    	MCRMetaLangText mtdMlt = (MCRMetaLangText) mtdElem.getElement(0);
    	return mtdMlt.getText();
    }
    
    /*
     * 
     */
    public String getURLParameter(MCRServletJob job, String urlParam)
    {
    	MCRFrontendUtil prop = new MCRFrontendUtil();
    	return prop.getProperty(job.getRequest(), urlParam).get();
    }
   
    
    /*
     * reads content of derivate files
     * an empty String as fileName Parameter means the main Derivate is taken
     */
    public ArrayList<String> getContentFromFile(MCRServletJob job, String fileName)
    {
    	String derivID = getURLParameter(job, "id");
		MCRPath root = MCRPath.getPath(derivID, "/");
		ArrayList<String> content = new ArrayList<String>();
    	try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) 
    	{
    	    for (Path file: stream) 
    	    {
    	    	String fs = file.getFileName().toString();
    	    	
    	    	//if (fs.startsWith(fileName && !fileName.isEmpty()))
    	    	if (fileName.isEmpty() && !fs.startsWith("untagged") && !fs.startsWith("tagged") && !fs.startsWith("processed"))
    	    	{
    	    		content = readDerivateFile(file);    	    	
    	    	}
    	    	else if (fs.startsWith(fileName) && !fileName.isEmpty())
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
     * needed to write content of leftover into file (see below)
     */
    public Path getDerivateFilePath(MCRServletJob job, String filename)
    {
    	Path derivFilePath = Paths.get("");
    	String derivID = getURLParameter(job, "id");
		MCRPath root = MCRPath.getPath(derivID, "/");
		
    	try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) 
    	{
    	    for (Path file: stream) 
    	    {
    	    	String fs = file.getFileName().toString();
    	    	if (filename.isEmpty())
    	    	{
    	    		derivFilePath = file;
    	    	}
    	    	else if (fs.startsWith(filename))
    	    	{
    	    		derivFilePath = file;
    	    	}
    	    }
    	} 
    	catch (IOException | DirectoryIteratorException x) 
    	{
    	    System.err.println(x);
    	}
    	return derivFilePath;
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
     * if fileName is empty, the main derivate file is processed
     */
    public int getNumberOfWords(MCRServletJob job, String fileName, int numWords)
    {
    	//int numWords = 0;
//    	String derivID = getURLParameter(job, "id");
//		MCRPath root = MCRPath.getPath(derivID, "/");
//		ArrayList<String> content = new ArrayList<String>();
//    	try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) 
//    	{
//    	    for (Path file: stream) 
//    	    {
//    	    	String fs = file.getFileName().toString();
//    	    	if (fileName.isEmpty())
//    	    	{
//    	    		numWords = numWords + getContentFromFile(job, fileName).size();
//    	    	}
//    	    	else if (fs.startsWith(fileName))
//    	    	{
//    	    		numWords = numWords + getContentFromFile(job, fileName).size();
//    	    	}
//    	    }
//    	} 
//    	catch (IOException | DirectoryIteratorException x) 
//    	{
//    	    System.err.println(x);
//    	}
    	
    	return numWords + getContentFromFile(job, fileName).size();
    	//return getContentFromFile(job, fileName).size();
    }
    
    /**
     * 1st phase of doGetPost. This method has a seperate transaction. Per default id does nothing as a fallback to the
     * old behaviour.
     * 
     * @see #render(MCRServletJob, Exception)
     */
    protected void think(MCRServletJob job) throws Exception 
    {
    	
    	this.job = job;
    	String dateFromCorp = getCorpusMetadata(job, "def.datefrom");
    	String dateUntilCorp = getCorpusMetadata(job, "def.dateuntil");
    	String corpID = getURLParameter(job, "objID");
    	String derivID = getURLParameter(job, "id");
        
    	//if NoW is 0, fill with anzWords
   	 	MCRObject helpObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(corpID));
   	 	Document jdomDocHelp = helpObj.createXML();
   	 	XPathFactory xpfacty = XPathFactory.instance();
   	 	XPathExpression<Element> xpExp = xpfacty.compile("//NoW", Filters.element());
   	 	Element elem = xpExp.evaluateFirst(jdomDocHelp);
   	 	int corpussize = getNumberOfWords(job, "", Integer.parseInt(elem.getText()));
        if (Integer.parseInt(elem.getText()) != corpussize)
        {
             elem.setText(Integer.toString(corpussize));
             helpObj = new MCRObject(jdomDocHelp);
             MCRMetadataManager.update(helpObj);
       	}
        
    	
        //Check if the uploaded corpus was processed before
        SolrClient slr = MCRSolrClientFactory.getSolrClient();
        SolrQuery qry = new SolrQuery();
        qry.setFields("korpusname", "datefrom", "dateuntil", "NoW", "id");
        qry.setQuery("datefrom:" + dateFromCorp + " AND dateuntil:" + dateUntilCorp + " AND NoW:" + corpussize);
        SolrDocumentList rslt = slr.query(qry).getResults();
        
        String korpusname = "";
        Boolean incrOcc = true;
        // if resultset contains only one, then it must be the newly created corpus
        if (slr.query(qry).getResults().getNumFound() > 1) 
        {
        	//delete corpmeta for the new corpus
        	//MCRMetadataManager.delete(MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(corpID)));
        	incrOcc = false;
        	//get the relevant corpID from SolrDocumentList rslt
       		corpID = rslt.get(0).getFieldValue("id").toString();
       		korpusname = rslt.get(0).getFieldValue("korpusname").toString();
        }
        
        
        //match all words in corpus with morphilo (creator=administrator) and save all words that are not in morphilo DB in leftovers
        ArrayList<String> leftovers = new ArrayList<String>();
        ArrayList<String> processed = new ArrayList<String>();
        
        leftovers = getUnknownWords(getContentFromFile(job, ""), dateFromCorp, dateUntilCorp, "", incrOcc, incrOcc, false);
        
        //write all words of leftover in file as derivative to respective corpmeta dataset        
        MCRPath root = MCRPath.getPath(derivID, "/");
        Path fn = getDerivateFilePath(job, "").getFileName();
        Path p = root.resolve("untagged-" + fn);
        Files.write(p, leftovers);
        
        //create a file for all words that were processed
        Path procWds = root.resolve("processed-" + fn);
        Files.write(procWds, processed);
    	       
    }
    
    /* 
     * checks if word is already available in the respective datasets
     * if setOcc is true occurrence Attribut is incremented
     * if setXlink is true the Derivate ID of the corpus is written to 
     * the label-attribute of the morphilo document
     */
    
    public ArrayList<String> getUnknownWords(
    		ArrayList<String> corpus, 
    		String timeCorpusBegin, 
    		String timeCorpusEnd, 
    		String wdtpe,
    		Boolean setOcc,
    		Boolean setXlink,
    		Boolean writeAllData) throws Exception
    {
    	String currentUser = MCRSessionMgr.getCurrentSession().getUserInformation().getUserID();
    	ArrayList lo = new ArrayList();
    	
    	for (int i = 0; i < corpus.size(); i++) 
        {
            SolrClient solrClient = MCRSolrClientFactory.getSolrClient();
            SolrQuery query = new SolrQuery();
            query.setFields("w","occurrence","begin","end", "id", "wordtype");
            query.setQuery(corpus.get(i));
            query.setRows(50); //Integer.MAX_VALUE mehr als 50 verschiedene Eintraege = Nutzer sind sehr unwahrscheinlich
            SolrDocumentList results = solrClient.query(query).getResults();
            Boolean available = false;

        	for (int entryNum = 0; entryNum < results.size(); entryNum++)
            {
            	String timeBegin ="";
            	String timeEnd = "";
            	String wordtype = "";
            	
            	if (results.get(entryNum).getFieldValue("wordtype") == null)
            	{
            		wordtype = "";
            	}
            	else 
            	{
            		wordtype = results.get(entryNum).getFieldValue("wordtype").toString();
            		//code to handle different wordtypes from previous application
            		if (wordtype.equals("N")) wordtype = "noun";
            		if (wordtype.equals("V")) wordtype = "verb";
            		if (wordtype.equals("A") || wordtype.equals("ADJ")) wordtype = "adjective";
            	}
            	
            	if (results.get(entryNum).getFieldValue("begin") == null)
                {
            		timeBegin = "0";
                }
            	else
            	{
            		timeBegin = results.get(entryNum).getFieldValue("begin").toString();
            	}
            	if (results.get(entryNum).getFieldValue("end") == null)
                {
            		timeEnd = "100000";
                }
            	else
            	{
            		timeEnd = results.get(entryNum).getFieldValue("end").toString();
            	}
            	
            	// update in MCRMetaDataManager
                String mcrIDString = results.get(entryNum).getFieldValue("id").toString();
                //MCRObjekt auslesen und JDOM-Document erzeugen:
                MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrIDString));
                Document jdomDoc = mcrObj.createXML();
                
                //check if the same time period applies
            	Boolean timeCorrect = false;
                if (Integer.parseInt(timeBegin) <= Integer.parseInt(timeCorpusBegin) &&
                	Integer.parseInt(timeEnd) >= Integer.parseInt(timeCorpusEnd) )
                {
                	timeCorrect = true;
                }
                
                    //change path with JDOM2 / XPath
                    XPathFactory xpfac = XPathFactory.instance();
                    //Check authority of User
                	Boolean isAuthorized = false;
                	Boolean isMorphiloData = false;
                    XPathExpression<Element> xpCreator =xpfac.compile("//service/servflags/servflag[@type='createdby']", Filters.element());
                	for (Element element : xpCreator.evaluate(jdomDoc))
                	{
                		//if createdby is administrator, it must be the master, 
                		//otherwise it is written to the dataset of the authentified user or (if false) not at all
                		if (element.getText().equals(currentUser) || element.getText().equals("administrator"))
                		{
            				isAuthorized = true;
                		}
                	}
                	
                	// all data is written to lo in TEI
                	if (writeAllData && isAuthorized && timeCorrect)
            		{
                		XPathExpression<Element> xpath = xpfac.compile("//morphiloContainer/morphilo", Filters.element());
                		for (Element e : xpath.evaluate(jdomDoc))
                		{
                			XMLOutputter outputter = new XMLOutputter();
                		    outputter.setFormat(Format.getPrettyFormat());
                		    lo.add(outputter.outputString(e.getContent()));
                		}
            		}
                	
	                XPathExpression<Element> xp = xpfac.compile("//morphiloContainer/morphilo/w", Filters.element());

	                //Iteriere über alle gefundene w-Elemente und setzte occurrence-Attribut um 1 hoch if setOcc is true. 
	                for (Element e : xp.evaluate(jdomDoc)) 
	                {
	                	//wenn Rechte da sind und Worttyp nirgends gegeben oder gleich ist 
	                	if (isAuthorized && timeCorrect
	                			&& ((e.getAttributeValue("wordtype") == null && wdtpe.equals(""))
	                			|| e.getAttributeValue("wordtype").equals(wordtype))) // nur zur Vereinheitlichung
	                	{
	                			int oc = -1;
	                			available = true;
			                	try
			                	{
			                		//adjust occurrence Attribut
			                		if (setOcc)
			                		{			                			
			                			oc = Integer.parseInt(e.getAttributeValue("occurrence"));			                			
			                			e.setAttribute("occurrence", Integer.toString(oc + 1)); 
			                		}

			                		//write morphilo-ObjectID in xml of corpmeta
			                		if (setXlink)
			                		{
			                			Namespace xlinkNamespace = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
				                		MCRObject corpObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(getURLParameter(job, "objID")));
				                		Document corpDoc = corpObj.createXML();
				                		XPathExpression<Element> xpathEx = xpfac.compile("//corpuslink", Filters.element());
				                   	 	Element elm = xpathEx.evaluateFirst(corpDoc);
				                   	 	elm.setAttribute("href" , mcrIDString, xlinkNamespace);
			                		}
			                		mcrObj = new MCRObject(jdomDoc);
			                        MCRMetadataManager.update(mcrObj);
			                        QualityControl qc = new QualityControl(mcrObj);
			                	}
			                	catch(NumberFormatException except)
			                	{
			                		// ignore
			                	}
	                	} // if-Bedingung Zeit Wortart Authorisiert
	                } // for-loop ueber Elemente eines Objekts
            	} //for-loop result.size() ueber alle gefundenen Objekte des solr
 
            if (!available) // if not available in datasets under the given conditions
            {
            	lo.add(corpus.get(i));
            }        
        }//for-loop corpus.size() ueber alle Woerter
    	return lo;
    }
    
    /**
     * 2nd phase of doGetPost This method has a seperate transaction and gets the same MCRServletJob from the first
     * phase (think) and any exception that occurs at the first phase. By default this method calls
     * doGetPost(MCRServletJob) as a fallback to the old behaviour.
     * 
     * @param job
     *            same instance as of think(MCRServlet job)
     * @param ex
     *            any exception thrown by think(MCRServletJob) or transaction commit
     * @throws Exception
     *             if render could not handle <code>ex</code> to produce a nice user page
     **/
    protected void render(MCRServletJob job, Exception ex) throws Exception {
        
    	// no info here how to handle
    	String referer = job.getRequest().getHeader("Referer");
    	// LOGGER.info("Content of Referer: " + referer);
    	job.getResponse().sendRedirect(referer);
    	
        if (ex != null)
            throw ex;
        if (job.getRequest().getMethod().equals("POST")) {
            doPost(job);
        } else {
            doGet(job);
        }
    }
}


