package custom.mycore.addons.morphilo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.jdom2.JDOMException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Attribute;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.filter.Filters;
import org.jdom2.Namespace;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JDOMorphilo extends MCRServlet
{
	
	private static Logger LOGGER = LogManager.getLogger();
	
	public MCRObject createMorphiloObject(MCRServletJob job, String word) throws Exception
	{
		MCRFrontendUtil prop = new MCRFrontendUtil();
		String corpID = prop.getProperty(job.getRequest(), "objID").get();
		String derivID = prop.getProperty(job.getRequest(), "id").get();
		AffixStripper afs = new AffixStripper(word);
		ProcessCorpusServlet pcs = new ProcessCorpusServlet();
		String datefrom = pcs.getCorpusMetadata(job, "def.datefrom"); 
		String dateuntil = pcs.getCorpusMetadata(job, "def.dateuntil");
		String corpusname = pcs.getCorpusMetadata(job, "def.korpusname");
		
		//Create Morphilo-MyCoRe-Object
		Namespace xlinkNamespace = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
		Namespace xmlNS = Namespace.getNamespace("xmlns", "http://www.w3.org/2000/xmlns/");
		Namespace xsiNS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		Document mycoreObject = new Document();
		Element wzl = new Element("mycoreobject");
		Element strct = new Element("structure");
		Element mtd = new Element("metadata");
		Element srv = new Element("service");
		Element morConDef = new Element("def.morphiloContainer");
		Element morCon = new Element("morphiloContainer");
		Element mor = new Element("morphilo");
		Element w = new Element("w");
		Element m1 = new Element("m1");
		Element m2 = new Element("m2");
		Element m3 = new Element("m3");
		Element m4 = new Element("m4");
		Element m5 = new Element("m5");
		
		Element cpslinkdef = new Element("def.corpuslink");
		Element cpslink = new Element("corpuslink");
		
		cpslinkdef.setAttribute(new Attribute("class", "MCRMetaLinkID"));
		cpslinkdef.setAttribute(new Attribute("heritable", "false"));
		cpslinkdef.setAttribute(new Attribute("notinherit", "true"));
		cpslink.setAttribute(new Attribute("inherited", "0"));
		cpslink.setAttribute(new Attribute("type", "locator", xlinkNamespace));
		cpslink.setAttribute(new Attribute("href", corpID, xlinkNamespace));
		wzl.addContent(strct);
		wzl.addContent(mtd);
		wzl.setAttribute(new Attribute("ID", "skeleton_morphilo_00000000"));
		wzl.setAttribute(new Attribute("xlink","http://www.w3.org/1999/xlink",xmlNS));
		wzl.setAttribute(new Attribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", xmlNS));
		wzl.setAttribute(new Attribute("noNamespaceSchemaLocation","datamodel-morphilo.xsd",xsiNS));
		wzl.setAttribute(new Attribute("label", derivID));
		wzl.setAttribute(new Attribute("version","2016.06.0.1-SNAPSHOT"));
		mtd.addContent(cpslinkdef);
		cpslinkdef.addContent(cpslink);
		mtd.addContent(morConDef);
		morConDef.addContent(morCon);
		morConDef.setAttribute(new Attribute("class", "MCRMetaXML"));
		morConDef.setAttribute(new Attribute("heritable", "false"));
		morConDef.setAttribute(new Attribute("notinherit", "true"));
		morCon.addContent(mor);
		morCon.setAttribute(new Attribute("inherited", "0"));
		mor.addContent(w);
		w.setAttribute(new Attribute("wordtype", afs.getWordClass()));
		w.setAttribute(new Attribute("lemma", afs.getLemma()));
		//occurrence can only be 1 since it is the first time this word is encountered
		w.setAttribute(new Attribute("occurrence", "1"));
		w.setAttribute(new Attribute("corpus", corpusname));
		w.setAttribute(new Attribute("begin", datefrom));
		w.setAttribute(new Attribute("end", dateuntil));
		w.addContent(m1);
		w.addContent(word);
		m1.addContent(m2);
		m1.setAttribute(new Attribute("type", "stem"));
		m1.setAttribute(new Attribute("pos", ""));
		m2.addContent(m3);
		m2.setAttribute(new Attribute("type", "base"));
		m3.setAttribute(new Attribute("type", "root"));
		//TODO: für den Fall mehrerer Wurzeln = Komposita anpassen
		m3.addContent(afs.getRoot().get(0));
		//the number of additional m-Elements depends on the number of identified prefixes and suffixes
		Map<String, Integer> prefixMorpheme = new HashMap<String, Integer>();
		prefixMorpheme = afs.getPrefixMorphem();
		int i = 0;
		for (String key : prefixMorpheme.keySet())
		{
			i++;
			Element prefix = new Element("m4");
			prefix.setAttribute(new Attribute("type", "prefix"));
			prefix.setAttribute(new Attribute("position", prefixMorpheme.get(key).toString()));
			prefix.setAttribute(new Attribute("PrefixbaseForm", key));
			prefix.setText(key);
			m2.addContent(prefix);
		}
		Map<String, Integer> suffixMorpheme = new HashMap<String, Integer>();
		suffixMorpheme = afs.getSuffixMorphem();
		for (String key : suffixMorpheme.keySet())
		{
			Element suffix = new Element("m5");
			suffix.setAttribute(new Attribute("type", "suffix"));
			suffix.setAttribute(new Attribute("position", suffixMorpheme.get(key).toString()));
			suffix.setAttribute(new Attribute("SuffixbaseForm", key));
			suffix.setAttribute(new Attribute("inflection", "no"));
			suffix.setText(key);
			m1.addContent(suffix);
		}
		//inflection must be added after the last suffix
		if (!afs.getInflection().isEmpty())
		{
			Element infl = new Element("m5");
			infl.setAttribute(new Attribute("type", "suffix"));
			infl.setAttribute(new Attribute("position", ""+(suffixMorpheme.size() + 1)));
			infl.setAttribute(new Attribute("SuffixbaseForm", afs.getInflection()));
			infl.setAttribute(new Attribute("inflection", "yes"));
			infl.setText(afs.getInflection());
			m1.addContent(infl);
		}		
		wzl.addContent(srv);
		mycoreObject.setContent(wzl);
		
		//outputs jdom for debug purposes
		try
		{
			FileWriter writer = new FileWriter("/home/h-gen/Documents/Projekte/morphilo/mycoreObject.xml");
	        XMLOutputter outputter = new XMLOutputter();
	        outputter.setFormat(Format.getPrettyFormat());
	        outputter.output(mycoreObject, writer);
	        outputter.output(mycoreObject, System.out);
	        writer.close(); // close writer
		}
		catch (IOException io) 
		{
			LOGGER.info(io.getMessage());
	    }
		
		MCRObjectID oid = MCRObjectID.getNextFreeId("skeleton_morphilo");
	    MCRObject obj = new MCRObject(mycoreObject);
	    obj.setId(oid);
	    MCRMetadataManager.create(obj);
	    
	    return obj;
	}
}
