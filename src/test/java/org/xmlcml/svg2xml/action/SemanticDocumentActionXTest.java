package org.xmlcml.svg2xml.action;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.TestUtils;

public class SemanticDocumentActionXTest {

	private final static Logger LOG = Logger.getLogger(SemanticDocumentActionXTest.class);
	
	@Test
	public void testSetVariable() {
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		semanticDocumentAction.setVariable("s.fooName", "barValue");
		Object value = semanticDocumentAction.getVariable("s.fooName");
		Assert.assertNotNull("value should not be null", value);
		Assert.assertTrue("class", value instanceof String);
		Assert.assertEquals("class", "barValue", value);
	}
	
	@Test
	public void testSetVariableWithIncorrectName() {
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		try {
			semanticDocumentAction.setVariable("fooName", "barValue");
			Assert.fail("Should throw exception for bad name");
		} catch (Exception e) {
			Assert.assertEquals("Bad variable name: fooName", e.getMessage());
		}
	}
	
	@Test
	public void testResetVariable() {
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		semanticDocumentAction.setVariable("d.fooName", "barValue1");
		semanticDocumentAction.setVariable("d.fooName", "barValue2");
		Object value = semanticDocumentAction.getVariable("d.fooName");
		Assert.assertNotNull("value should not be null", value);
		Assert.assertEquals("class", "barValue2", value);
	}
	
	@Test
	/** read and set variables
	 */
	//@Ignore // FIXME semanticDocumentAction null
	public void testVariableMap() {
		SVGPlusConverterX converter = new SVGPlusConverterX();
		converter.run(
				" -c "+Fixtures.NOOP_FILE+
				" -i "+Fixtures.AJC_PAGE6_PDF
				);
		List<String> variableNames = converter.getSemanticDocumentAction().getVariableNames();
		Assert.assertNotNull("value should not be null", variableNames);
	}

	@Test
	/** read and set variables
	 * read a PDF because current logic requires there to be one
	 */
	//@Ignore // FIXME semanticDocumentAction null
	public void testInjectVariable() {
		SVGPlusConverterX converter = new SVGPlusConverterX();
		converter.run(
				" -c "+ Fixtures.NOOP_FILE +
				" -i "+ Fixtures.AJC_PAGE6_PDF +
			    " -d.dummy dummyValue"
				);
		SemanticDocumentActionX semanticDocumentAction = converter.getSemanticDocumentAction();
		List<String> variableNames = semanticDocumentAction.getVariableNames();
		Assert.assertNotNull(variableNames);
		for (String name : variableNames) {
			LOG.debug("name: "+name);
		}
		Assert.assertTrue("var names", variableNames.size() > 3);
		Assert.assertTrue("sem doc", TestUtils.fileEquals(Fixtures.NOOP_FILE, 
				(String) semanticDocumentAction.getVariable(SemanticDocumentActionX.S_SEMDOC)));
		Object fileObj = semanticDocumentAction.getVariable(SemanticDocumentActionX.S_INFILE);
		Assert.assertNotNull("file ", fileObj);
		Assert.assertTrue("file ", fileObj instanceof File);
		Assert.assertEquals("input file", new File(Fixtures.AJC_PAGE6_PDF).getPath(), 
				((File) fileObj).getPath());
		Assert.assertNull("output file",  
				semanticDocumentAction.getVariable(SemanticDocumentActionX.S_OUTFILE));
		Assert.assertEquals("dummy", "dummyValue", 
				semanticDocumentAction.getVariable("d.dummy"));
		Assert.assertNull("null",  
				semanticDocumentAction.getVariable(null));
	}
}
