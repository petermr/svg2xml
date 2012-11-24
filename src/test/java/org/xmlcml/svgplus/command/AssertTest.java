package org.xmlcml.svgplus.command;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.core.SemanticDocumentAction;

/** tests <assert> and <variable> commands
 * 
 * @author pm286
 *
 */
public class AssertTest {

	@Test
	public void assertTestFromCommand() {
		SemanticDocumentAction semanticDocumentAction = Fixtures.getSemanticDocumentAction(Fixtures.ASSERT_TST);
		semanticDocumentAction.run();
		semanticDocumentAction = Fixtures.getSemanticDocumentAction(Fixtures.NO_ASSERT_TST);
		try {
			semanticDocumentAction.run();
			Assert.fail("should fail assertion");
		} catch (Exception e) {
			Assert.assertEquals("Assert for: (s.foo) expected: nobar; found: bar", e.getMessage());
		}
	}
	
}
