package org.xmlcml.svg2xml.container;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.text.TextFixtures;

public class ListContainerTest {

	private static final PrintStream SYSOUT = System.out;

	@Test
	public void testAJCList() throws Exception {
		File file = TextFixtures.ACS_072516_6_4SB_SVG;
		String outfile = "target/acs072516_6_4Sa.html";
		createAndDebugList("AJC", file, outfile);
	}

	@Test
	public void testAJCList65() throws Exception {
		File file = TextFixtures.ACS_072516_6_5SA_SVG;
		String outfile = "target/acs072516_6_5Sa.html";
		createAndDebugList("AJC", file, outfile);
	}

	@Test
	public void testBMCList() throws Exception {
		File file = TextFixtures.BMC_312_12_7SB_SVG;
		String outfile = "target/bmc_312_12_7Sb.html";
		createAndDebugList("BMC", file, outfile);
	}

	@Test
	public void testRSCList() throws Exception {
		File file = TextFixtures.RSC_B306241d_6_8SA_SVG;
		String outfile = "target/rscb306241d.chunk6.8Sa.html";
		createAndDebugList("RSC", file, outfile);
	}

	@Test
	public void testRSCListHtml() throws Exception {
		File file = TextFixtures.RSC_B306241d_6_8SA_SVG;
		String outfile = "target/rscb306241d.chunk6.8Sa.html";
		ScriptContainer sc = ScriptContainer.createScriptContainer(file);
		ListContainer listContainer = ListContainer.createList(sc);
		HtmlElement elem = listContainer.createHtmlElement();
		CMLUtil.debug(elem, new FileOutputStream(new File(outfile)), 1);
	}

	@Test
	//@Ignore // fails RuntimeException // FIXME
	public void testMaterialsList() throws Exception {
		File file = TextFixtures.MDPI_27_18_7SA_SVG;
		String outfile = "target/mdpi_27_18_7Sa.html";
		createAndDebugList("MDPI", file, outfile);
	}

	@Test
	// don't understand why this doesn't work. Perhaps on double boundary?
	public void testNPGList() throws Exception {
		File file = TextFixtures.NPG_00788_5_3SA_SVG;
		String outfile = "target/npg00778.chunk5.3Sa.html";
		createAndDebugList("NPG", file, outfile);
	}

	@Test
	// PROBLEM WITH SEPARATE ACCENTS on slightly different line
	// also wobbly x coords for start of indent (up to 0.6 pixel)
	@Ignore // superscripts not sorted out
	public void testNPGList54() throws Exception {
		File file = TextFixtures.NPG_00778_5_4SA_SVG;
		String outfile = "target/npg00778.chunk5.4Sa.html";
		createAndDebugList("NPG", file, outfile);
	}

	@Test
	public void testPeerJBullet() throws Exception {
		File file = TextFixtures.PEERJ_50_12_6SB_SVG;
		String outfile = "target/peerj50.chunk12.6Sb.html";
		createAndDebugList("PeerJ", file, outfile);
	}

	/** =======================================================
	npg00778.chunk5.3Sa
	 * ========================================================
	 */
	
	private static void createAndDebugList(String pub, File file, String outfile) {
		SYSOUT.println("========="+pub+"===========");
		ScriptContainer sc = ScriptContainer.createScriptContainer(file);
		ListContainer listContainer = ListContainer.createList(sc);
		if (listContainer != null) {
//			listContainer.debug();
		} else {
			SYSOUT.println("Cannot create list");
		}
		SYSOUT.println("==========================");
	}

}
