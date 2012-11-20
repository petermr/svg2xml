package org.xmlcml.svgplus.core;

import junit.framework.Assert;

import org.junit.Test;

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
	 * we can run without an input file - jsut doesn't dso very much
	 */
	public void testCommand() {
		SVGPlusConverter converter = new SVGPlusConverter(); 
		try {
			converter.run("" +
				"-c src/main/resources/org/xmlcml/svgplus/core/noop.xml ");
		} catch (Exception e) {
			throw new RuntimeException("should not fail run");
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
					"-i src/test/resources/org/xmlcml/svgplus/core/ajc-page6.pdf ");
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
					"-c src/main/resources/org/xmlcml/svgplus/core/noop.xml "+
					"-i src/test/resources/org/xmlcml/svgplus/core/ajc-page6.pdf ");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("failed: "+e.getCause());
		}
		Assert.assertTrue("read file", true);
	}

}
