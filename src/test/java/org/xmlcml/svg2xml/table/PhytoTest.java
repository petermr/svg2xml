package org.xmlcml.svg2xml.table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.pdf.PDFAnalyzer;

/** tables from JEssOilRes.
 * and maybe elsewhere
 * 
 * @author pm286
 *
 */
public class PhytoTest {
	private static final Logger LOG = Logger.getLogger(PhytoTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	private final static File JEOR_DIR = new File("../../phyto/joer");
	
	@Test
	/** just to generate the SVG
	 * 
	 */
	@Ignore
	public void testCreateSVG() {
		List<File> pdfFiles = new ArrayList<File>
			(FileUtils.listFiles(JEOR_DIR, new WildcardFileFilter("fulltext.pdf") , TrueFileFilter.INSTANCE));
		int i = 0;
		for (File pdfFile : pdfFiles) {
			String root = pdfFile.getParentFile().getName();
			File wrongOutDir = new File("target/svg/fulltext");
			File outDir = new File(JEOR_DIR, root+"/svg/");
			PDFAnalyzer pdfAnalyzer = new PDFAnalyzer();
			pdfAnalyzer.setOutputTopDir(outDir);
			pdfAnalyzer.analyzePDFFile(pdfFile);
			new PDFAnalyzer().analyzePDFFile(pdfFile);
			// results should be in outDir but aren't...
			try {
				FileUtils.copyDirectory(wrongOutDir, outDir);
				FileUtils.deleteDirectory(wrongOutDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (i > 10) break;
		}
	}
}
