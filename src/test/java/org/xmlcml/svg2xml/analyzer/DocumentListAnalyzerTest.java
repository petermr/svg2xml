package org.xmlcml.svg2xml.analyzer;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

/** tests DocumentListAnalyzer (either diretcories oe files)
 * 
 * @author pm286
 *
 */
public class DocumentListAnalyzerTest {

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
