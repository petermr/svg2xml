package org.xmlcml.svg2xml.container;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.ScriptWord;
import org.xmlcml.svg2xml.text.StyleSpan;
import org.xmlcml.svg2xml.text.TextFixtures;
import org.xmlcml.svg2xml.text.TextStructurer;

public class ScriptContainerTest {

	@Test
	public void test3WordContainer() {
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_1SA_SVG);
		Assert.assertEquals("1a", 
				"TextStructurer: 1chars: 9 Y: 39.615 fontSize: 7.97 physicalStyle: null >>Page6of14\n",
				textContainer.toString());
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(TextFixtures.BMC_312_6_1SA_SVG);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		Assert.assertEquals("1a", "Page6of14", sc.getRawValue());
	}
	
	
	@Test
	public void test4WordContainerScriptList() {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(TextFixtures.BMC_312_6_1SA_SVG);
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_1SA_SVG);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		List<ScriptLine> scriptList = sc.getScriptLineList();
		Assert.assertEquals("scriptLines", 1, scriptList.size());
		Assert.assertEquals("line0", "Page6of14\n----\n", scriptList.get(0).toString());
	}

	@Test
	public void testGet4Words() {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(TextFixtures.BMC_312_6_1SA_SVG);
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_1SA_SVG);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		List<ScriptLine> scriptLineList = sc.getScriptLineList();
		ScriptLine scriptLine = scriptLineList.get(0);
		List<ScriptWord> scriptWords = scriptLine.getWords();
		Assert.assertEquals("line0", 4, scriptWords.size());
		String[] value ={"Page", "6", "of", "14"};
		for (int i = 0; i < scriptWords.size(); i++) {
			Assert.assertEquals(""+i, value[i], scriptWords.get(i).getRawValue());
		}
	}

	@Test
	public void testGetTitle() {
		testScript(TextFixtures.BMC_312_6_0SA_SVG, new String[][] {
				{"Hiwatashi", "et", "al.", "BMC", "Evolutionary", "Biology", "2011,", "11:312"},
				{"http://www.biomedcentral.com/1471-2148/11/312", },
				});
	}

	/** this is not right - shouldn't split after slash */
	@Test
	public void testBadSlash() {
		testScript(TextFixtures.BMC_312_6_0SA1_SVG, new String[][] {
				{"http://www.biomedcentral.com/1471-2148/11/312" }
				});
	}

	@Test
	public void testGetShortPara() {
		testScript(TextFixtures.BMC_312_6_3SA_SVG, new String[][] {
				{"genes", "in", "the", "exons", "and", "introns", "in", "these", "individuals", "was"},
				{"essentially", "the", "same", "as", "the", "pattern", "shown", "in", "Figure", "1."}
		});
	}

	@Test
	public void testGetShortHeading0() {
		testScript(TextFixtures.BMC_312_6_4SA_SVG, new String[][] {
				{"Nucleotide", "diversity", "of", "L", "and", "M", "opsin", "genes", "within"},
				{"species"}
		}
		);
	}

	@Test
	/** {} means skip checking that line
	 * 
	 */
	public void testGetLargePara() {
		testScript(TextFixtures.BMC_312_6_4SB_SVG, new String[][] {
				{"Figure", "2", "summarizes", "the", "nucleotide", "diversity", "of", "the", "L"},
				{"and", "M", "opsin", "exons", "and", "introns", "and", "of", "the", "neutral", "refer-"},
				{"ences", "(see", "Additional", "file", "1,", "Tables", "S4", "and", "S5", "for", "the"},
				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
				{},				{},				{},				{},				{},
		}
		);
	}

	@Test
	public void testGetLargePara3() {
		testScript(TextFixtures.BMC_312_6_4SB3_SVG, new String[][] {
				{"ences", "(see", "Additional", "file", "1,", "Tables", "S4", "and", "S5", "for", "the"},
		}
		);
	}
	
	@Test
	public void testGetSpans0() {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(TextFixtures.BMC_312_6_0SA0_SVG);
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_312_6_0SA0_SVG);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		List<List<StyleSpan>> styleSpanListList = sc.getStyleSpanListList();
		Assert.assertEquals("lists", 1, styleSpanListList.size());
		Assert.assertEquals("lists0", 7, styleSpanListList.get(0).size());
		Assert.assertEquals("lists0.0", "Hiwatashi", styleSpanListList.get(0).get(0).toString());
	}

	@Test
	public void testGetSpans() {
		String[][] values ={
				{"Hiwatashi", "<I>etal</I>", ".", "<I>BMCEvolutionaryBiology</I>", "2011,", "<B>11</B>", ":312"}, 
				{"http://www.biomedcentral.com/1471-2148/11/312"}
		};
		testSpans(values, TextFixtures.BMC_312_6_0SA_SVG);
	}

	@Test
	// the apostrophes are created in a bold font!
	public void testGetParaSpans() {
		String[][] values ={
				{"Bloodsampleswerecollectedfromatotalof157indivi-"},
				{"dualsofthefollowingspecies:Agile(", "<I>Hylobatesagilis</I>", ";"},
				{"N=37),Kloss", "<B>’</B>", "(", "<I>H.klossii</I>", ";N=2),White-handed(", "<I>H.lar</I>", ";"},
				{"N=40),SilveryJavan(", "<I>H.moloch</I>", ";N=6),Mueller", "<B>’</B>", "sBor-"},
				{"neangray(","<I>H.muelleri</I>",";N=6),Pileated(","<I>H.pileatus</I>",";N="},
				{"19),ChineseWhite-cheeked(","<I>Nomascusleucogenys</I>",";N="},
				{"16)andSiamang(","<I>Symphalangussyndactylus</I>",";N=31)."},
				{"SamplingwasconductedattheRagunanZooandthe"},
				{"PontianakZooinIndonesia,andtheChiangMaiZoo,"},
				{"theBangkokZooandtheKhaoKheowOpenZooin"},
				{"Thailand.Wealsosampledgibbonsrearedbylocalresi-"},
				{"dentsinKalimantan,Indonesia.GenomicDNAwas"},
				{"extractedfrombloodsamplesusingtheDNAMicroex-"},
				{"tractionKit(Stratagene,SantaClara,CA)ortheQIAamp"},
				{"DNABloodMiniKit(Qiagen,Duesseldorf,Germany)."},
				{"Researchpermissionsweregrantedbyeachcountryand"},
				{"samplingwasconductedaccordingtotheGuideforthe"},
				{"CareandUseofLaboratoryAnimalsbytheNational"},
				{"InstituteofHealth,U.S.A.(1985)andtheGuideforthe"},
				{"CareandUseofLaboratoryPrimatesbythePrimate"},
				{"ResearchInstitute,KyotoUniversity(1986,2002).All"},
				{"procedureswereapprovedbytheanimalethicscommit-"},
				{"teeofthePrimateResearchInstitute,KyotoUniversity."},
				{"Amongthe157individuals,152weresubjectedtothe"},
				{"genotypingoftheL/Mopsingenes(Additionalfile1,"},
				{"TableS1).Theremaining5individuals(two", "<I>H.agilis</I>", ","},
				{"one", "<I>H.lar</I>", ",andtwo", "<I>S.syndactylus</I>", ")wereincludedinthe"},
				{"analysisoftheneutralreferencegenes.Amongthe152"},
				{"individuals,94weresubjectedtoDNAsequencingofthe"},
				{"entire3.6~3.9-kbregionencompassingexon3toexon5"},
				{"(Table1)."},
		};
		File file = TextFixtures.BMC_312_2_4SC_SVG;
		testSpans(values, file);
	}

	@Test
	public void testGetHeaders() {
		String[][] values ={
				{"<B>NucleotidediversityofLandMopsingeneswithin</B>"},
				{"<B>species</B>"},
		};
		File file = TextFixtures.BMC_312_6_4SA_SVG;
		testSpans(values, file);
	}
	
	@Test
	// note this has some "bold" quotation marks
	public void testGetReferences() {
		String[][] values ={
	{"1.NathansJ,ThomasD,HognessDS:", "Moleculargeneticsofhumancolor"},
	{"vision:thegenesencodingblue,green,andredpigments.", "<I>Science</I>", "1986,"},
	{"232", ":193-202."},
	{"2.JacobsGH:", "Primatephotopigmentsandprimatecolorvision.", "<I>ProcNatl</I>"},
	{"<I>AcadSciUSA</I>", "1996,", "93", ":577-581."},
	{"3.YokoyamaR,YokoyamaS:", "Convergentevolutionofthered-andgreen-"},
	{"likevisualpigmentgenesinfish,", "<I>Astyanaxfasciatus</I>", ",andhuman.", "<I>Proc</I>"},
	{"<I>NatlAcadSciUSA</I>", "1990,", "87", ":9315-9318."},
	{"4.NeitzM,NeitzJ,JacobsGH:", "Spectraltuningofpigmentsunderlyingred-"},
	{"greencolorvision.", "<I>Science</I>", "1991,", "252", ":971-974."},
	{"5.AsenjoAB,RimJ,OprianDD:", "Moleculardeterminantsofhumanred/"},
	{"greencolordiscrimination.", "<I>Neuron</I>", "1994,", "12", ":1131-1138."},
	{"6.YokoyamaS,RadlwimmerFB:", "The", "<B>“</B>", "five-sites", "<B>”</B>", "ruleandtheevolutionof"},
	{"redandgreencolorvisioninmammals.", "<I>MolBiolEvol</I>", "1998,", "15", ":560-567."},
	{"7.YokoyamaS,RadlwimmerFB:", "Themoleculargeneticsofredandgreen"},
	{"colorvisioninmammals.", "<I>Genetics</I>", "1999,", "153", ":919-932."},
	{"8.YokoyamaS,RadlwimmerFB:", "Themoleculargeneticsandevolutionof"},
	{"redandgreencolorvisioninvertebrates.", "<I>Genetics</I>", "2001,", "158", ":1697-1710."},
	{"9.ChanT,LeeM,SakmarTP:", "Introductionofhydroxyl-bearingaminoacids"},
	{"causesbathochromicspectralshiftsinrhodopsin.Aminoacid"},
		};
		File file = TextFixtures.BMC_312_12_7SB_SVG;
		testSpans(values, file);
	}

	@Test
	public void testGetSuScripts() {
		String[][] values ={
	{"TomohideHiwatashi", "1", ",AkichikaMikami", "2,8", ",TakafumiKatsumura", "1", ",BambangSuryobroto", "3", ","},
	{"DyahPerwitasari-Farajallah", "3,4", ",SuchindaMalaivijitnond", "5", ",BoripatSiriaroonrat", "6", ",HirokiOota", "1,9", ",ShunjiGoto", "7,10", "and"},
	{"ShojiKawamura", "1*"},
		};
		File file = TextFixtures.BMC_312_1_4SA_SVG;
		testSpans(values, file);
	}
	
	@Test
	public void testCorrespondence() {
		String[][] values ={
	{"*Correspondence:kawamura@k.u-tokyo.ac.jp",},
	{"1", "DepartmentofIntegratedBiosciences,GraduateSchoolofFrontierSciences,"},
	{"TheUniversityofTokyo,Kashiwa277-8562,Japan", },
	{"Fulllistofauthorinformationisavailableattheendofthearticle"},
		};
		File file = TextFixtures.BMC_312_1_7DA_SVG;
		testSpans(values, file);
	}
	
	@Test
	public void testLicence() {
		String[][] values ={
	{"©2011Hiwatashietal;licenseeBioMedCentralLtd.ThisisanOpenAccessarticledistributedunderthetermsoftheCreative",},
	{"CommonsAttributionLicense(http://creativecommons.org/licenses/by/2.0),whichpermitsunrestricteduse,distribution,and", },
	{"reproductioninanymedium,providedtheoriginalworkisproperlycited.", },
		};
		File file = TextFixtures.BMC_312_1_10SA_SVG;
		testSpans(values, file);
	}
	
	
	@Test
	public void testBoldWithSubscriptAndItalic() {
		String[][] values ={
	{"<B>Effectoflatepromoter</B>", "<B><I>p</I></B>", "<B><I>R</I></B>", "<B><I>’</I></B>", "<B>activity</B>"},
		};
		File file = TextFixtures.BMC_174_5_3SA_SVG;
		testSpans(values, file);
	}
	
	@Test
	public void testLargePara() {
		String[][] values ={
	{"seeminglyconvexrelationshipbetween", "<I>t</I>", "L", "-", "<I>t</I>", "KCN", "and"},
	{"<I>t</I>", "KCN", "[[39],theirfigurefive]."},
	{"Theeffectsof", "<I>t</I>", "KCN", "onlysistimeSDsandCVsare"},
	{"showninFigure4B.Again,wewitnessedtheexpected"}, 
	{"patternofasignificantnegativerelationshipbetween"},
	{"<I>t</I>", "KCN", "andtheSDs(aquadraticfit,", "<I>F</I>", "[2,4]", "=9.91,", "<I>p</I>", "="},
	// MISSINGLINE in the source
	{"CVs(aquadraticfit,", "<I>F</I>", "[2,4]", "=16.03,", "<I>p</I>", "=0.0282,adjusted", },
	{"<I>R</I>", "2", "=0.834).Theseresultsshowedthatthelaterintime"}, 
	{"KCNwasadded,thelessvariationtherewasinindivi-"}, 
	{"duallysistimes.Infact,thelowestSD(1.45min)and"},
	{"lowestCV(2.53%)wereobservedwhenKCNwasadded"},
    {"55minafterinduction.Thiswasasignificanttwo-fold"},
    {"reductionintheSDwhencomparednormallysiscondi-"}, 
    {"tions(seeTable1forstrainIN56withtheSD=3.24"}, 
    {"min;Student", "<B>’</B>", "s", "<I>t</I>", "=15.45,", "<I>p</I>", "<0.0001,usingthestandard"},
	{"deviationfortheSDinBox7.1of[56]).Thisobserva-"}, 
	{"tionindicatedthatindividualtriggeringforholeforma-"}, 
	{"tionduringthenormalprogressionofcelllysiswas"}, 
	{"relativelyasynchronouswhencomparedtotheartificial"}, 
	{"methodofacutetriggeringbyKCNaddition."},
	{"Similartotheeffectofgrowthrate,alinearregression"}, 
	{"oftheSDs(", "<I>F</I>", "[1,5]", "=0.60,", "<I>p</I>", "=0.4726)orCVs(", "<I>F</I>", "[1,5]", "="}, 
	{"0.328,", "<I>p</I>", "=0.5917)againsttheMLTsdidnotyieldsignif-"}, 
	{"icantresult.Anotherinterestingaspectoftherelation-"}, 
	{"shipbetween", "<I>t</I>", "KCN", "andthelysistimeSDsisthatthe"}, 
	{"SDsdropprecipitouslywhenKCNisaddedabout35"}, 
	{"minafterinduction.Thisobservationsuggeststhat,"}, 
	{"approximately35minafterthermalinduction,the"}, 
	{"majorityofthelysogeniccellshaveaccumulatedenough"}, 
	{"holinproteinsinthecellmembranetoformholes"},
	{"immediatelyiftriggered."}, 
	};
		File file = TextFixtures.BMC_174_6_3SA_SVG;
		testSpans(values, file);
	}
	
	@Test
	public void testGetHTML63() {
		File file = TextFixtures.BMC_174_6_3SA_SVG;
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(file);
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(file);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		List<List<StyleSpan>> styleSpanListList = sc.getStyleSpanListList();
		HtmlElement divElement = new HtmlDiv();
		for (int i = 0; i < styleSpanListList.size(); i++) {
			List<StyleSpan> styleSpanList = styleSpanListList.get(i);
			for (int j = 0; j < styleSpanList.size(); j++) {
				StyleSpan styleSpan = styleSpanList.get(j);
				HtmlElement htmlElement = styleSpan.getHtmlElement();
				divElement.appendChild(htmlElement);
			}
		}
		System.out.println(divElement.toXML());
	}

	// ==========================================================================================

	private void testSpans(String[][] values, File file) {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(file);
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(file);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		List<List<StyleSpan>> styleSpanListList = sc.getStyleSpanListList();
		Assert.assertEquals("lists", values.length, styleSpanListList.size());
		for (int i = 0; i < values.length; i++) {
			List<StyleSpan> styleSpanList = styleSpanListList.get(i);
			if (values[i].length > 0) {
				if (values[i].length != styleSpanList.size()) {
					for (int j = 0; j < styleSpanList.size(); j++) {
						System.out.println(styleSpanList.get(j).toString());
					}
				}
				Assert.assertEquals("line"+i, values[i].length, styleSpanList.size());
				for (int j = 0; j < values[i].length; j++) {
					Assert.assertEquals("line"+i+","+j, values[i][j], styleSpanList.get(j).toString());
				}
			}
		}
	}

	
	private void testScript(File svgFile, String[][] words) {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(svgFile);
		TextStructurer textContainer = 
				TextStructurer.createTextStructurerWithSortedLines(svgFile);
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textContainer, pageAnalyzer);
		List<ScriptLine> scriptLineList = sc.getScriptLineList();
		Assert.assertEquals("scriptLines", words.length, scriptLineList.size());
		for(int i = 0; i < words.length; i++) {
			ScriptLine scriptLine = scriptLineList.get(i);
			List<ScriptWord> scriptWords = scriptLine.getWords();
			if (words[i].length > 0) {
				if (words[i].length != scriptWords.size()) {
					for (int j = 0; j < scriptWords.size(); j++) {
						System.out.println(scriptWords.get(j).getRawValue());
					}
				}
				Assert.assertEquals("line"+i, words[i].length, scriptWords.size());
				for (int j = 0; j < scriptWords.size(); j++) {
					Assert.assertEquals(""+j, words[i][j], scriptWords.get(j).getRawValue());
				}
			}
		}
	}
}
