package org.xmlcml.svg2xml.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.util.SVG2XMLConstantsX;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.Multiset;

public class TextStructurerTest {

	private final static Logger LOG = Logger.getLogger(TextStructurerTest.class);
	
	private static final String GEOTABLE_7 = "geotable-7.";
	private List<File> geoFileList;
	
	@Before
	public void setup() {
		File bmcDir = new File(Fixtures.BMC_DIR);
		Assert.assertTrue(bmcDir.exists());
		File[] files = bmcDir.listFiles();
		Assert.assertTrue(files.length > 0);
		geoFileList = new ArrayList<File>();
		for (File file : files) {
			String name = file.getName();
			if (name.startsWith(GEOTABLE_7) && name.endsWith(SVG2XMLConstantsX.DOT_SVG) &&
					!name.equals(GEOTABLE_7+"svg")) {
				geoFileList.add(file);
			}
		}
		Assert.assertTrue(geoFileList.size() == 7);
	}
	
	@Test
	public void testMultilineFonts() {
		TextStructurer textContainer = TextStructurer.createTextStructurer(Fixtures.PARA_SUSCRIPT_SVG);
		Multiset<String> fontFamilyMultiset = textContainer.getFontFamilyMultiset();
		Assert.assertEquals("font occurrences", 523, fontFamilyMultiset.size());
		Set<String> entrySet = fontFamilyMultiset.elementSet();
		Assert.assertEquals("different fonts", 3, entrySet.size());
		Assert.assertEquals("Times-Roman", 3, fontFamilyMultiset.count("Times-Roman"));
		Assert.assertEquals("MTSYN", 7, fontFamilyMultiset.count("MTSYN"));
		Assert.assertEquals("TimesNewRoman", 513, fontFamilyMultiset.count("TimesNewRoman"));
	}
	
	@Test
	public void testMultilineCommonestFontFamily() {
		TextStructurer textContainer = TextStructurer.createTextStructurer(Fixtures.PARA_SUSCRIPT_SVG);
		Assert.assertEquals("commonest fontfamily", "TimesNewRoman", textContainer.getCommonestFontFamily());
	}
	
	@Test
	public void testReadBMCGeotable() {
		Assert.assertEquals(7, geoFileList.size());
	}
	
	@Test
	public void testReadBMCGeotableContainers() {
		for (File geoFile : geoFileList) {
			TextStructurer container = TextStructurer.createTextStructurer(geoFile);
		}
	}
	
	@Test
	@Ignore
	public void testCommonestBMCGeotableFontSizes() {
		double[] sizes = {7.97, 7.97, 9.76, 10.26, 9.76, 10.26, 9.76};
		int i = 0;
		for (File geoFile : geoFileList) {
			TextStructurer container = TextStructurer.createTextStructurer(geoFile);
			TextCoordinate size = container.getCommonestFontSize();
			Assert.assertEquals("file"+i, sizes[i], size.getDouble(), 0.001);
			i++;
		}
	}
	
	@Test
	@Ignore
	public void testCommonestBMCGeotableFontFamilies() {
		String[] family = {"AdvOT46dcae81", "AdvOT46dcae81", "AdvOTa9103878", "AdvOTa9103878", 
				           "AdvOTa9103878", "AdvOTa9103878", "AdvOTa9103878"};
		int i = 0;
		for (File geoFile : geoFileList) {
			TextStructurer container = TextStructurer.createTextStructurer(geoFile);
			String fontFamily = container.getCommonestFontFamily();
			Assert.assertEquals("file"+i, family[i], fontFamily);
			i++;
		}
	}
	
	@Test
	@Ignore
	public void testBMCGeotableFontFamilyDiversity() {
		int[] nfont = {3, 1, 3, 3, 1, 5, 3};
		int i = 0;
		for (File geoFile : geoFileList) {
			TextStructurer container = TextStructurer.createTextStructurer(geoFile);
			Assert.assertEquals("file"+i, nfont[i], container.getFontFamilyCount());
			i++;
		}
	}
	
	@Test
	public void testBMCGeotableTextLines() {
		File geoFile2 = geoFileList.get(2);
		TextStructurer container = TextStructurer.createTextStructurer(geoFile2);
	}
	
	@Test
	@Ignore // fails
	public void testFullTables() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(
						Fixtures.BERICHT_PAGE6_SVG, (PageAnalyzer) null);
		List<TabbedTextLine> tabbedTextLineList = textStructurer.createTabbedLineList();
//		Assert.assertNotNull(tabbedTextLineList);
//		for (int i = 0; i < tabbedTextLineList.size(); i++) {
//			System.out.println(">"+i+"> "+tabbedTextLineList.get(i));
//		}

	}

	@Test
	@Ignore
	public void testWordListCollection() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(
						Fixtures.BERICHT_PAGE6_SVG, (PageAnalyzer) null);
		List<TextLine> textLineList = textStructurer.getLinesInIncreasingY();
		for (int i = 0; i < textLineList.size(); i++) {
			System.out.println(">"+i+"> "+textLineList.get(i));
		}

	}

	@Test
	public void testHOText() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(
						Fixtures.IMAGE_2_11_HO_SVG, (PageAnalyzer) null);
		List<RawWords> wordList = textStructurer.createRawWordsList();
		Assert.assertEquals("ho", "{(HO)}", wordList.get(0).toString());
	}
	
	@Test
	public void testSubscriptedText() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(
						Fixtures.IMAGE_2_11_NO2_SVG, (PageAnalyzer) null);
		List<RawWords> wordList = textStructurer.createRawWordsList();
		Assert.assertEquals("no2", 2, wordList.size());
		Assert.assertEquals("no", "{(NO)}", wordList.get(0).toString());
		Assert.assertEquals("xy", "(299.7,525.78)", wordList.get(0).get(0).getXY().toString());
		Assert.assertEquals("no", "{(2)}", wordList.get(1).toString());
		Assert.assertEquals("xys", "(312.42,527.7)", wordList.get(1).get(0).getXY().toString());
	}
	
	@Test
	public void test2_11() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(
						Fixtures.IMAGE_2_11_SVG, (PageAnalyzer) null);
		List<RawWords> wordList = textStructurer.createRawWordsList();
		Assert.assertEquals("2.11", 3, wordList.size());
		Assert.assertEquals("1", "{HO........(NO)}", wordList.get(0).toString());
		Assert.assertEquals("2", "{(2)}", wordList.get(1).toString());
		Assert.assertEquals("2", "{(O)}", wordList.get(2).toString());
	}
	
	
	@Test
	public void test2_15() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(
						Fixtures.IMAGE_2_15_SVG, (PageAnalyzer) null);
		
		List<RawWords> wordList = textStructurer.createRawWordsList();
		Assert.assertEquals("words", 6, wordList.size());
		Assert.assertEquals("0", "{(O)}", wordList.get(0).toString());
		Assert.assertEquals("1", "{(N)}", wordList.get(1).toString());
		Assert.assertEquals("2", "{(H)}", wordList.get(2).toString());
		Assert.assertEquals("3", "{(H)}", wordList.get(3).toString());
		Assert.assertEquals("4", "{OH..(O)}", wordList.get(4).toString());
		Assert.assertEquals("5", "{Cyclopiazonic.(acid)}", wordList.get(5).toString());
	}



}
