package org.xmlcml.svg2xml.text;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.GraphicsElement.FontWeight;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.old.ChunkAnalyzerX;
import org.xmlcml.svg2xml.old.FontStyleOld;
import org.xmlcml.svg2xml.old.FontStyleOld.Style;
import org.xmlcml.svg2xml.page.TextAnalyzer;
import org.xmlcml.svg2xml.tools.Chunk;

public class FontStyleTest {

	private final static Logger LOG = Logger.getLogger(FontStyleTest.class);
	
	private static final Object NORMAL_STYLE = new FontStyleOld(Style.NORMAL);
	private static final Object BOLD_STYLE = new FontStyleOld(Style.BOLD);
	private static final Object BOLD_ITALIC_STYLE = new FontStyleOld(String.valueOf(Style.BOLD)+String.valueOf(Style.ITALIC));
	private static final Object ITALIC_STYLE = new FontStyleOld(Style.ITALIC);

	@Test
	public void testFontStyle() {
		FontStyleOld bold = new FontStyleOld("bold");
		FontStyleOld bold1 = new FontStyleOld("BOLD");
		Assert.assertEquals("bold ", bold, bold1);
		FontStyleOld normal = new FontStyleOld("");
		FontStyleOld normal1 = new FontStyleOld("normal");
		Assert.assertEquals("normal", normal, normal1);
		Assert.assertTrue("normal", normal.equals(normal1));
		Assert.assertFalse("bold", bold.equals(normal));
	}

	@Test
	public void testFontStyleInText() {
		SVGText text = new SVGText();
		FontStyleOld fontStyle = FontStyleOld.getFontStyle(text);
		Assert.assertEquals("normal", NORMAL_STYLE, fontStyle);
		text.setFontWeight(FontWeight.BOLD);
		fontStyle = FontStyleOld.getFontStyle(text);
		Assert.assertEquals("bold", BOLD_STYLE, fontStyle);
		text.setFontStyle(GraphicsElement.FontStyle.ITALIC);
		fontStyle = FontStyleOld.getFontStyle(text);
		Assert.assertEquals("bold italic", BOLD_ITALIC_STYLE, fontStyle);
		text.setFontWeight(GraphicsElement.FontWeight.NORMAL);
		fontStyle = FontStyleOld.getFontStyle(text);
		Assert.assertEquals("italic", ITALIC_STYLE, fontStyle);
	}

//	@Test
//	public void testFontStyleInChunks() {
//		List<Chunk> leafChunks = Fixtures.createLeafChunks(Fixtures.FONT_STYLES_PDF, 1);
//		Assert.assertEquals("font style count", 7, leafChunks.size());
//		for (Chunk chunk : leafChunks) {
//			TextLine textLine = getTextFirstLineFromChunk(chunk);
//			LOG.trace("FS "+textLine.getFontStyleSet().size()+" "+textLine.getFontStyleSet());
//		}
////		ChunkAnalyzerX chunkAnalyzer = new ChunkAnalyzerX();
////		chunkAnalyzer.analyzeChunk(leafChunks.get(0));
////		TextAnalyzerX textAnalyzerX = chunkAnalyzer.getTextAnalyzerX();
//	}

//	@Test
//	public void testFontStyleInChunks0() {
//		List<Chunk> leafChunks = Fixtures.createLeafChunks(Fixtures.FONT_STYLES_PDF, 1);
//		TextLine textLine = getTextFirstLineFromChunk(leafChunks.get(3));
//		Set<FontStyleOld> fontStyleSet = textLine.getFontStyleSet();
//	}
//
//	private TextLine getTextFirstLineFromChunk(Chunk chunk) {
//		List<TextLine> textLines = getTextLines(chunk);
//		TextLine textLine = textLines.size() == 0 ? null : textLines.get(0);
//		return textLine;
//	}
//
//	private List<TextLine> getTextLines(Chunk chunk) {
//		ChunkAnalyzerX chunkAnalyzerX = new ChunkAnalyzerX();
//		chunkAnalyzerX.analyzeChunk(chunk);
//		TextAnalyzerX textAnalyzerX = chunkAnalyzerX.getTextAnalyzerX();
//		List<TextLine> textLines = textAnalyzerX.getLinesInIncreasingY();
//		return textLines;
//	}
}
