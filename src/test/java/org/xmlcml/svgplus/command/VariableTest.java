package org.xmlcml.svgplus.command;

import org.junit.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.core.SemanticDocumentAction;

/** tests <assert> and <variable> commands
 * 
 * @author pm286
 *
 */
public class VariableTest {


	@Test
	public void variableTest() {
		SemanticDocumentAction semanticDocumentAction = new SemanticDocumentAction();
		semanticDocumentAction.setVariable("s.foo", "bar");
		Assert.assertEquals("get variable", "bar", semanticDocumentAction.getVariable("s.foo"));
		semanticDocumentAction.setVariable("s.foo", "boo");
		Assert.assertEquals("get variable", "boo", semanticDocumentAction.getVariable("s.foo"));
		semanticDocumentAction.setVariable("s.foo", null);
		Object obj =  semanticDocumentAction.getVariable("s.foo");
		Assert.assertNull("variable should be null", obj);
	}
	
	@Test
	@Ignore
	public void variableTestFromCommand() {
		SemanticDocumentAction semanticDocumentAction = Fixtures.getSemanticDocumentAction(Fixtures.VARIABLE_TST);
	}
}
