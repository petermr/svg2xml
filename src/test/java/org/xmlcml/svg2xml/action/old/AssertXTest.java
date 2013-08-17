package org.xmlcml.svg2xml.action.old;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.action.AbstractActionX;

/** tests <assert> and <variable> commands
 * 
 * @author pm286
 *
 */
@Ignore
public class AssertXTest {

	@Test
	public void assertTestFromCommand() {
		AbstractActionX semanticDocumentAction = Fixtures.getSemanticDocumentAction(Fixtures.ASSERT_TST);
		semanticDocumentAction.run();
		semanticDocumentAction = Fixtures.getSemanticDocumentAction(Fixtures.NO_ASSERT_TST);
		try {
			semanticDocumentAction.run();
			Assert.fail("should fail assertion");
		} catch (Exception e) {
			Assert.assertEquals("Assert for: (s.foo) expected: nobar; found: (bar)", e.getMessage());
		}
	}
	
}
