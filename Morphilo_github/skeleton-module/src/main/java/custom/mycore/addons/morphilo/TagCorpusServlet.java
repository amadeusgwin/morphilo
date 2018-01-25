package custom.mycore.addons.morphilo;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.io.FileWriter;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Optional;

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
import org.jdom2.Attribute;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.filter.Filters;
import org.jdom2.Namespace;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;
import org.mycore.access.MCRAccessException;
import org.mycore.common.content.MCRFileContent;
import org.mycore.common.content.MCRStringContent;
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
import org.mycore.common.xml.MCRXMLFunctions;


public class TagCorpusServlet extends MCRServlet {
	
	private static final long serialVersionUID = 3148314720092349972L;
	private static Logger LOGGER = LogManager.getLogger();
	private static String tagUrl = "";
	private MCRServletJob job;
	
	@Override
    public void doGetPost(MCRServletJob job) throws Exception
    {
		
    }
	
	/*
	 * 
	 */
	protected void think(MCRServletJob job) throws Exception 
    {
		this.job = job;
		MCRFrontendUtil prop = new MCRFrontendUtil();
		String corpID = prop.getProperty(job.getRequest(), "objID").get();
		String derivID = prop.getProperty(job.getRequest(), "id").get();
		MCRPath root = MCRPath.getPath(derivID, "/");
		
		ProcessCorpusServlet pcs = new ProcessCorpusServlet();
		ArrayList<String> leftovers = new ArrayList<String>();	
		leftovers.clear();
		// check if word was really written to dataset
		leftovers = pcs.getUnknownWords(
				pcs.getContentFromFile(job, "untagged"), 
				pcs.getCorpusMetadata(job, "def.datefrom"), 
				pcs.getCorpusMetadata(job, "def.dateuntil"),
				"",
				false, 
				false,
				false);
		
		//recreate file with new untagged words
        Path fn = pcs.getDerivateFilePath(job, "untagged").getFileName();
        Path p = root.resolve(fn);
        Files.delete(p);
        //Path fn2 = pcs.getDerivateFilePath(job, "untagged").getFileName();
        Path b = root.resolve(fn);
        Files.write(b, leftovers);
		
		//build jdom to call publish.xed mask
    	if (!leftovers.isEmpty())
    	{
    		ArrayList<String> processed = new ArrayList<String>();
    		//processed.add(leftovers.get(0));
    		JDOMorphilo jdm = new JDOMorphilo();
    		MCRObject obj = jdm.createMorphiloObject(job, leftovers.get(0));   		
    		//write word to be annotated in process list and save it
    		Path filePathProc = pcs.getDerivateFilePath(job, "processed").getFileName();
    		Path proc = root.resolve(filePathProc);
    		processed = pcs.getContentFromFile(job, "processed");
    		processed.add(leftovers.get(0));
    		Files.write(proc, processed);
    		
    		//call entry mask for next word
    		tagUrl = prop.getBaseURL() + "content/publish/morphilo.xed?id=" + obj.getId();
    		
    	}
    	else
    	{
    		//initiate process to give a complete tagged file of the original corpus
    		//if untagged-file is empty, match original file with morphilo 
    		//creator=administrator OR creator=username and write matches in a new file
    		ArrayList<String> complete = new ArrayList<String>();
    		ProcessCorpusServlet pcs2 = new ProcessCorpusServlet();
    		complete = pcs2.getUnknownWords(
    				pcs2.getContentFromFile(job, ""), //main corpus file
    				pcs2.getCorpusMetadata(job, "def.datefrom"),
    				pcs2.getCorpusMetadata(job, "def.dateuntil"),
    				"", //wordtype
    				false,
    				false,
    				true);

    		Files.delete(p);
    		//Path newFileName = pcs2.getDerivateFilePath(job, "").getFileName();
    		MCRXMLFunctions mdm = new MCRXMLFunctions();
    		String mainFile = mdm.getMainDocName(derivID);
        	Path newRoot = root.resolve("tagged-" + mainFile);
            Files.write(newRoot, complete);
            	
    		//return to Menu page
    		tagUrl = prop.getBaseURL() + "receive/" + corpID;
    	}
    }
	
protected void render(MCRServletJob job, Exception ex) throws Exception 
	{
    	job.getResponse().sendRedirect(tagUrl); //call data mask for manual tagging
    	
        if (ex != null)
            throw ex;
        if (job.getRequest().getMethod().equals("POST")) {
            doPost(job);
        } else {
            doGet(job);
        }
    }
}
