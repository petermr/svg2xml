package org.xmlcml.svgplus.control;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.svgplus.core.SVGPlusConverter;

public class SemanticDocumentActionTest {

	@Test
	public void testSetVariable() {
		SemanticDocumentAction semanticDocumentAction = new SemanticDocumentAction();
		semanticDocumentAction.setVariable("fooName", "barValue");
		Object value = semanticDocumentAction.getVariable("fooName");
		Assert.assertNotNull("value should not be null", value);
		Assert.assertTrue("class", value instanceof String);
		Assert.assertEquals("class", "barValue", value);
	}
	
	@Test
	public void testResetVariable() {
		SemanticDocumentAction semanticDocumentAction = new SemanticDocumentAction();
		semanticDocumentAction.setVariable("fooName", "barValue1");
		semanticDocumentAction.setVariable("fooName", "barValue2");
		Object value = semanticDocumentAction.getVariable("fooName");
		Assert.assertNotNull("value should not be null", value);
		Assert.assertEquals("class", "barValue2", value);
	}
	
	@Test
	/** read and set variables
	 */
	public void testVariableMap() {
		SVGPlusConverter converter = new SVGPlusConverter();
		converter.run(
				"-c src/main/resources/org/xmlcml/svgplus/core/noop.xml "+
				"-i src/test/resources/org/xmlcml/svgplus/core/ajc-page6.pdf "
				);
		List<String> variableNames = converter.getSemanticDocumentAction().getVariableNames();
		Assert.assertNotNull("value should not be null", variableNames);
	}

	@Test
	/** read and set variables
	 * read a PDF because current logic requires there to be one
	 */
	public void testInjectVariable() {
		SVGPlusConverter converter = new SVGPlusConverter();
		converter.run(
				"-c src/main/resources/org/xmlcml/svgplus/core/noop.xml "+
				"-i src/test/resources/org/xmlcml/svgplus/core/ajc-page6.pdf "+
			    "-d.dummy dummyValue"
				);
		SemanticDocumentAction semanticDocumentAction = converter.getSemanticDocumentAction();
		List<String> variableNames = semanticDocumentAction.getVariableNames();
		Assert.assertNotNull(variableNames);
		Assert.assertEquals("var names", 3, variableNames.size());
		Assert.assertEquals("sem doc", "src/main/resources/org/xmlcml/svgplus/core/noop.xml", 
				semanticDocumentAction.getVariable(SVGPlusConverter.S_SEMDOC));
		Assert.assertEquals("input file", "src/test/resources/org/xmlcml/svgplus/core/ajc-page6.pdf", 
				semanticDocumentAction.getVariable(SVGPlusConverter.S_INPUTFILE));
		Assert.assertNull("output file",  
				semanticDocumentAction.getVariable(SVGPlusConverter.S_OUTPUTFILE));
		Assert.assertEquals("dummy", "dummyValue", 
				semanticDocumentAction.getVariable("d.dummy"));
		Assert.assertNull("null",  
				semanticDocumentAction.getVariable(null));
	}
}
