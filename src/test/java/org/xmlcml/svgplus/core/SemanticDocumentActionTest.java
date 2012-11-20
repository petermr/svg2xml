package org.xmlcml.svgplus.core;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class SemanticDocumentActionTest {

	private final static Logger LOG = Logger.getLogger(SemanticDocumentActionTest.class);
	@Test
	public void testSetVariable() {
		SemanticDocumentAction semanticDocumentAction = new SemanticDocumentAction();
		semanticDocumentAction.setVariable("s.fooName", "barValue");
		Object value = semanticDocumentAction.getVariable("s.fooName");
		Assert.assertNotNull("value should not be null", value);
		Assert.assertTrue("class", value instanceof String);
		Assert.assertEquals("class", "barValue", value);
	}
	
	@Test
	public void testSetVariableWithIncorrectName() {
		SemanticDocumentAction semanticDocumentAction = new SemanticDocumentAction();
		semanticDocumentAction.setVariable("fooName", "barValue");
		Object value = semanticDocumentAction.getVariable("fooName");
		Assert.assertNull("value should be null", value);
	}
	
	@Test
	public void testResetVariable() {
		SemanticDocumentAction semanticDocumentAction = new SemanticDocumentAction();
		semanticDocumentAction.setVariable("d.fooName", "barValue1");
		semanticDocumentAction.setVariable("d.fooName", "barValue2");
		Object value = semanticDocumentAction.getVariable("d.fooName");
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
		for (String name : variableNames) {
			LOG.debug("name: "+name);
		}
		Assert.assertEquals("var names", 3, variableNames.size());
		Assert.assertEquals("sem doc", "src/main/resources/org/xmlcml/svgplus/core/noop.xml", 
				semanticDocumentAction.getVariable(SemanticDocumentAction.S_SEMDOC));
		Object fileObj = semanticDocumentAction.getVariable(SemanticDocumentAction.S_INFILE);
		Assert.assertNotNull("file ", fileObj);
		Assert.assertTrue("file ", fileObj instanceof File);
		Assert.assertEquals("input file", new File("src/test/resources/org/xmlcml/svgplus/core/ajc-page6.pdf").getPath(), 
				((File) fileObj).getPath());
		Assert.assertNull("output file",  
				semanticDocumentAction.getVariable(SemanticDocumentAction.S_OUTFILE));
		Assert.assertEquals("dummy", "dummyValue", 
				semanticDocumentAction.getVariable("d.dummy"));
		Assert.assertNull("null",  
				semanticDocumentAction.getVariable(null));
	}
}
