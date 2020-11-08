import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class XMLValidator {
	private static final String W3C_XML_SCHEMA_11_NS_URI = "http://www.w3.org/XML/XMLSchema/v1.1";
	private static final String XML_VALIDATION = "-val";
	private static final String ENTITY_DEREFERENCE = "-deref";
	
	private static void validateFile(File xmlFile, File xsdFile) throws SAXException, IOException {
	    // 1. Lookup a factory for the W3C XML Schema language
	    //SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA_11_NS_URI);
	    // 2. Compile the schema.
	    File schemaLocation = xsdFile; 
	    Schema schema = factory.newSchema(schemaLocation);
 
	    // 3. Get a validator from the schema.
	    Validator validator = schema.newValidator();

	    // 4. Parse the document you want to check.
	    Source source = new StreamSource(xmlFile);

	    // 5. Check the document
	    try
	    {
	        validator.validate(source);
	        System.out.println(xmlFile.getName() + " is valid.");
	    }
	    catch (SAXException ex)
	    {
	        System.out.println(xmlFile.getName() + " is not valid because ");
	        System.out.println(ex.getMessage());
	    }
	}
	private static void findXMLFileAndValidate(File dir, File xsdFile) throws IOException, SAXException {
		File[] listOfFiles = dir.listFiles();
		for (File file : listOfFiles) {
			if (file.isDirectory())
				findXMLFileAndValidate(file, xsdFile);
			String filename = file.getName();
			if (file.isFile() && filename.endsWith(".xml")) {
				XMLValidator.validateFile(file, xsdFile);
			}
		}
	}
	
	private static void entityDeReferencing(File xmlFile){
		try{
			
			String newFileName = xmlFile.getAbsolutePath().substring(0, xmlFile.getAbsolutePath().indexOf(".xml"))+"-entity-deref.xml";
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			//doc.getDocumentElement().normalize();
			DOMSource ds = new DOMSource(doc);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
		 
		    StreamResult result = new StreamResult(new File(newFileName));
		    transformer.transform(ds, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private static void findXMLFileAndEntityDeRef(File dir) throws IOException, SAXException {
		File[] listOfFiles = dir.listFiles();
		for (File file : listOfFiles) {
			if (file.isDirectory())
				findXMLFileAndEntityDeRef(file);
			String filename = file.getName();
			if (file.isFile() && filename.endsWith(".xml")) {
				XMLValidator.entityDeReferencing(file);
			}
		}
	}
	
	public static void main(String[] args) { 
		if(args.length < 2){
	        System.out.println("Please provide a valid command line arguments to get better results.");
	        //System.exit(0);
	    }
		boolean actionValidate = false;
		boolean actionEntityDeRef = false;
		
		File xsdFile = null;
		File xmlFile = null;
		
		//boolean actionValidate = true;
		//boolean actionEntityDeRef = true;
		//File xmlFile = new File("H:/alphawallet/tokenscripts/COFI.xml");
		//H:/alphawallet/TokenScript-Examples/examples/Karma/karma.xml
		//File xmlFile = new File("H:/alphawallet/TokenScript-Examples/examples/Karma/karma.xml");
		//File xsdFile = new File("H:/alphawallet/TokenScript/schema/tokenscript.xsd");
				
		Iterator<String> iterator = Arrays.stream(args).iterator();
		while(iterator.hasNext()) {
			String argument = iterator.next();
			if(argument.equals(XML_VALIDATION))
				actionValidate = true;
			if(argument.equals(ENTITY_DEREFERENCE))
				actionEntityDeRef = true;
			if(argument.endsWith(".xsd"))
				xsdFile = new File(argument);
			if(argument.endsWith(".xml") || new File(argument).isDirectory())
				xmlFile = new File(argument);
	    }
		
		if(actionValidate){
			try {
				if(xmlFile.isDirectory()){
					XMLValidator.findXMLFileAndValidate(xmlFile, xsdFile);
				} else {
					XMLValidator.validateFile(xmlFile, xsdFile);
				}
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			} finally{
				System.out.println("Validate action completed");
			}
		}
		
		if(actionEntityDeRef) {	
			try {
				if(xmlFile.isDirectory()){
					findXMLFileAndEntityDeRef(xmlFile);
				} else {
					entityDeReferencing(xmlFile);
				}
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			} finally{
				System.out.println("Entity dereference action completed");
			}
		}
		
	}
}
