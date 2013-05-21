package org.xmlcml.svg2xml.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.junit.Test;

public class XMLDeclTest {
	
	@Test
	public void testXMLDeclarationInCDATA() {
		
		try {
			Element e = new Builder().build(new FileInputStream("src/test/resources/declaration.xml")).getRootElement();
			for (int i = 0; i < e.getChildCount(); i++) {
				Node child = e.getChild(i);				
				System.out.println(child+ " "+child.getValue());
			}
			
		} catch (ValidityException e) {
			System.out.println("invalid");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("FNF");
			e.printStackTrace();
		} catch (ParsingException e) {
			System.out.println("Parsing");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO");
			e.printStackTrace();
		}
	}

}
