package org.xmlcml.svg2xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.IntRangeArray;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTr;
import org.xmlcml.svg2xml.table.TableContentCreator;
import org.xmlcml.svg2xml.table.TableSection;
import org.xmlcml.svg2xml.table.TableStructurer;
import org.xmlcml.svg2xml.table.TableTitle;
import org.xmlcml.svg2xml.text.HorizontalElement;
import org.xmlcml.svg2xml.text.HorizontalRuler;
import org.xmlcml.xml.XMLUtil;


public class TableDemos {

	
	public static final String IMAGE_G = "image.g";
	public static final Logger LOG = Logger.getLogger(TableDemos.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static File CLINICAL_DIR = new File("demos/clinical/svg");
	private final static File ACR_DIR = new File(CLINICAL_DIR, "acr");
	private final static File ADA_DIR = new File(CLINICAL_DIR, "ada");
	private final static File AHA_DIR = new File(CLINICAL_DIR, "aha");
	private final static File AMA_DIR = new File(CLINICAL_DIR, "ama");
	private final static File APA_DIR = new File(CLINICAL_DIR, "apa");
	private final static File BLK_DIR = new File(CLINICAL_DIR, "blk");
	private final static File BMC_DIR = new File(CLINICAL_DIR, "bmc");
	private final static File BMJ_DIR = new File(CLINICAL_DIR, "bmj");
	private final static File ELS_DIR = new File(CLINICAL_DIR, "els");
	private final static File EVJ_DIR = new File(CLINICAL_DIR, "evj");
	private final static File LANCET_DIR = new File(CLINICAL_DIR, "lancet");
	private final static File LB_DIR = new File(CLINICAL_DIR, "lb");
	private final static File LPW_DIR = new File(CLINICAL_DIR, "lpw");
	private final static File LWW_DIR = new File(CLINICAL_DIR, "lww");
	private final static File NATURE_DIR = new File(CLINICAL_DIR, "nature");
	private final static File NEJM_DIR = new File(CLINICAL_DIR, "nejm");
	private final static File OUP_DIR = new File(CLINICAL_DIR, "oup");
	private final static File PLOS_DIR = new File(CLINICAL_DIR, "plos");
	private final static File SPR_DIR = new File(CLINICAL_DIR, "spr");
	private final static File TEX_DIR = new File(CLINICAL_DIR, "tex");
	private final static File WILEY_DIR = new File(CLINICAL_DIR, "wiley");
	private final static File WK_DIR = new File(CLINICAL_DIR, "wk");
	
	private final static File TARGET_TABLE_DIR = new File("target/table");
	
	private static final File ACR_OUT_DIR = new File(TARGET_TABLE_DIR, "ACR65481");
	private static final File ADA_OUT_DIR = new File(TARGET_TABLE_DIR, "ADA_PH1");
	private static final File AHA_OUT_DIR = new File(TARGET_TABLE_DIR, "AHA_PH2");
	private static final File AMA_OUT_DIR = new File(TARGET_TABLE_DIR, "AMA_Dobson2013_1");

	@Test
	/** iterates over all "image.g.d.d.svg" in directory and identifies those with "Table d"
	 * 
	 */
	public void testFindTable() {
		File svgDir = ELS_DIR;
		String[] roots = {
				"0415", 
				"1092", 
				"1323", 
				"1967",
				};
		TableTitle[][] tableTitles = new TableTitle[][] {
			new TableTitle[]{
					new TableTitle("Table 4", "image.g.10.2.svg"),
					new TableTitle("Table 5", "image.g.11.2.svg"),
					new TableTitle("Table 1", "image.g.4.4.svg"),
					new TableTitle("Table 2", "image.g.6.2.svg"),
					new TableTitle("Table 3", "image.g.6.2.svg"),
			},
			new TableTitle[]{
					new TableTitle("Table 2", "image.g.10.2.svg"),
					new TableTitle("Table 3", "image.g.10.2.svg"),
					new TableTitle("Table 4", "image.g.11.2.svg"),
					new TableTitle("Table 4 (continued)", "image.g.12.2.svg"),
					new TableTitle("Table 5", "image.g.13.2.svg"),
					new TableTitle("Table 5 (continued)", "image.g.14.2.svg"),
					new TableTitle("Table 1", "image.g.8.2.svg"),
			},
			new TableTitle[]{
					new TableTitle("Table 1", "image.g.4.2.svg"),
					new TableTitle("Table 2", "image.g.5.2.svg"),
					new TableTitle("Table 3", "image.g.7.2.svg"),
					new TableTitle("Table 3 (continued)", "image.g.8.2.svg"),
			},
			new TableTitle[]{
					new TableTitle("Table 1", "image.g.3.7.svg"),
					new TableTitle("Table 2", "image.g.5.2.svg"),
			},
				
		};
		
		TableContentCreator tableContentCreator = new TableContentCreator();
		for (int r = 0; r < roots.length; r++) {
			String root = roots[r];
			LOG.debug("====>"+root+"<===");
			List<File> svgChunkFiles = filterChunkFiles(svgDir, root);
			List<TableTitle> tableTitleList = tableContentCreator.findTableTitles(svgChunkFiles);
			for (int i = 0; i < tableTitles[r].length; i++) {
				Assert.assertEquals(tableTitles[r][i].toString(), tableTitleList.get(i).toString().trim());
			}

		}
	}
	
	@Test
	public void testCreateTables0415() throws IOException {
		File svgDir = ELS_DIR;
		String root = "0415";
		List<TableTitle> tableTitles = Arrays.asList(new TableTitle[] {
			new TableTitle("Table 1", "image.g.4.4.svg"),
			new TableTitle("Table 2", "image.g.6.2.svg"),
			new TableTitle("Table 3", "image.g.6.2.svg"),
			new TableTitle("Table 4", "image.g.10.2.svg"),
			new TableTitle("Table 5", "image.g.11.2.svg"),
		});
		int[] listLengths = {31, 99, 99, 21, 63};
		int[] starts = {16, 0, 83, 0, 0};
		int[] fullRulers = {
				3,
				4, // extra body section
				3,
				3,
				4, // extra body section
				};
		String[] rangeString = {
				"[(0,2)(2,3)(3,5)(5,5)]",
				"[(0,1)(1,2)(2,10)(10,23)(23,57)]",
				"[(0,1)(1,3)(3,12)(12,12)]",
				"[(0,1)(1,3)(3,16)(16,16)]",
				"[(0,1)(1,2)(2,10)(10,27)(27,34)]",
		};
		IntRangeArray[] rangesArray1 = new IntRangeArray[]{
			new IntRangeArray(Arrays.asList(
					new IntRange(0,2),
					new IntRange(2,2),
					new IntRange(2,3),
					new IntRange(3,6),
					new IntRange(6,6)
					)),
			
		};
		
		for (int i = 0; i < tableTitles.size(); i++) {
			TableTitle tableTitle = tableTitles.get(i);
			LOG.debug("======================"+tableTitle.getChunkName()+"========================");
			File inputFile = new File(svgDir, root+"/"+tableTitle.getChunkName());
			TableContentCreator tableContentCreator = new TableContentCreator();
			tableContentCreator.setTableTitle(tableTitle);
			tableContentCreator.createContent(inputFile);
			tableContentCreator.createSectionsAndRangesArray();
			IntRangeArray rangesArray = tableContentCreator.getRangesArray();
			LOG.debug(rangesArray);
//			Assert.assertEquals("range "+i, rangeString[i].toString(), rangesArray.toString());
//			rangesArray = tableContentCreator.getRangesArrayWithPseudoHeader();
//			LOG.debug(rangesArray);
//			rangesArray = rangesArray1[i];
			
			List<HorizontalElement> horizontalList = tableContentCreator.getHorizontalList();
			Assert.assertEquals(listLengths[i], horizontalList.size());
			int iRow = tableContentCreator.search(tableTitle.getTitle());
			Assert.assertEquals(starts[i],  iRow);
			List<HorizontalRuler> fullRulerList = tableContentCreator.getFullRulers(iRow);
			Assert.assertEquals("ruler:"+i, fullRulers[i],  fullRulerList.size());

			List<TableSection> sectionList = tableContentCreator.getTableSectionList();
			LOG.debug("ranges "+sectionList);
			HtmlHtml html = TableStructurer.createHtmlWithTable(inputFile, sectionList, tableTitle);
			if (html == null) {
				LOG.error("Cannot create table: "+tableTitle);
			} else {
				File outfile = new File("target/table/"+tableTitle.getChunkName()+".html");
				LOG.debug("writing: "+outfile);
				XMLUtil.debug(html, outfile, 1);
			}
		}
	}
	
	@Test
	/** iterates over several articles analyzing all image files
	 *  "image.g.d.d.svg" in directory and identifies those with "Table d"
	 * 
	 */
	public void testFindTablesInArticlesELS() {
		String[] roots = {
				"0415", 
				"1092", 
				"1323", 
				"1967",
				};

		createHTML(ELS_DIR, roots);
	}

	@Test
	@Ignore // files not yet in directory
	/** iterates over several articles analyzing all image files
	 *  "image.g.d.d.svg" in directory and identifies those with "Table d"
	 * 
	 */
	public void testFindTablesInArticlesNEJM() {
		String[] roots = {
				"1505126",
				"1506699",
				"1507062"
				};

		createHTML(NEJM_DIR, roots);
	}

	@Test
	/** iterates over many publishers analyzing all image files
	 *  "image.g.d.d.svg" in directory and identifies those with "Table d"
	 * 
	 */
	public void testFindTablesInArticlesManyPublishers() {
		String[] roots;
		
		// tree columns and subscripts and non-Unicode maths
		roots = new String[]{
				"ACR65481",
				};
		createHTML(ACR_DIR, roots);
		assertRowsColumns(new File(ACR_OUT_DIR, "Table1..image.g.7.2.svg.html"), 46, 4);

		// commented out to save time
		
//		// not checked for correctness
//		// 2 out of 3 columns
//		roots = new String[]{
//				"ADA_PH1",
//				};
//		createHTML(ADA_DIR, roots);
//		assertRowsColumns(new File(ADA_OUT_DIR, "Table1.image.g.3.0.svg.html"), 81, 3);
//		assertRowsColumns(new File(ADA_OUT_DIR, "Table2.image.g.3.0.svg.html"), 81, 6);
//		
//		roots = new String[]{
//				"AMA_Dobson2013_1",
//				};
//		createHTML(AMA_DIR, roots);
//		// APA NYI
//		roots = new String[]{
//				"BLK_JPR52758",
//				"BLK_SAM55371",
//				};
//		createHTML(BLK_DIR, roots);
//
//		roots = new String[]{
//				"BMC73226",
//				};
//		createHTML(BMC_DIR, roots);
//
//		roots = new String[]{
//				"BMJ312529",
//				"BMJBollard312268",
//				};
//		createHTML(BMJ_DIR, roots);
//
//		roots = new String[]{
//				"0415",
//				"1092",
//				"1323",
//				"1967",
//				"ELS_Petaja2009"
//				};
//		createHTML(ELS_DIR, roots);
//		
//		roots = new String[]{
//				"EVJ62903",
//				};
//		createHTML(EVJ_DIR, roots);
//		// "Table" at bottom
//		roots = new String[]{
//				"LANCET16302844",
//				};
//		createHTML(LANCET_DIR, roots);
//
//		roots = new String[]{
//				"LPW_Reisinger2007",
//				};
//		createHTML(LPW_DIR, roots);
//
//		roots = new String[]{
//				"LWW61463",
//				};
//		createHTML(LWW_DIR, roots);
//
//		roots = new String[]{
//				"NATUREsrep29540",
//				};
//		createHTML(NATURE_DIR, roots);
//		// SOME missing
//
//		roots = new String[]{
//				"NEJMOA1411321",
//				};
//		createHTML(NEJM_DIR, roots);
//
//		roots = new String[]{
//				"OUP_PH3",
//				};
//		createHTML(OUP_DIR, roots);
//		// FAILS to clip correctly
//
//		roots = new String[]{
//				"PLOS57170",
//				};
//		createHTML(PLOS_DIR, roots);
//
//		roots = new String[]{
//				"SPR57530",
//				"SPR68755"
//				};
//		createHTML(SPR_DIR, roots);
////		// TEX omitted
//		roots = new String[]{
//				"Wiley44386",
//				};
//		createHTML(WILEY_DIR, roots);
//
//		roots = new String[]{
//				"WK_Vesikari2015",
//				};
//		createHTML(WK_DIR, roots);
		
	}
	
	@Test
	@Ignore
	public void testFindTablesInArticlesProblems() {
		String[] roots;

		roots = new String[]{
		"AHA_PH2",
		};
createHTML(AHA_DIR, roots);

		roots = new String[]{
		"LB_HV_Romanowski2011_1",
		};
createHTML(LB_DIR, roots);

		
	}

	private void assertRowsColumns(File htmlFile, int rowCount, int colCount) {
		Assert.assertTrue(""+htmlFile, htmlFile.exists());
		HtmlTable htmlTable = (HtmlTable) HtmlElement.create(XMLUtil.getQueryElements(XMLUtil.parseQuietlyToDocument(htmlFile), 
				".//*[local-name()='table']").get(0));
		List<HtmlTr> rows = htmlTable.getRows();
		Assert.assertEquals(rowCount,  rows.size());
		HtmlTr row0 = rows.get(0);
		int cols = row0.getTdChildren().size()+row0.getThChildren().size();
		Assert.assertEquals(colCount,  cols);
	}
	
	@Test
	public void testTidyHtmlWiley() throws IOException {
		String root = "Wiley44386";
		String filename = "image.g.4.1.svg";
		File svgFile = new File(WILEY_DIR, "Wiley44386/"+filename);
		TableContentCreator tableContentCreator = new TableContentCreator();
		tableContentCreator.setTableTitle(new TableTitle("TABLE 1.", filename));
		tableContentCreator.setAddIndents(true);
		HtmlHtml html = tableContentCreator.createHTMLFromSVG(svgFile);
		HtmlTable table = (HtmlTable) XMLUtil.getQueryElements(html, ".//*[local-name()='table']").get(0);
		Assert.assertNotNull(html);
		XMLUtil.debug(html, new File("target/table/"+root+"/"+filename+".html"), 1);
	}

	// ==============================================
	
	private void createHTML(File svgDir, String[] roots) {
		for (int r = 0; r < roots.length; r++) {			
			String root = roots[r];
			LOG.debug("====>"+root+"<===");
			List<File> svgChunkFiles = filterChunkFiles(svgDir, root);
			TableContentCreator tableContentCreator = new TableContentCreator();
			List<TableTitle> tableTitleList = tableContentCreator.findTableTitles(svgChunkFiles);
			for (int i = 0; i < tableTitleList.size(); i++) {
				TableTitle tableTitle = tableTitleList.get(i);
				LOG.debug("======================"+tableTitle.getChunkName()+"========================");
				File inputFile = new File(svgDir, root+"/"+tableTitle.getChunkName());
				tableContentCreator = new TableContentCreator(); //refresh to make sure
				tableContentCreator.setTableTitle(tableTitle);
				HtmlHtml html = tableContentCreator.createHTMLFromSVG( inputFile);
				if (html == null) {
					LOG.error("Cannot create table: "+tableTitle);
				} else {
					File outfile = new File("target/table/"+root+"/"+tableTitle.getTitle().replaceAll("\\s", "")+
							"."+tableTitle.getChunkName()+".html");
					LOG.debug("writing: "+outfile);
					try {
						XMLUtil.debug(html, outfile, 1);
					} catch (IOException e) {
						LOG.error("Cannot write file: "+outfile+" ("+e+")");
						continue;
					}
				}
			}
		}
	}

	

	// ==========================================
	
	private List<File> filterChunkFiles(File svgDir, String root) {
		File inputDir = new File(svgDir, root+"/");
		LOG.debug("analysing directory: "+inputDir);
		List<File> svgChunkFiles = new ArrayList<File>(FileUtils.listFiles(inputDir, new String[] {"svg"},  true));
		for (int i = svgChunkFiles.size() - 1; i >= 0; i--) {
			if (!svgChunkFiles.get(i).getName().startsWith(IMAGE_G)) {
				svgChunkFiles.remove(i);
			}
		}
		return svgChunkFiles;
	}

}
