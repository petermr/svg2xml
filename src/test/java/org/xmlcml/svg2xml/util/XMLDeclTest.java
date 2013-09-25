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
	public void testXMLDeclarationInCDATA() {
		
		try {
			Element e = new Builder().build(new FileInputStream("src/test/resources/declaration.xml")).getRootElement();
			for (int i = 0; i < e.getChildCount(); i++) {
				Node child = e.getChild(i);				
//				LOG.debug(child+ " "+child.getValue());
			}
			
		} catch (ValidityException e) {
			LOG.debug("invalid");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			LOG.debug("FNF");
			e.printStackTrace();
		} catch (ParsingException e) {
			LOG.debug("Parsing");
			e.printStackTrace();
		} catch (IOException e) {
			LOG.debug("IO");
			e.printStackTrace();
		}
	}

}
