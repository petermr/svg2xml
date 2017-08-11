package org.xmlcml.svg2xml.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.junit.Test;

public class XMLDeclTest {
	
	private static final Logger LOG = Logger.getLogger(XMLDeclTest.class);

	@Test
	public void testXMLDeclarationInCDATA() throws Exception {
		Element e = new Builder().build(new FileInputStream("src/test/resources/declaration.xml")).getRootElement();
	}

}
