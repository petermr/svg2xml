package org.xmlcml.svgplus.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

public class Validator {

	public void testValidatingParser1(String xmlFilename, String xsdPathname) throws Exception {
//		String SCHEMA_FILENAME = "src/foo/bar/test1.xml";
//		String SCHEMA_PATH = "test1.xsd";
		InputStream SCHEMA_STREAM = new FileInputStream(new File(xsdPathname));
//		InputStream SCHEMA_STREAM = getClass().getResourceAsStream(SCHEMA_PATH);
		StreamSource SCHEMA_SOURCE = new StreamSource(SCHEMA_STREAM);

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(SCHEMA_SOURCE);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		DocumentBuilder builder = factory.newDocumentBuilder();
//		ErrorHandler errorHandler = new DefaultErrorHandler();
//		builder.setErrorHandler(errorHandler);
		builder.parse(new File(xmlFilename));
	}

	public static void main(String[] args) throws Exception {
//		String xmlFilename = "src/foo/bar/test1.xml";
		String xmlFilename = "src/foo/bar/note.xml";
		String xsdPathname = "src/foo/bar/note.xsd";
		new Validator().testValidatingParser1(xmlFilename, xsdPathname);
	}
}
