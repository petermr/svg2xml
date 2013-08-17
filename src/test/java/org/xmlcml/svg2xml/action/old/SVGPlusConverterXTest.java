package org.xmlcml.svg2xml.action.old;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.action.SVGPlusConverterX;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;

@Ignore
public class SVGPlusConverterXTest {

	/** some of these crash with OutOfMemoryError on maven so most commented out */
	
	@Test
	/** prints usage
	 *  should run without crashing and leave a few lines on sysout
	 */
	public void testUsage() {
		SVGPlusConverterX converter = new SVGPlusConverterX();
		converter.run("");
		Assert.assertTrue("didn't crash", true);
	}

	@Test
	/** tests that we can read a minimal command file
	 * we can run without an input file - just doesn't do very much
	 */
	public void testCommand() {
		SVGPlusConverterX converter = new SVGPlusConverterX(); 
		try {
			converter.run("" +
				"-c "+Fixtures.NOOP_FILE);
		} catch (Exception e) {
			throw new RuntimeException("should not fail run", e);
		}
		Assert.assertNull(converter.getSemanticDocumentAction().getVariable(SemanticDocumentActionX.S_INFILE));
	}
	
	@Test
	/** tests that command file is mandatory
	 *  If any args are present, so must -c file be
	 */
	public void testMissingCommandFile() {
		SVGPlusConverterX converter = new SVGPlusConverterX(); 
		try {
			converter.run("" +
					" -o target" +
					"-i " + Fixtures.AJC_PAGE6_PDF);
			Assert.fail("Should trap missing command file");
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertTrue("must give command file", msg.startsWith("Must always give command file"));
		}
	}
	@Test
	@Ignore // crashes with GC limit on maven
	/** tests that input file can be read
	 *  If any args are present, so must -c file be
	 */
	public void testInputFile() {
		SVGPlusConverterX converter = new SVGPlusConverterX(); 
		try {
			converter.run("" +
					" -o target" +
					" -c "+Fixtures.NOOP_FILE +
					" -i "+Fixtures.AJC_PAGE6_PDF);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

	@Test
	@Ignore // crashes with GC limit on maven
	/** tests that input file can be read
	 *  If any args are present, so must -c file be
	 */
	public void testInputFileExecution() {
		SVGPlusConverterX converter = new SVGPlusConverterX(); 
		try {
			converter.run("" +
					" -o target" +
					" -c "+Fixtures.BASIC_FILE +
					" -i "+Fixtures.AJC_PAGE6_PDF);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

	@Test
	@Ignore // crashes with GC limit on maven
	/** tests that input file can be read
	 *  If any args are present, so must -c file be
	 */
	public void testInputFilesExecution() {
		SVGPlusConverterX converter = new SVGPlusConverterX(); 
		try {
			converter.run("" +
					" -o target" +
					" -c "+Fixtures.BASIC_FILE +
					" -i "+Fixtures.CORE_DIR);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

	@Test
	@Ignore // crashes with GC limit on maven
	/** tests that input file can be read and outputDir can be set
	 *  If any args are present, so must -c file be
	 */
	public void testInputOutputFileExecution() {
		SVGPlusConverterX converter = new SVGPlusConverterX(); 
		try {
			converter.run("" +
					" -c "+Fixtures.BASIC_FILE +
					" -i "+Fixtures.AJC_PAGE6_PDF +
					" -o "+"target");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

	@Test
	@Ignore // crashes with GC limit on maven
	/** tests chunkAnalyzer
	 * 
	 */
	public void testChunkAnalyzer() {
		SVGPlusConverterX converter = new SVGPlusConverterX(); 
		try {
			converter.run("" +
					" -c "+Fixtures.CHUNK_ANALYZE +
					" -i "+Fixtures.AJC_PAGE6_PDF +
					" -o "+"target/chunkAnalyze0");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

	@Test
	@Ignore //runs OOM on commandline
	/** tests chunkAnalyzer
	 * 
	 */
	public void testChunkAnalyzerCSIRO0() {
		SVGPlusConverterX converter = new SVGPlusConverterX(); 
		try {
			converter.run("" +
					" -c "+Fixtures.CHUNK_ANALYZE +
					" -i "+Fixtures.CSIRO_DIR0 +
					" -o "+"target/csiro/test0");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

	@Test
	@Ignore // crashes with GC limit on maven
	/** tests chunkAnalyzer
	 * 
	 */
	public void testChunkAnalyzerCSIRO1() {
		SVGPlusConverterX converter = new SVGPlusConverterX(); 
		try {
			converter.run("" +
					" -c "+Fixtures.CHUNK_ANALYZE +
					" -i "+Fixtures.CSIRO_DIR1 +
					" -o "+"target/csiro/test1");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

}
