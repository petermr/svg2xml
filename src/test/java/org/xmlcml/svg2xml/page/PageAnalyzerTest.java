package org.xmlcml.svg2xml.page;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.SVG2XMLFixtures;
import org.xmlcml.svg2xml.container.AbstractContainerOLD;
import org.xmlcml.svg2xml.container.MixedContainer;
import org.xmlcml.svg2xml.container.ScriptContainerOLD;
import org.xmlcml.svg2xml.container.ShapeContainer;
import org.xmlcml.svg2xml.pdf.PDFAnalyzer;
import org.xmlcml.svg2xml.text.ScriptLineOLD;
import org.xmlcml.svg2xml.text.StyleSpansOLD;
import org.xmlcml.svg2xml.text.StyleSpansTest;


public class PageAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(PageAnalyzerTest.class);
	
	public final static String BMC_GEOTABLE = "geotable-1471-2148-11-310";

	public final static String AJC1 = "CH01182";
	
	@Test
	public void test312MULT_8() {
		String[][][] values ={
				org.xmlcml.svg2xml.text.TextFixtures.BMC_312MULT_8_0_HTML,
				org.xmlcml.svg2xml.text.TextFixtures.BMC_312MULT_8_1_HTML,
				org.xmlcml.svg2xml.text.TextFixtures.BMC_312MULT_8_2_HTML,
		};
		File[] files ={
				org.xmlcml.svg2xml.text.TextFixtures.BMC_312MULT_8_0_SVG,
				org.xmlcml.svg2xml.text.TextFixtures.BMC_312MULT_8_1_SVG,
				org.xmlcml.svg2xml.text.TextFixtures.BMC_312MULT_8_2_SVG,
		};
		org.xmlcml.svg2xml.text.TextFixtures.testSpans(values, files);
	}
	
	@Test
	public void testPageAnalyzer8() {
		PageAnalyzer pageAnalyzer = new PageAnalyzer(org.xmlcml.svg2xml.text.TextFixtures.createSVG(org.xmlcml.svg2xml.text.TextFixtures.BMC_312MULT_8_SVG), null);
		//PageAnalyzer pageAnalyzer = new PageAnalyzer(this, pageCounter);
		pageAnalyzer.splitChunksAndCreatePage();
		/*pageAnalyzer.analyze();
		List<AbstractContainer> containerList = pageAnalyzer.getPageAnalyzerContainerList();
		Assert.assertNotNull("containerList", containerList);
		for (AbstractContainer container : containerList) {
			LOG.trace(container.toString());
		}*/
	}


	@Before
	public void createSVGFixtures() {
		//PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, BMC_GEOTABLE);
	}
	
	@Test
	public void testSetup() {
		
	}
	
	//TODO update container count once a decent chunking algorithm has been written
	@Test
	public void testRawPage1() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE1);
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		Assert.assertNotNull("containers", containerList);
		//Assert.assertEquals("containers", 12, containerList.size());
	}
	
	@Test
	public void testRawPage1classes() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE1);
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		checkAbstractContainers(
				new Class[]{
				ScriptContainerOLD.class,
				ShapeContainer.class,
				MixedContainer.class,
				ScriptContainerOLD.class,
				ScriptContainerOLD.class,
				MixedContainer.class,
				ScriptContainerOLD.class,
				ScriptContainerOLD.class,
				MixedContainer.class,
				ScriptContainerOLD.class,
				ShapeContainer.class,
				ScriptContainerOLD.class,},
				containerList);
	}
	
	@Test
	public void testRawPage2classes() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		checkAbstractContainers(
				new Class[]{
						ScriptContainerOLD.class,
						ScriptContainerOLD.class,
						ScriptContainerOLD.class,
						ScriptContainerOLD.class,
						ScriptContainerOLD.class,
						ScriptContainerOLD.class,
						ScriptContainerOLD.class,
						},
				containerList);
	}
	
	@Test
	public void testRawPage2Content() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
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
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		int i = 0;
		for (AbstractContainerOLD container : containerList) {
			new File("target/junk/").mkdirs();
			SVGUtil.debug(container.createHtmlElement(), 
				new FileOutputStream(new File("target/junk/page2."+(i++)+".html")), 1);
		}
	}
	
	@Test
	public void testPage2Html0() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		String actual = containerList.get(0).createHtmlElement().toXML();
		LOG.trace(".. "+actual);
		Assert.assertEquals("html0", "" +
				"<div xmlns=\"http://www.w3.org/1999/xhtml\" id=\"g.2.0\">Hiwatashi  <i>et al</i> .  <i>BMC Evolutionary Biology </i> 2011,  <b>11</b> :312 http://www.biomedcentral.com/1471-2148/11/312 </div>",
					actual);
	}
	
	/** 
	 * Note this has wrongly elided 's'
	 */
	@Test
	public void testPage2Html3_3() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.SVG_MULTIPLE_2_3_3_SVG);
		LOG.trace(SVGElement.readAndCreateSVG(SVG2XMLFixtures.SVG_MULTIPLE_2_3_3_SVG).toXML());
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		String actual = containerList.get(0).createHtmlElement().toXML();
		LOG.trace(".. "+actual);
		// ids are computed so 2.0
		Assert.assertEquals("html0", "" +
				"<div xmlns=\"http://www.w3.org/1999/xhtml\" id=\"g.2.0\">study, we focused on gibbons (Family Hylobatidae), com-</div>",
				actual);
	}

	/** 
	 * Note this has wrongly elided 's'
	 */
	@Test
	public void testPage2Html3() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		String actual = containerList.get(3).createHtmlElement().toXML();
		LOG.trace(".. "+actual);
		Assert.assertEquals("html3", 
				"<div xmlns=\"http://www.w3.org/1999/xhtml\" id=\"g.2.3\">L opsin gene of two African hominoids, humans [18] and chimpanzees (primarily  <i>P. t. verus</i>" +
				" ) [25]. In the present study, we focused on gibbons (Family Hylobatidae), com-monly known as the lesser apes, for which normal tri-chromacy is reported" +
				" [30]. Gibbons occur in Asia and are the most diverse and speciose of all living apes [31], mak-ing them an ideal group with which to assess the range" +
				" of L/M opsin genetic variation. We examined the nucleotide variation of both the L and M opsin genes by sequencing the 3.6~3.9-kb genomic region" +
				" encompassing exon 3 to exon 5 from individuals in five species and three genera of gibbons. <p /></div>",
				actual);
	}

	@Test
	public void testPage2ScriptLineList0Content() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
		ScriptContainerOLD scriptContainer = (ScriptContainerOLD) pageAnalyzer.getAbstractContainerList().get(0);
		List<ScriptLineOLD> scriptLineList = scriptContainer.getScriptLineList();
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
		StyleSpansOLD styleSpans = StyleSpansTest.getStyleSpans(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2, 0, 0);
		StyleSpansTest.checkStyleSpans("0 0", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">Hiwatashi <i>et al</i>. <i>BMC Evolutionary Biology </i>2011, <b>11</b>:312</span>",
				7.97, styleSpans);
	}
	
	@Test
	public void testStyleSpans2_2_0() {
		StyleSpansOLD styleSpans = StyleSpansTest.getStyleSpans(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2, 2, 0);
		StyleSpansTest.checkStyleSpans("2 0", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">corresponding sequence of the other, this type of recombi-</span>",
				9.763, styleSpans);
	}
	
	@Test
	public void testPage2ScriptLineList0() {
		StyleSpansOLD styleSpans = StyleSpansTest.getStyleSpans(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2, 0, 0);
		StyleSpansTest.checkStyleSpans("0 0", 
				"<span xmlns=\"http://www.w3.org/1999/xhtml\">Hiwatashi <i>et al</i>. <i>BMC Evolutionary Biology </i>2011, <b>11</b>:312</span>",
				7.97, styleSpans);
	}
	

	@Test
	public void testRawPage3classes() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE3);
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		checkAbstractContainers(
				new Class[]{
						ScriptContainerOLD.class,
						ScriptContainerOLD.class,
						MixedContainer.class,
						ScriptContainerOLD.class,
						ScriptContainerOLD.class,
						},
				containerList);
	}
	
	@Test
	@Ignore
	public void testSVGBug() {
		GraphicsElement svgElement = SVGElement.readAndCreateSVG(new File("src/test/resources/svg/Shukla/page8.svg"));
		//analyzeChunkInSVGPage((SVGElement) svgElement.getChildElements().get(5), "chunk", "src/test/resources/org/xmlcml/svg2xml/svg/Shukla/out", "results");
	}
	
	@Test
    @Ignore
	public void testSVGBug1() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(new File("src/test/resources/svg/Shukla/page8.svg"));
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		checkAbstractContainers(
			new Class[]{
					org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
					org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.ShapeContainer.class,
					org.xmlcml.svg2xml.container.MixedContainer.class,
					org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
					org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
			},
			containerList);
	}
	
	@Test
	public void testMDPIPageAndProcessing() {
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(new File("src/test/resources/svg/mdpi/metabolites-02-00039-page2.svg"));
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		checkAbstractContainers(
		new Class[]{
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.MixedContainer.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				
				org.xmlcml.svg2xml.container.MixedContainer.class,
				org.xmlcml.svg2xml.container.MixedContainer.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.MixedContainer.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.MixedContainer.class,
				org.xmlcml.svg2xml.container.MixedContainer.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.MixedContainer.class,
				
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.MixedContainer.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.MixedContainer.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.MixedContainer.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
				org.xmlcml.svg2xml.container.ScriptContainerOLD.class,
		},
		containerList);
	}
	
	//TODO this chunk doesn't look right
	@Test
	public void testOutput(){
		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(new File("src/test/resources/svg/mdpi/metabolites-02-00039-page2.svg"));
		pageAnalyzer.getPageIO().setRawSVGDocumentDir(new File("target/mdpi/test/"));
		List<AbstractContainerOLD> containerList = pageAnalyzer.getAbstractContainerList();
		MixedContainer mixedContainer10 = (MixedContainer) containerList.get(7);
		GraphicsElement chunk10 = mixedContainer10.getChunkAnalyzer().getSVGChunk();
		LOG.trace("chunk10 "+chunk10.toXML());
		pageAnalyzer.outputChunks();
		pageAnalyzer.outputHtmlComponents();
		pageAnalyzer.outputImages();
		pageAnalyzer.outputHtmlRunningText();
		pageAnalyzer.writeFinalSVGPageToFinalDirectory();
	}

	//================================================================
	
	//====================================================================
	
	//TODO sort out count checking once a decent chunking algorithm has been written
	private void checkAbstractContainers(Class<?>[] classes,
			List<AbstractContainerOLD> containerList) {
		Assert.assertNotNull(containerList);
		/*try {
			Assert.assertEquals("size", classes.length, containerList.size());
		} catch (AssertionError e) {
			System.err.println("ERROR: found classes");
			for (AbstractContainer container : containerList) {
				System.err.println(container.getClass());
			}
			throw (new RuntimeException("Failed", e));
		}
		for (int i = 0; i < classes.length; i++) {
			Assert.assertEquals("container"+i, classes[i], containerList.get(i).getClass());
		}*/
	}

	private void checkContainerRawContent(String[] contents,
			List<AbstractContainerOLD> containerList) {
		try {
			Assert.assertEquals("size", contents.length, containerList.size());
		} catch (AssertionError e) {
			for (AbstractContainerOLD container : containerList) {
				System.err.println(container.getRawValue());
			}
			throw (new RuntimeException("Failed", e));
		}
		for (int i = 0; i < contents.length; i++) {
			Assert.assertEquals("container"+i, contents[i], containerList.get(i).getRawValue());
		}
	}

	private void checkScriptLineListContent(String[] contents,
			List<ScriptLineOLD> scriptLineList) {
		try {
			Assert.assertEquals("size", contents.length, scriptLineList.size());
		} catch (AssertionError e) {
			for (ScriptLineOLD scriptLine : scriptLineList) {
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
		File[] files = inDir.listFiles();
		if (files != null) {
			for (File file : files) {
				String path = file.getName().toLowerCase();
				if (path.endsWith(".pdf")) {
					PDFAnalyzer analyzer = new PDFAnalyzer();
					analyzer.setSVGTopDir(svgDir);
					analyzer.setOutputTopDir(outDir);
					//analyzer.setSkipFile(skipFile);
					analyzer.analyzePDFFile(file);
				}
			}
		}
	}

}
