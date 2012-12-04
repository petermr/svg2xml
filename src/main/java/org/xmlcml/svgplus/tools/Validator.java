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

import org.apache.xml.utils.DefaultErrorHandler;
import org.xml.sax.ErrorHandler;

public class Validator {

	public void testValidatingParser1(String xmlFilename, String xsdPathname) throws Exception {
//		String SCHEMA_FILENAME = "src/foo/bar/test1.xml";
//		String SCHEMA_PATH = "test1.xsd";
		InputStream schemaStream = new FileInputStream(new File(xsdPathname));
//		InputStream SCHEMA_STREAM = getClass().getResourceAsStream(SCHEMA_PATH);
		StreamSource SCHEMA_SOURCE = new StreamSource(schemaStream);

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(SCHEMA_SOURCE);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		factory.setValidating(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		ErrorHandler errorHandler = new DefaultErrorHandler();
		builder.setErrorHandler(errorHandler);
		builder.parse(new File(xmlFilename));
	}

}
