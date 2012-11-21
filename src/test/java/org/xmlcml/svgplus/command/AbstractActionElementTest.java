package org.xmlcml.svgplus.command;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.svgplus.core.SemanticDocumentElement;

public class AbstractActionElementTest {

	File NOOP_FILE = new File("src/main/resources/org/xmlcml/svgplus/core/noop.xml");

	@Test
	public void testCreateActionElementElement() throws Exception {
		Element element = new Builder().build(NOOP_FILE).getRootElement();
		AbstractActionElement abstractActionElement = AbstractActionElement.createActionElement(element);
		Assert.assertNotNull(abstractActionElement);
		Assert.assertTrue(abstractActionElement instanceof SemanticDocumentElement);
//		Assert.assertNotNull(object);
	}
	
	@Test
	public void testCreateActionElementFile() {
		File noopFile = new File("src/mani/resources/org/xmlcml/");
	}
	
	@Test
	public void testCreateActionElementFileWithIncludes() {
	}
}
