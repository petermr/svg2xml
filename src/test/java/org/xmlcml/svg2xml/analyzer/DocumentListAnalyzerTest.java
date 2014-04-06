package org.xmlcml.svg2xml.analyzer;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.collection.DocumentListAnalyzer;

/** tests DocumentListAnalyzer (either diretcories oe files)
 * 
 * @author pm286
 *
 */
public class DocumentListAnalyzerTest {

	// DO NOT RUN THESE AS DISTRIBUTION TESTS if you get out of memory
	
	public final static File BMCDIR = new File("src/test/resources/pdfs/bmc");
	public final static File PDFDIR = new File("../pdfs");
	public final static File PEERJDIR = new File(PDFDIR, "peerj");
	
	@Test
	@Ignore
	public void testDirectory() {
		DocumentListAnalyzer analyzer = new DocumentListAnalyzer();
		analyzer.analyzeDirectory(BMCDIR);
	}
	
	@Test
	@Ignore
	public void testDirectoryPeerJ() {
		DocumentListAnalyzer analyzer = new DocumentListAnalyzer();
		analyzer.analyzeDirectory(PEERJDIR);
	}
	
}
