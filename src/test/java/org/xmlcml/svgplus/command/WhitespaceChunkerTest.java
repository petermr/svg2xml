package org.xmlcml.svgplus.command;

import org.junit.Test;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.core.SVGPlusConverter;

public class WhitespaceChunkerTest {

	@Test
	public void whitespaceChunkerTest() {
		SVGPlusConverter converter = new SVGPlusConverter();
		converter.run(
				"-c "+Fixtures.WHITESPACE_CHUNKER_TST);
	}

}
