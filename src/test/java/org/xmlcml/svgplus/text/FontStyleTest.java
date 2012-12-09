package org.xmlcml.svgplus.text;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.GraphicsElement.FontWeight;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svgplus.Fixtures;
import org.xmlcml.svgplus.analyzer.ChunkAnalyzerX;
import org.xmlcml.svgplus.analyzer.TextAnalyzerX;
import org.xmlcml.svgplus.text.FontStyle.Style;
import org.xmlcml.svgplus.tools.Chunk;

public class FontStyleTest {
	
	private static final Object NORMAL_STYLE = new FontStyle(Style.NORMAL);
	private static final Object BOLD_STYLE = new FontStyle(Style.BOLD);
	private static final Object BOLD_ITALIC_STYLE = new FontStyle(""+Style.BOLD+""+Style.ITALIC);
	private static final Object ITALIC_STYLE = new FontStyle(Style.ITALIC);

	@Test
	public void testFontStyle() {
		FontStyle bold = new FontStyle("bold");
		FontStyle bold1 = new FontStyle("BOLD");
		Assert.assertEquals("bold ", bold, bold1);
		FontStyle normal = new FontStyle("");
		FontStyle normal1 = new FontStyle("normal");
		Assert.assertEquals("normal", normal, normal1);
		Assert.assertTrue("normal", normal.equals(normal1));
		Assert.assertFalse("bold", bold.equals(normal));
	}

	@Test
	public void testFontStyleInText() {
		SVGText text = new SVGText();
		FontStyle fontStyle = FontStyle.getFontStyle(text);
		Assert.assertEquals("normal", NORMAL_STYLE, fontStyle);
		text.setFontWeight(FontWeight.BOLD);
		fontStyle = FontStyle.getFontStyle(text);
		Assert.assertEquals("bold", BOLD_STYLE, fontStyle);
		text.setFontStyle(GraphicsElement.FontStyle.ITALIC);
		fontStyle = FontStyle.getFontStyle(text);
		Assert.assertEquals("bold italic", BOLD_ITALIC_STYLE, fontStyle);
		text.setFontWeight(GraphicsElement.FontWeight.NORMAL);
		fontStyle = FontStyle.getFontStyle(text);
		Assert.assertEquals("italic", ITALIC_STYLE, fontStyle);
	}
	
	@Test
	public void testFontStyleInChunks() {
		List<Chunk> leafChunks = Fixtures.createLeafChunks(Fixtures.FONT_STYLES_PDF, 1);
		Assert.assertEquals("font style count", 7, leafChunks.size());
		for (Chunk chunk : leafChunks) {
			TextLine textLine = getTextFirstLineFromChunk(chunk);
			System.out.println("FS "+textLine.getFontStyleSet().size()+" "+textLine.getFontStyleSet());
		}
//		ChunkAnalyzerX chunkAnalyzer = new ChunkAnalyzerX();
//		chunkAnalyzer.analyzeChunk(leafChunks.get(0));
//		TextAnalyzerX textAnalyzerX = chunkAnalyzer.getTextAnalyzerX();
	}

	@Test
	public void testFontStyleInChunks0() {
		List<Chunk> leafChunks = Fixtures.createLeafChunks(Fixtures.FONT_STYLES_PDF, 1);
		TextLine textLine = getTextFirstLineFromChunk(leafChunks.get(3));
		Set<FontStyle> fontStyleSet = textLine.getFontStyleSet();
	}

	private TextLine getTextFirstLineFromChunk(Chunk chunk) {
		List<TextLine> textLines = getTextLines(chunk);
		TextLine textLine = textLines.size() == 0 ? null : textLines.get(0);
		return textLine;
	}

	private List<TextLine> getTextLines(Chunk chunk) {
		ChunkAnalyzerX chunkAnalyzerX = new ChunkAnalyzerX();
		chunkAnalyzerX.analyzeChunk(chunk);
		TextAnalyzerX textAnalyzerX = chunkAnalyzerX.getTextAnalyzerX();
		List<TextLine> textLines = textAnalyzerX.getLinesInIncreasingY();
		return textLines;
	}
}
