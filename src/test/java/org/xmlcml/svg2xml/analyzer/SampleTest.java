package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.html.HtmlMenuSystem;
import org.xmlcml.svg2xml.Fixtures;

public class SampleTest {

	private final static Logger LOG = Logger.getLogger(SampleTest.class);

	public final static File AJCINDIR = new File(Fixtures.EXT_PDFTOP, "ajc");
	public final static File AJCSVGDIR = new File(Fixtures.TARGET, "ajc");
	public final static File AJCOUTDIR = new File(Fixtures.TARGET, "ajc");
	
	public final static String MATHS = "maths-1471-2148-11-311";
	public final static String MULTIPLE = "multiple-1471-2148-11-312";
	public final static String TREE = "tree-1471-2148-11-313";

	public final static String AJC1 = "CH01182";
	
	public void createSVGFixtures() {
		PageAnalyzerTest.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, MATHS);
		PageAnalyzerTest.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, MULTIPLE);
		PageAnalyzerTest.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, TREE);

//		createSVG(AJCINDIR, AJCOUTDIR, AJC1);    // uncomment for AJC

	}
	
	@Test
	public void testSetup() {
		
	}
	
	@Test
	public void testTreePages() {
		PageAnalyzerTest.analyzePaper(Fixtures.BMCSVGDIR, TREE, Fixtures.BMCOUTDIR);
	}

	@Test
	public void testMultiplePages() {
		PageAnalyzerTest.analyzePaper(Fixtures.BMCSVGDIR, MULTIPLE, Fixtures.BMCOUTDIR);
	}

	@Test
	public void testMathsPages() {
		PageAnalyzerTest.analyzePaper(Fixtures.BMCSVGDIR, MATHS, Fixtures.BMCOUTDIR);
	}

	@Test
	@Ignore
	public void testAJC1Pages() {
		PageAnalyzerTest.analyzePaper(AJCSVGDIR, AJC1, AJCOUTDIR);
	}

	@Test
	@Ignore
	public void testZombiePages() {
		PageAnalyzerTest.analyzePDF(Fixtures.MISCINDIR, Fixtures.MISCSVGDIR, "Zombies1", Fixtures.MISCOUTDIR);
	}

	@Test
	public void testElife() {
		PageAnalyzerTest.analyzePDF(Fixtures.ELIFEINDIR, Fixtures.ELIFESVGDIR, "e00013.full", Fixtures.ELIFEOUTDIR);
	}
	
	@Test
	public void testPeerJ() {
		PageAnalyzerTest.analyzePDF(Fixtures.PEERJINDIR, Fixtures.PEERJSVGDIR, "50", Fixtures.PEERJOUTDIR);
	}
	
	@Test
	public void testAny() {
		File[] files = Fixtures.ANYINDIR.listFiles();
		System.out.println(Fixtures.ANYSVGDIR+" ... "+Fixtures.ANYOUTDIR);
		if (files != null) {
			for (File file : files) {
				String path = file.getName().toLowerCase();
				LOG.debug("path: "+path);
				if (path.endsWith(".pdf")) {
					String path0 = path.substring(0, path.length() - 4);
					PageAnalyzerTest.analyzePDF(Fixtures.ANYINDIR, Fixtures.ANYSVGDIR, path0, Fixtures.ANYOUTDIR);
					File htmlDir = (new File(Fixtures.ANYOUTDIR, path0));
					try {
						IOUtils.copy(new FileInputStream(file), new FileOutputStream(new File(htmlDir, "00_"+path)));
					} catch (Exception e1) {
						throw new RuntimeException(e1);
					}
//					HtmlMenuSystem menuSystem = HtmlMenuSystem.readDirectory(htmlDir);
					LOG.debug("HTML system");
					HtmlMenuSystem menuSystem = new HtmlMenuSystem();
					menuSystem.setOutdir(htmlDir.toString());
					File[] filesh = htmlDir.listFiles();
					for (File filex : filesh) {
						menuSystem.addHRef(filex.toString());
					}
					try {
						menuSystem.outputMenuAndBottomAndIndexFrame();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
}
