package org.xmlcml.svg2xml.action;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;

public class AbstractActionElementXTest {


	@Test
	public void testCreateActionElementElement() throws Exception {
		Element element = new Builder().build(Fixtures.NOOP_FILE).getRootElement();
		AbstractActionX abstractActionElement = AbstractActionX.createActionX(element);
		Assert.assertNotNull(abstractActionElement);
		Assert.assertTrue(abstractActionElement instanceof SemanticDocumentActionX);
	}
	
	@Test
	public void testCreateDocumentIteratorElement() throws Exception {
		Element element = new Builder().build(Fixtures.NOOP_FILE).getRootElement();
		AbstractActionX abstractActionX = AbstractActionX.createActionX(element);
		AbstractActionX firstChildElement = (AbstractActionX) abstractActionX.getChildElements().get(0);
		Assert.assertNotNull(firstChildElement);
		Assert.assertTrue(firstChildElement instanceof DocumentIteratorActionX);
	}
	
	@Test
	public void testCreateActionElementFile() {
		Element element = SemanticDocumentActionX.createSemanticDocument(Fixtures.NOOP_FILE);
		Assert.assertNotNull(element);
		Assert.assertTrue(element instanceof SemanticDocumentActionX);
	}
	
	@Test
	public void testDescendantsSemanticDocument() {
		SemanticDocumentActionX semanticDocumentElement = SemanticDocumentActionX.createSemanticDocument(Fixtures.NOOP_FILE);
		AbstractActionX childElement = (AbstractActionX) semanticDocumentElement.getChildElements().get(0);
		AbstractActionX childSemanticDocumentAction = childElement.getSemanticDocumentActionX();
		Assert.assertNotNull(childSemanticDocumentAction);
	}
	
	@Test
	public void testCreateActionElementFileWithIncludes() {
		SemanticDocumentActionX semanticDocumentElement = SemanticDocumentActionX.createSemanticDocument(Fixtures.INCLUDE_TEST_FILE);
		Assert.assertNotNull(semanticDocumentElement);
	}
	
	@Test
	public void testInputFileOnDocumentIteratorWithAssert() {
		SVGPlusConverterX converter = new SVGPlusConverterX();
		converter.run(
				" -o target "+
				" -c "+Fixtures.INFILE_TEST
				);
	}
	
	
}
