package org.xmlcml.svg2xml.analyzer;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.text.SvgPlusCoordinate;
import org.xmlcml.svg2xml.text.TextLineContainer;

import com.google.common.collect.Multiset;

public class TextLineContainerTest {

	private final static Logger LOG = Logger.getLogger(TextLineContainerTest.class);
	private static final String SVG = ".svg";
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
			if (name.startsWith(GEOTABLE_7) && name.endsWith(SVG) && !name.equals(GEOTABLE_7+"svg")) {
				geoFileList.add(file);
			}
		}
		Assert.assertTrue(geoFileList.size() == 7);
	}
	
	@Test
	public void testMultilineFonts() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainer(Fixtures.PARA_SUSCRIPT_SVG);
		Multiset<String> fontFamilyMultiset = textLineContainer.getFontFamilyMultiset();
		Assert.assertEquals("font occurrences", 523, fontFamilyMultiset.size());
		Set<String> entrySet = fontFamilyMultiset.elementSet();
		Assert.assertEquals("different fonts", 3, entrySet.size());
		Assert.assertEquals("Times-Roman", 3, fontFamilyMultiset.count("Times-Roman"));
		Assert.assertEquals("MTSYN", 7, fontFamilyMultiset.count("MTSYN"));
		Assert.assertEquals("TimesNewRoman", 513, fontFamilyMultiset.count("TimesNewRoman"));
	}
	
	@Test
	public void testMultilineCommonestFontFamily() {
		TextLineContainer textLineContainer = TextLineContainer.createTextLineContainer(Fixtures.PARA_SUSCRIPT_SVG);
		Assert.assertEquals("commonest fontfamily", "TimesNewRoman", textLineContainer.getCommonestFontFamily());
	}
	
	
	@Test
	public void testReadBMCGeotable() {
		Assert.assertEquals(7, geoFileList.size());
	}
	
	@Test
	public void testReadBMCGeotableContainers() {
		for (File geoFile : geoFileList) {
			TextLineContainer container = TextLineContainer.createTextLineContainer(geoFile);
		}
	}
	
	@Test
	@Ignore
	public void testCommonestBMCGeotableFontSizes() {
		double[] sizes = {7.97, 7.97, 9.76, 10.26, 9.76, 10.26, 9.76};
		int i = 0;
		for (File geoFile : geoFileList) {
			TextLineContainer container = TextLineContainer.createTextLineContainer(geoFile);
			SvgPlusCoordinate size = container.getCommonestFontSize();
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
			TextLineContainer container = TextLineContainer.createTextLineContainer(geoFile);
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
			TextLineContainer container = TextLineContainer.createTextLineContainer(geoFile);
			Assert.assertEquals("file"+i, nfont[i], container.getFontFamilyCount());
			i++;
		}
	}
	
	@Test
	public void testBMCGeotableTextLines() {
		File geoFile2 = geoFileList.get(2);
		TextLineContainer container = TextLineContainer.createTextLineContainer(geoFile2);
	}

}
