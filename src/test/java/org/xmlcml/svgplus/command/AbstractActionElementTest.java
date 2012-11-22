package org.xmlcml.svgplus.command;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.core.SemanticDocumentElement;
import org.xmlcml.svgplus.document.DocumentIteratorElement;

public class AbstractActionElementTest {


	@Test
	public void testCreateActionElementElement() throws Exception {
		Element element = new Builder().build(Fixtures.NOOP_FILE).getRootElement();
		AbstractActionElement abstractActionElement = AbstractActionElement.createActionElement(element);
		Assert.assertNotNull(abstractActionElement);
		Assert.assertTrue(abstractActionElement instanceof SemanticDocumentElement);
	}
	
	@Test
	public void testCreateDocumentIteratorElement() throws Exception {
		Element element = new Builder().build(Fixtures.NOOP_FILE).getRootElement();
		AbstractActionElement abstractActionElement = AbstractActionElement.createActionElement(element);
		AbstractActionElement firstChildElement = (AbstractActionElement) abstractActionElement.getChildElements().get(0);
		Assert.assertNotNull(firstChildElement);
		Assert.assertTrue(firstChildElement instanceof DocumentIteratorElement);
	}
	
	@Test
	public void testCreateActionElementFile() {
		Element element = SemanticDocumentElement.createSemanticDocument(Fixtures.NOOP_FILE);
		Assert.assertNotNull(element);
		Assert.assertTrue(element instanceof SemanticDocumentElement);
	}
	
	@Test
	public void testCreateActionElementAction() {
		SemanticDocumentElement semanticDocumentElement = SemanticDocumentElement.createSemanticDocument(Fixtures.NOOP_FILE);
		AbstractAction abstractAction = semanticDocumentElement.getAction();
		Assert.assertNotNull(abstractAction);
		Assert.assertTrue(abstractAction instanceof SemanticDocumentAction);
		SemanticDocumentAction semanticDocumentAction = abstractAction.getSemanticDocumentAction();
		Assert.assertNotNull(semanticDocumentAction);
		Assert.assertTrue(semanticDocumentAction instanceof SemanticDocumentAction);
	}
	
	@Test
	public void testDescendantsSemanticDocument() {
		SemanticDocumentElement semanticDocumentElement = SemanticDocumentElement.createSemanticDocument(Fixtures.NOOP_FILE);
		AbstractActionElement childElement = (AbstractActionElement) semanticDocumentElement.getChildElements().get(0);
		AbstractAction childAction = childElement.getAction();
		Assert.assertNotNull(childAction);
		AbstractAction childSemanticDocumentAction = childAction.getSemanticDocumentAction();
		Assert.assertNotNull(childSemanticDocumentAction);
	}
	
	@Test
	public void testCreateActionElementFileWithIncludes() {
		SemanticDocumentElement semanticDocumentElement = SemanticDocumentElement.createSemanticDocument(Fixtures.INCLUDE_TEST_FILE);
		AbstractAction abstractAction = semanticDocumentElement.getAction();
		Assert.assertNotNull(abstractAction);
		Assert.assertTrue(abstractAction instanceof SemanticDocumentAction);
		SemanticDocumentAction semanticDocumentAction = abstractAction.getSemanticDocumentAction();
		Assert.assertNotNull(semanticDocumentAction);
		Assert.assertTrue(semanticDocumentAction instanceof SemanticDocumentAction);
	}
}
