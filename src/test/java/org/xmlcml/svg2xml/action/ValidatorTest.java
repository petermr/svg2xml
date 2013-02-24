package org.xmlcml.svg2xml.action;


import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svgplus.tools.Validator;

public class ValidatorTest {

	@Test
	public void testDummy() {
		
	}
	
	@Test
	@Ignore
	public void testTest() throws Exception {
		String xmlFilename = "src/test/resources/org/xmlcml/svgplus/tools/test.xml";
		String xsdPathname = "src/test/resources/org/xmlcml/svgplus/tools/test.xsd";
		new Validator().testValidatingParser1(xmlFilename, xsdPathname);
	}
	
	@Test
	@Ignore
	public void testTest1() {
		String xmlFilename = "src/test/resources/org/xmlcml/svgplus/tools/test1.xml";
		String xsdPathname = "src/test/resources/org/xmlcml/svgplus/tools/test.xsd";
		Validator validator = new Validator();
		try {
			validator.testValidatingParser1(xmlFilename, xsdPathname);
		} catch (Exception e) {
			throw new RuntimeException("Cannot validate", e);
		}
	}
}
