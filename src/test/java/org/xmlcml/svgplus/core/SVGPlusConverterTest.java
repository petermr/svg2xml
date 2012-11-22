package org.xmlcml.svgplus.core;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svgplus.Fixtures;

public class SVGPlusConverterTest {

	@Test
	/** prints usage
	 *  should run without crashing and leave a few lines on sysout
	 */
	public void testUsage() {
		SVGPlusConverter converter = new SVGPlusConverter();
		converter.run("");
		Assert.assertTrue("didn't crash", true);
	}

	@Test
	/** tests that we can read a minimal command file
	 * we can run without an input file - just doesn't do very much
	 */
	public void testCommand() {
		SVGPlusConverter converter = new SVGPlusConverter(); 
		try {
			converter.run("" +
				"-c "+Fixtures.NOOP_FILE);
		} catch (Exception e) {
			throw new RuntimeException("should not fail run", e);
		}
		Assert.assertNull(converter.getSemanticDocumentAction().getVariable(SemanticDocumentAction.S_INFILE));
	}
	
	@Test
	/** tests that command file is mandatory
	 *  If any args are present, so must -c file be
	 */
	public void testMissingCommandFile() {
		SVGPlusConverter converter = new SVGPlusConverter(); 
		try {
			converter.run("" +
					"-i " + Fixtures.AJC_PAGE6_PDF);
			Assert.fail("Should trap missinf command file");
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertTrue("must give command file", msg.startsWith("Must always give command file"));
		}
	}
	@Test
	/** tests that input file can be read
	 *  If any args are present, so must -c file be
	 */
	public void testInputFile() {
		SVGPlusConverter converter = new SVGPlusConverter(); 
		try {
			converter.run("" +
					" -c "+Fixtures.NOOP_FILE +
					" -i "+Fixtures.AJC_PAGE6_PDF);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

	@Test
	/** tests that input file can be read
	 *  If any args are present, so must -c file be
	 */
	public void testInputFileExecution() {
		SVGPlusConverter converter = new SVGPlusConverter(); 
		try {
			converter.run("" +
					" -c "+Fixtures.BASIC_FILE +
					" -i "+Fixtures.AJC_PAGE6_PDF);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

}
