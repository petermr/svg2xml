package org.xmlcml.svg2xml.table;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGDefs;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.graphics.svg.linestuff.Path2ShapeConverter;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class CMUCLTest {

	public static final Logger LOG = Logger.getLogger(CMUCLTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static File CMUCL0 = new File(Fixtures.TABLE_DIR, "cmucl0/");
	private static final File CMUCL_OUT_DIR = new File("target/table/cmucl0");

	@Test
	public void testBMC() {
		String root = "BMC_Medicine";
		extractTables(root);
	}
	
	@Test
	public void testBMCHTML() {
		String root = "BMC_Medicine";
		createHTML(root);
	}
	
	@Test
	public void testBMCMarkup() {
		String root = "BMC_Medicine";
		markupAndOutputTables(root);
	}
	
	@Test
	public void testInformaRotated() {
		String root = "Informa_ExpOpinInvestDrugsRot";
		markupAndOutputTables(root);
	}
	
	@Test
	/** need to hack the boxes */
	public void testLancet() {
		String root = "TheLancet_1";
		markupAndOutputTables(root);
	}
	
	@Test
	public void testNature() {
		String root = "Nature_EurJClinNutrit";
		markupAndOutputTables(root);
	}
	
	@Test
	public void Nature_SciRep_1() {
		String root = "Nature_SciRep_1";
		markupAndOutputTables(root);
	}
	
	
	@Test
	public void testAllTables() {
		File[] dirs = CMUCL0.listFiles();
		for (File dir : dirs) {
			extractTables(dir.getName());
		}
	}

	@Test
	public void testAllMarkup() {
		File[] dirs = CMUCL0.listFiles();
		for (File dir : dirs) {
			markupAndOutputTables(dir.getName());
		}
	}

	// ======================
	
	public static void createHTML(String root) {
		File inDir = new File(CMUCL0, root+"/");
		File outDir = new File(CMUCL_OUT_DIR, root+"/");
		List<File> tableFiles = new ArrayList<File>
			(FileUtils.listFiles(inDir, new WildcardFileFilter("table*.svg") , TrueFileFilter.INSTANCE));
		for (File tableFile : tableFiles) {
			TableContentCreator tableContentCreator = new TableContentCreator();
			HtmlHtml html = tableContentCreator.createHTMLFromSVG(tableFile);
			File outfile = new File(outDir, tableFile.getName()+".html");
			LOG.debug("writing: "+outfile);
			try {
				XMLUtil.debug(html, outfile, 1);
			} catch (IOException e) {
				LOG.error("Cannot write file: "+outfile+" ("+e+")");
			}
		}
	}

	private static void markupAndOutputTables(String root/*, String filename, int nHeaderCols, int nBodyCols*/) {
		File inDir = new File(CMUCL0, root+"/");
		File outDir = new File(CMUCL_OUT_DIR, root+"/");
		List<File> inputFiles = new ArrayList<File>
		(FileUtils.listFiles(inDir, new WildcardFileFilter("table*.svg") , TrueFileFilter.INSTANCE));
		for (File inputFile : inputFiles) {
			TableContentCreator tableContentCreator = new TableContentCreator(); 
			tableContentCreator.markupAndOutputTable(inputFile, outDir);
		}
	}

	private void extractTables(String root) {
		File outDir = CMUCL_OUT_DIR;
		outDir.mkdirs();
		File bmcDir = new File(CMUCL0, root);
		List<File> tableFiles = new ArrayList<File>
			(FileUtils.listFiles(bmcDir, new WildcardFileFilter("table*.svg") , TrueFileFilter.INSTANCE));
		for (File file : tableFiles) {
			String fileroot = file.getName().replace(".svg", "");
			SVGSVG svg = null;
			try {
				svg = (SVGSVG) SVGUtil.parseToSVGElement(new FileInputStream(file));
			} catch (Exception e) {
				LOG.error("Cannot find/read: "+file);
				continue;
			}
			SVGDefs.removeDefs(svg);
			List<SVGPath> paths = SVGPath.extractSelfAndDescendantPaths(svg);
			List<SVGShape> shapes = new Path2ShapeConverter().convertPathsToShapes(paths);
			SVGG g = new SVGG();
			Multiset<String> classSet = HashMultiset.create();
			for (SVGShape shape : shapes) {
				g.appendChild(shape);
				classSet.add(shape.getClass().getSimpleName());
			}
			LOG.debug(root+": "+classSet);
			SVGSVG.wrapAndWriteAsSVG(g, new File(outDir, root+"/"+fileroot+".shapes.svg"));
		}
	}
}
