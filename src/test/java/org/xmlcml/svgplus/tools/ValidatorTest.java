package org.xmlcml.svgplus.tools;


import org.junit.Test;

public class ValidatorTest {

	@Test
	public void testTest() throws Exception {
		String xmlFilename = "src/test/resources/org/xmlcml/svgplus/tools/test.xml";
		String xsdPathname = "src/test/resources/org/xmlcml/svgplus/tools/test.xsd";
		new Validator().testValidatingParser1(xmlFilename, xsdPathname);
	}
	@Test
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
