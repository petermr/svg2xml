package org.xmlcml.svg2xml.action;

import org.junit.Assert;


import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;

/** tests <assert> and <variable> commands
 * 
 * @author pm286
 *
 */
public class VariableXTest {


	@Test
	public void variableTest() {
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
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
		AbstractActionX semanticDocumentAction = Fixtures.getSemanticDocumentAction(Fixtures.VARIABLE_TST);
	}
}
