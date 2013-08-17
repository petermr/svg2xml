package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.container.DivContainer;
import org.xmlcml.svg2xml.container.PathContainer;
import org.xmlcml.svg2xml.container.ScriptContainer;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.StyleSpans;
import org.xmlcml.svg2xml.text.StyleSpansTest;
import org.xmlcml.svg2xml.text.TextFixtures;


public class PageAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(PageAnalyzerTest.class);
	
	public final static String BMC_GEOTABLE = "geotable-1471-2148-11-310";

	public final static String AJC1 = "CH01182";
	
	@Test
	public void test312MULT_8() {
		String[][][] values ={
				TextFixtures.BMC_312MULT_8_0_HTML,
				TextFixtures.BMC_312MULT_8_1_HTML,
				TextFixtures.BMC_312MULT_8_2_HTML,
		};
		File[] files ={
				TextFixtures.BMC_312MULT_8_0_SVG,
				TextFixtures.BMC_312MULT_8_1_SVG,
				TextFixtures.BMC_312MULT_8_2_SVG,
		};
		TextFixtures.testSpans(values, files);
	}
	
	@Test
	public void testPageAnalyzer8() {
		PageAnalyzer pageAnalyzer = new PageAnalyzer(TextFixtures.createSVG(TextFixtures.BMC_312MULT_8_SVG));
//		PageAnalyzer pageAnalyzer = new PageAnalyzer(this, pageCounter);
		pageAnalyzer.splitChunksAnnotateAndCreatePage();
//		pageAnalyzer.analyze();
//		List<AbstractContainer> containerList = pageAnalyzer.getPageAnalyzerContainerList();
//		Assert.assertNotNull("containerList", containerList);
//		for (AbstractContainer container : containerList) {
//			System.out.println(container.toString());
//		}
	}


	@Before
	public void createSVGFixtures() {
//		PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, BMC_GEOTABLE);
	}
	
	@Test
	public void testSetup() {
		
	}
	
	@Test
	public void testRawPage1() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(Fixtures.RAW_MULTIPLE312_SVG_PAGE1);
		pageAnalyzer.analyze();
//		LOG.debug(pageAnalyzer.toString());
		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
		Assert.assertNotNull("containers", containerList);
		Assert.assertEquals("containers", 12, containerList.size());
	}
	
	@Test
	public void testRawPage1classes() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(Fixtures.RAW_MULTIPLE312_SVG_PAGE1);
		pageAnalyzer.analyze();
		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
		checkAbstractContainers(
				new Class[]{
				ScriptContainer.class,
				PathContainer.class,
				DivContainer.class,
				ScriptContainer.class,
				ScriptContainer.class,
				DivContainer.class,
				ScriptContainer.class,
				ScriptContainer.class,
				DivContainer.class,
				ScriptContainer.class,
				PathContainer.class,
				ScriptContainer.class,},
				containerList);
	}
	
	@Test
	public void testRawPage2classes() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(Fixtures.RAW_MULTIPLE312_SVG_PAGE2);
		pageAnalyzer.analyze();
		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
		checkAbstractContainers(
				new Class[]{
						ScriptContainer.class,
						ScriptContainer.class,
						ScriptContainer.class,
						ScriptContainer.class,
						ScriptContainer.class,
						ScriptContainer.class,
						ScriptContainer.class,
						},
				containerList);
	}
	
	@Test
	public void testRawPage2Content() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(Fixtures.RAW_MULTIPLE312_SVG_PAGE2);
		pageAnalyzer.analyze();
		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
		checkContainerRawContent(
			new String[]{
				"Hiwatashietal.BMCEvolutionaryBiology2011,11:312http://www.biomedcentral.com/1471-2148/11/312",
				"Page2of14",
				"correspondingsequenceoftheother,thistypeofrecombi-nationisoftencalledgeneconversion.Geneconversion" +
				"issuggestedtohaveoccurredfrequentlybetweenthehumanLandMopsingenes[11-15]andtohaveplayedacrucial" +
				"roleingeneratinghybridsofthetwogeneswithalteredspectralsensitivities[16-18].Evenamongindividuals" +
				"withnormalcolorvision,theallelefrequencyoftheLopsingenewithAlaatthesite180insteadofSerisreported" +
				"tobe30-38%innon-Africanpopulations[16-18].Comparedtohumans,theincidenceofcolorvisionvariationis" +
				"reportedtoberareinothercatarrhines[19-21].Among744malelong-tailedmacaques(Macacafascicularis)examined" +
				",onlythreewerefoundtohaveasingleL/Mhybridgenewithanintermediatespectralsensitivityandto" +
				"bedichromats[19,22,23].Among58malechimpanzees(Pantroglodytes),onewasfoundtohaveanL/Mhybridgene" +
				"withanintermediatespectralsensitivityinadditiontoonenormalMopsingeneontheXchromosomeandtobea" +
				"protanomaloustrichro-mat[21,24].Thus,frequenciesofcolorvisionvariantsinmalelong-tailedmacaques" +
				"andmalechimpanzeescanbecalculatedtobe~0.4%and~1.7%,respectively.Thesefrequenciescouldbeover" +
				"estimatedbecausenovariantswerefoundin455malemonkeysfromothermacaquespecies[19,23]andbecause" +
				"thechimpanzeesexaminedwerefromlimitednumbersofbreedingcolonies[21].Otherstudieshavereported" +
				"anabsenceofcolorvisiondefectsinOldWorldmonkeysandapes[20,25].Nevertheless,geneconversionis" +
				"suggestedtohaveoccurredfrequentlybetweentheLandMopsingenesinnon-humancatarrhinesonthebasis" +
				"ofthefollowingobservations:(1)theintraspecificnucleotidedivergencebetweentheLandMopsingenes" +
				"(paralogousdivergence)tendstobesmallerthandivergenceofthesamegenebetweenspecies(orthologousdivergence)" +
				"[26,27],(2)alle-licpolymorphismisoftensharedbetweenLandMopsingenesandbetweenspecies[28,29]," +
				"(3)paralogousnucleo-tidedivergenceinintronsandperipheralexons(exons1and6)issignificantly" +
				"smallerthanthoseinthecentrallylocatedexons(exons2-5),whichcontaintheaminoacidsitesaffecting" +
				"absorptionspectraoftheLandMphotopig-ments[13,14].Thesestudiessuggestthatgeneconversionsat" +
				"nucleotidesitesrelevantforthespectraldifferencebetweentheLandMopsinshavebeeneffectively" +
				"elimi-natedfromthepopulationbypurifyingnaturalselection.Ifgeneconversionoccurredfrequently" +
				"betweentheLandMopsingenesandifpurifyingselectionwasactiveinnon-humancatarrhines,wewouldalso" +
				"expectanotherintraspecificpatternofnucleotidevariation:highernucleotidedivergencebetweenthe" +
				"LandMopsingenesincentralexonsthaninintronsinadditiontolowernucleotidediversitywithintheseexons" +
				"thanwithinintrons.However,thewithin-speciesnucleotidevariationofbothexonsandintronshasbeen" +
				"evaluatedforonlythe",
				"LopsingeneoftwoAfricanhominoids,humans[18]andchimpanzees(primarilyP.t.verus)[25].Inthepresentstudy," +
				"wefocusedongibbons(FamilyHylobatidae),com-monlyknownasthelesserapes,forwhichnormaltri-chromacyis" +
				"reported[30].GibbonsoccurinAsiaandarethemostdiverseandspecioseofalllivingapes[31],mak-ingthemanideal" +
				"groupwithwhichtoassesstherangeofL/Mopsingeneticvariation.WeexaminedthenucleotidevariationofboththeLand" +
				"Mopsingenesbysequencingthe3.6~3.9-kbgenomicregionencompassingexon3toexon5fromindividualsinfivespecies" +
				"andthreegeneraofgibbons.",
				"Methods",
				"GibbonDNAsamplesBloodsampleswerecollectedfromatotalof157indivi-dualsofthefollowingspecies:" +
				"Agile(Hylobatesagilis;N=37),Kloss’(H.klossii;N=2),White-handed(H.lar;N=40),SilveryJavan" +
				"(H.moloch;N=6),Mueller’sBor-neangray(H.muelleri;N=6),Pileated(H.pileatus;N=19),Chinese" +
				"White-cheeked(Nomascusleucogenys;N=16)andSiamang(Symphalangussyndactylus;N=31).Samplingwas" +
				"conductedattheRagunanZooandthePontianakZooinIndonesia,andtheChiangMaiZoo,theBangkokZooand" +
				"theKhaoKheowOpenZooinThailand.Wealsosampledgibbonsrearedbylocalresi-dentsinKalimantan," +
				"Indonesia.GenomicDNAwasextractedfrombloodsamplesusingtheDNAMicroex-tractionKit(Stratagene," +
				"SantaClara,CA)ortheQIAampDNABloodMiniKit(Qiagen,Duesseldorf,Germany).Researchpermissionswere" +
				"grantedbyeachcountryandsamplingwasconductedaccordingtotheGuidefortheCareandUseofLaboratory" +
				"AnimalsbytheNationalInstituteofHealth,U.S.A.(1985)andtheGuidefortheCareandUseofLaboratory" +
				"PrimatesbythePrimateResearchInstitute,KyotoUniversity(1986,2002).Allprocedureswereapproved" +
				"bytheanimalethicscommit-teeofthePrimateResearchInstitute,KyotoUniversity.Amongthe157individuals," +
				"152weresubjectedtothegenotypingoftheL/Mopsingenes(Additionalfile1,TableS1).Theremaining5" +
				"individuals(twoH.agilis,oneH.lar,andtwoS.syndactylus)wereincludedintheanalysisoftheneutral" +
				"referencegenes.Amongthe152individuals,94weresubjectedtoDNAsequencingoftheentire3.6~3.9-kb" +
				"regionencompassingexon3toexon5(Table1).",
				"GenotypingandsequencingofthegibbonLandMopsingenesInprimates,theLandMopsingenesarearrayedinthe" +
				"sameorientationontheX-chromosomeandseparatedbyapproximately24kb[32].Bothgenesconsistofsixexons" +
				"thatencodeaprotein364aminoacidslong,whichspansapproximately15kb[1,33,34].Thefirst(most"
			},
				containerList);
	}
	
	@Test
	public void testPage2HtmlAll() throws Exception {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(Fixtures.RAW_MULTIPLE312_SVG_PAGE2);
		pageAnalyzer.analyze();
		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
		int i = 0;
		for (AbstractContainer container : containerList) {
			Fixtures.HTML_MULTIPLE312_DIR.mkdirs();
			CMLUtil.debug(container.createHtmlElement(), 
					new FileOutputStream(new File(Fixtures.HTML_MULTIPLE312_DIR, "page2."+(i++)+".html")), 1);
		}
	}
	
	@Test
	public void testPage2Html0() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(Fixtures.RAW_MULTIPLE312_SVG_PAGE2);
		pageAnalyzer.analyze();
		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
		Assert.assertEquals("html0", "<div xmlns=\"http://www.w3.org/1999/xhtml\"><span>Hiwatashi <span> </span></span><span><i>et al</i><span> </span></span><span>. <span> </span></span><span><i>BMC Evolutionary Biology </i><span> </span></span><span>2011, <span> </span></span><span><b>11</b><span> </span></span><span>:312<span> </span></span><span>http://www.biomedcentral.com/1471-2148/11/312<span> </span></span></div>",
				containerList.get(0).createHtmlElement().toXML());
	}
	
	@Test
	/** note this has wrongly elided 's'
	 * 
	 */
	public void testPage2Html3() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(Fixtures.RAW_MULTIPLE312_SVG_PAGE2);
		pageAnalyzer.analyze();
		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
		Assert.assertEquals("html0", "<div xmlns=\"http://www.w3.org/1999/xhtml\"><span>L opsin gene of two African hominoids, humans[18] and<span> </span>" +
				"</span><span>chimpanzees (primarily <span> </span></span><span><i>P. t. verus</i><span> </span></span>" +
				"<span>) [25]. In the present<span> </span></span><span>study, we focused on gibbons(Family Hylobatidae), com-</span>" +
				"<span>monly known as the lesser apes, for which normal tri-</span>" +
				"<span>chromacy isreported [30]. Gibbonsoccur in Asia and are<span> </span></span>" +
				"<span>the most diverse and speciose of all living apes[31], mak-</span>" +
				"<span>ing them an ideal group with which to assessthe range of<span> </span></span>" +
				"<span>L/M opsin genetic variation. We examined the nucleotide<span> </span></span>" +
				"<span>variation of both the L and M opsin genesby sequencing<span> </span></span>" +
				"<span>the 3.6~3.9-kb genomic region encompassing exon 3 to<span> </span></span>" +
				"<span>exon 5 from individuals in five species and three genera<span> </span></span>" +
				"<span>of gibbons.</span></div>",
				containerList.get(3).createHtmlElement().toXML());
	}

	@Test
	public void testPage2ScriptLineList0Content() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(Fixtures.RAW_MULTIPLE312_SVG_PAGE2);
		pageAnalyzer.analyze();
		ScriptContainer scriptContainer = (ScriptContainer) pageAnalyzer.getAbstractContainerList().get(0);
		List<ScriptLine> scriptLineList = scriptContainer.getScriptLineList();
		Assert.assertEquals("scriptLines", 2, scriptLineList.size());
		checkScriptLineListContent(
				new String[]{
						"Hiwatashietal.BMCEvolutionaryBiology2011,11:312  %%%%\n",
						"http://www.biomedcentral.com/1471-2148/11/312  %%%%\n"
				},
				scriptLineList
		);
	}
	
	@Test
	public void testStyleSpans2_0_0() {
		StyleSpans styleSpans = StyleSpansTest.getStyleSpans(Fixtures.RAW_MULTIPLE312_SVG_PAGE2, 0, 0);
		StyleSpansTest.checkStyleSpans("0 0", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">Hiwatashi <i>et al</i>. <i>BMC Evolutionary Biology </i>2011, <b>11</b>:312</span>",
				7.97, styleSpans);
	}
	
	@Test
	public void testStyleSpans2_2_0() {
		StyleSpans styleSpans = StyleSpansTest.getStyleSpans(Fixtures.RAW_MULTIPLE312_SVG_PAGE2, 2, 0);
		StyleSpansTest.checkStyleSpans("2 0", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">corresponding sequence of the other, thistype of recombi-</span>",
				9.763, styleSpans);
	}
	
	@Test
	public void testPage2ScriptLineList0() {
		StyleSpans styleSpans = StyleSpansTest.getStyleSpans(Fixtures.RAW_MULTIPLE312_SVG_PAGE2, 0, 0);
		StyleSpansTest.checkStyleSpans("0 0", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">Hiwatashi <i>et al</i>. <i>BMC Evolutionary Biology </i>2011, <b>11</b>:312</span>",
				7.97, styleSpans);
	}
	

	@Test
	public void testRawPage3classes() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(Fixtures.RAW_MULTIPLE312_SVG_PAGE3);
		pageAnalyzer.analyze();
		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
		checkAbstractContainers(
				new Class[]{
						ScriptContainer.class,
						ScriptContainer.class,
						DivContainer.class,
						ScriptContainer.class,
						ScriptContainer.class,
						},
				containerList);
	}
	
//	@Test
//	public void testGeoTablePage() {
//		int page = 2;
//		PDFAnalyzer.analyzeChunkInSVGPage(Fixtures.BMCSVGDIR, BMC_GEOTABLE, page, Fixtures.BMCOUTDIR);
//	}
//	
//	@Test
//	public void testGeoTablePages() {
//		File[] files = new File(Fixtures.BMCSVGDIR, BMC_GEOTABLE).listFiles();
//		PDFAnalyzer.analyzeChunksInPagesInFiles(files, Fixtures.BMCSVGDIR, BMC_GEOTABLE, Fixtures.BMCOUTDIR);
//	}
//
//	@Test
//	public void testBoldResults() {
//		SVGElement svgElement = SVGElement.readAndCreateSVG(new File("src/test/resources/org/xmlcml/svg2xml/svg/bmc/tree-page-2-results.svg"));
//		PDFAnalyzer.analyzeChunkInSVGPage((SVGElement) svgElement.getChildElements().get(0), "chunk", Fixtures.BMCOUTDIR, "results");
//	}
	
	//================================================================
	
	//====================================================================
	
	private void checkAbstractContainers(Class[] classes,
			List<AbstractContainer> containerList) {
		try {
			Assert.assertEquals("size", classes.length, containerList.size());
		} catch (AssertionError e) {
			System.err.println("ERROR");
			for (AbstractContainer container : containerList) {
				System.err.println(container.getClass());
			}
			throw (new RuntimeException("Failed", e));
		}
		for (int i = 0; i < classes.length; i++) {
			Assert.assertEquals("container"+i, classes[i], containerList.get(i).getClass());
		}
	}

	private void checkContainerRawContent(String[] contents,
			List<AbstractContainer> containerList) {
		try {
			Assert.assertEquals("size", contents.length, containerList.size());
		} catch (AssertionError e) {
			for (AbstractContainer container : containerList) {
				System.err.println(container.getRawValue());
			}
			throw (new RuntimeException("Failed", e));
		}
		for (int i = 0; i < contents.length; i++) {
			Assert.assertEquals("container"+i, contents[i], containerList.get(i).getRawValue());
		}
	}

	private void checkScriptLineListContent(String[] contents,
			List<ScriptLine> scriptLineList) {
		try {
			Assert.assertEquals("size", contents.length, scriptLineList.size());
		} catch (AssertionError e) {
			for (ScriptLine scriptLine : scriptLineList) {
				System.err.println(scriptLine);
			}
			throw (new RuntimeException("Failed", e));
		}
		for (int i = 0; i < contents.length; i++) {
			Assert.assertEquals("container"+i, contents[i], scriptLineList.get(i).toString());
		}
	}


	public static void testDirectory(File inDir, File svgDir, File outDir) {
		testDirectory(inDir, svgDir, outDir, true);
	}

	public static void testDirectory(File inDir, File svgDir, File outDir, boolean skipFile) {
		LOG.debug("inputTopDir: "+inDir+"; svgDir "+svgDir+"; outDir "+outDir);
		File[] files = inDir.listFiles();
		if (files != null) {
			for (File file : files) {
				String path = file.getName().toLowerCase();
				LOG.debug("path: "+path);
				if (path.endsWith(".pdf")) {
					PDFAnalyzer analyzer = new PDFAnalyzer();
					analyzer.setSVGTopDir(svgDir);
					analyzer.setOutputTopDir(outDir);
//					analyzer.setSkipFile(skipFile);
					analyzer.analyzePDFFile(file);
				}
			}
		}
	}

}
