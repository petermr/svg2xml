package org.xmlcml.svg2xml.text;

import java.util.List;

import org.junit.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;

public class LineChunkTest {

	private static Logger LOG = Logger.getLogger(LineChunkTest.class);
	
	@Test
	public void testLineChunks() {
		TextLine textLine = Fixtures.BERICHT_PAGE6_34_TEXTLINE;
		List<LineChunk> lineChunkList = textLine.getLineChunks();
		Assert.assertEquals("chunks", 9, lineChunkList.size());
		for (int i = 0; i < lineChunkList.size(); i++) {
			LOG.trace(lineChunkList.get(i));
		}
	}

}
