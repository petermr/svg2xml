package org.xmlcml.svg2xml.table;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlImg;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.html.HtmlTr;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.text.PhraseListList;
import org.xmlcml.svg2xml.text.SuscriptEditor;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Attribute;


//@Ignore
public class TableContentCreatorTest {
	
	static final Logger LOG = Logger.getLogger(TableContentCreatorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TABLE = "table";
	private static final String BORDER = "border";
	private static final String VERTICAL_ALIGN_TOP = "vertical-align:top";
	private static final double IMG_XSCALE = 700.0;
	private static final File CM_UCL_DIR = new File("../../cm-ucl");

	@Test
	public void testSimple1() {
		File inputFile = new File(Fixtures.TABLE_DIR, "grid/simple1.svg");
		File outDir = new File("target/table/grid/");
		// refactor this stack
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
//		Assert.assertEquals("cols",  5, tableContentCreator.getColumnCount());
//		Assert.assertEquals("rows",  7, tableContentCreator.getRowCount());
	}
	
	@Test
	public void testTable1() {
		File inputFile = new File(Fixtures.TABLE_DIR, "grid/table1.svg");
		File outDir = new File("target/table/grid/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}

	@Test
	public void testSimple2() {
		File inputFile = new File(Fixtures.TABLE_DIR, "grid/simple2.svg");
		File outDir = new File("target/table/grid/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}

	@Test
	public void testSimple3() {
		File inputFile = new File(Fixtures.TABLE_DIR, "grid/simple3.svg");
		File outDir = new File("target/table/grid/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
//		int rows = tableContentCreator.getRowCount();
//		Assert.assertEquals("rows",  8, rows);
//		int cols = tableContentCreator.getColumnCount();
//		Assert.assertEquals("cols",  5, cols);
	}

	@Test
	public void testSimple4() {
		File inputFile = new File(Fixtures.TABLE_DIR, "grid/simple4.svg");
		File outDir = new File("target/table/grid/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}
	
	
	// box	10.1016_S0140-6736(16)31461-1/
	// not yet solved
	@Test
	public void testBox1() {
		File inputFile = new File(Fixtures.TABLE_DIR, "box/table1.svg");
		File outDir = new File("target/table/box/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}
	
	@Test
	// 	10.1016_j.pain.2014.09.020
	public void testGlueTables() {
		File inputFile1 = new File(Fixtures.TABLE_DIR, "glue/table3.svg");
		File inputFile1cont = new File(Fixtures.TABLE_DIR, "glue/table3cont.svg");
		File inputFile1annot = new File(Fixtures.TABLE_DIR, "glue/table3.svg");
		File inputFile1annotcont = new File(Fixtures.TABLE_DIR, "glue/table3cont.svg");
		File outDir = new File("target/table/glue/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile1, outDir);
	}

	@Test
	/** simple rectangular Table.
	 * No subtables or split columns
	 * @throws IOException
	 */
	public void testSimple2Html() throws IOException {
//		cm-ucl/corpus-oa-pmr/10.1016_j.pain.2014.09.033/pdftable/table1.annot.svg
		File inputFile1 = new File(Fixtures.TABLE_DIR, "html/simple/10.1016_j.pain.2014.09.033.annot.svg");
//		10.1016_j.jadohealth.2016.10.001/pdftable/table3.annot.svg
//		File inputFile1 = new File(Fixtures.TABLE_DIR, "html/table3.annot.svg");
		File outDir = new File("target/table/html/simple");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.createHTML(inputFile1, outDir);
	}
	
	@Test
	@Ignore
	// spurious SVGText with whitespace content
	public void testWhitespaceProblem() {
		File inputFile = new File(Fixtures.TABLE_DIR, "whitespace/table1.svg");
		File outDir = new File("target/table/whitespace/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}

	@Test
	/** indents.
	 * No subscripts or split columns
	 * @throws IOException
	 */
	public void testIndents() throws IOException {
//		cm-ucl/corpus-oa-pmr/10.1186_1471-2431-13-190/pdftable/table1.annot.svg
		File inputFile1 = new File(Fixtures.TABLE_DIR, "indent/10.1186_1471-2431-13-190.annot.svg");
		File outDir = new File("target/table/indent/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.createHTML(inputFile1, outDir);
	}
	
	@Test
	/** split column/s
	 * No subscripts or indents
	 * @throws IOException
	 */
	public void testSplitColumn() throws IOException {
//		cm-ucl/corpus-oa-pmr/1	10.1179_1743132815Y.0000000050/pdftable/table5.annot.svg
		File inputFile1 = new File(Fixtures.TABLE_DIR, "splitcol/10.1179_1743132815Y.0000000050.annot.svg");
		File outDir = new File("target/table/splitcol/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.createHTML(inputFile1, outDir);
	}
	
	@Test
	/** subscript
	 * No split columns or indents
	 * complex suscripts in Footer - not yet resolved
	 * @throws IOException
	 */
	public void testSuscriptSVG() throws IOException {
//		cm-ucl/corpus-oa-pmr/10.1371_journal.pbio.1000481/pdftable/table1.annot.svg
		File inputFile1 = new File(Fixtures.TABLE_DIR, "suscript/10.1371_journal.pbio.1000481.svg");
		File outDir = new File("target/table/suscript/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile1, outDir);
		PhraseListList phraseListList = new PhraseListList(tableContentCreator.getTableFooter().getOrCreatePhraseLists());
		SuscriptEditor suscriptEditor = new SuscriptEditor(phraseListList);
		suscriptEditor.mergeAll();
		LOG.debug("PLL"+phraseListList);
	}
	
	@Test
	/** subscript
	 * No split columns or indents
	 * isolated superscripts
	 * @throws IOException
	 */
	public void testSuscriptSVG1() throws IOException {
		File inputFile1 = new File(Fixtures.TABLE_DIR, "suscript/10.1007_s00213-015-4198-1.svg");
		File outDir = new File("target/table/suscript/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile1, outDir);
		PhraseListList phraseListList = new PhraseListList(tableContentCreator.getTableFooter().getOrCreatePhraseLists());
		LOG.debug(phraseListList.toString());
		Assert.assertEquals(5, phraseListList.size());
		SuscriptEditor suscriptEditor = new SuscriptEditor(phraseListList);
		suscriptEditor.mergeAll();
		LOG.debug("PLL"+phraseListList);
		XMLUtil.debug(phraseListList.toHtml(), new File(outDir, FilenameUtils.getBaseName(inputFile1.toString())+".html"), 1);
	}
	
	
	@Test
	/** subscript
	 * No split columns or indents
	 * @throws IOException
	 */
	public void testSuscriptHTML() throws IOException {
//		cm-ucl/corpus-oa-pmr/10.1371_journal.pbio.1000481/pdftable/table1.annot.svg
		File inputFile1 = new File(Fixtures.TABLE_DIR, "suscript/10.1371_journal.pbio.1000481.annot.svg");
		File outDir = new File("target/table/suscript/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.createHTML(inputFile1, outDir);
	}
	
	
	/** production
	 * 
	 */
	@Test
	public void testCreateSVGFromPDF() throws IOException {
		File pdfOrigDir = new File(CM_UCL_DIR, "corpus-oa");
		File pmrDir = new File(CM_UCL_DIR, "corpus-oa-pmr");
		pmrDir.mkdirs();
		File[] pdfDirs = pdfOrigDir.listFiles();
		for (File pdfDir : pdfDirs) {
			File pmrPdfDir = new File(pmrDir, pdfDir.getName());
			pmrPdfDir.mkdirs();
			File pdfFile = new File(pdfDir, "fulltext.pdf");
			File svgDir = new File(pmrPdfDir, "svg/");
			svgDir.mkdirs();
			new PDF2SVGConverter().run(
					"-logger", "-infofiles", "-logglyphs", "-outdir", svgDir.toString(), pdfFile.toString());
			File pngDir = new File(pmrPdfDir, "png/");
			List<File> pngs = new ArrayList<File>(FileUtils.listFiles(svgDir, new String[] {"png"}, false));
			for (File png : pngs) {
				if (FileUtils.sizeOf(png) == 0) {
					png.delete();
				} else {
					try {
						FileUtils.moveToDirectory(png, pngDir, true);
					} catch (Exception e) {
						LOG.error("cannot move file: "+e);
					}
				}
			}
		}
	}
	
	@Test
	/** iterates over CProject and marksup tables
	 * adds visible boxes for detected regions and adds titles
	 * 
	 */
	public void testMarkupTables() {
		File pmrDir = new File(CM_UCL_DIR, "corpus-oa-pmr");
		File chDir = new File(CM_UCL_DIR, "corpus-oa");
		File[] ctrees = pmrDir.listFiles();
		for (File ctree : ctrees) {
			LOG.debug("*************"+ctree+"**************");
			File svgDir = new File(ctree, "svg/");
			List<File> svgFiles = new ArrayList<File>(FileUtils.listFiles(svgDir, new String[]{"svg"}, false));
			File tableDir = new File(ctree, "pdftable/");
			tableDir.mkdirs();
			for (File svgFile : svgFiles) {
				if (svgFile.getName().startsWith("table")) {
					LOG.debug("============="+svgFile+"=============");
					TableContentCreator tableContentCreator = new TableContentCreator(); 
					tableContentCreator.markupAndOutputTable(svgFile, tableDir);
					String root = FilenameUtils.getBaseName(svgFile.toString());
					File annotSvg = new File(tableDir, root+TableContentCreator.DOT_ANNOT_SVG);
					try {
						tableContentCreator.createHTML(annotSvg, tableDir);
					} catch (IOException e) {
						LOG.debug("Cannot write html: "+e);
					}
				}
			}
		}
	}
	
	@Test
	public void testCreateDoubleTableHTML() throws IOException {
		double TD_WIDTH = 500.0;
		File pmrDir = new File(CM_UCL_DIR, "corpus-oa-pmr");
		File chDir = new File(CM_UCL_DIR, "corpus-oa");
		Assert.assertTrue(pmrDir.getAbsoluteFile().toString()+" exists", pmrDir.exists());
		File[] ctrees = pmrDir.listFiles();
		for (File ctree : ctrees) {
			String root = ctree.getName();
			File chTree = new File(chDir, root+"/");
			File pmrTree = new File(pmrDir, root+"/");
			File pmrImageDir = new File(pmrTree, "image/");
			File pdfTableDir = new File(pmrTree, "pdftable/");
			List<File> pngs = new ArrayList<File>(FileUtils.listFiles(chTree, new String[]{"png"}, false));
			for (File png : pngs) {
				if (png.getName().startsWith(TABLE)) {

					String pngName = png.getName();
					String pngRoot = pngName.substring(0, pngName.length() - ".png".length());
					File pngFile = new File(pmrImageDir, pngName);
					BufferedImage image = ImageIO.read(pngFile);
					int imgWidth = image.getWidth();
					int imgHeight = image.getHeight();
					String pngRootSuffix = pngRoot.substring(TABLE.length());
					File svgAnnotFile1 = new File(pdfTableDir, pngRoot+TableContentCreator.DOT_ANNOT_SVG);
					
					HtmlHtml html = new HtmlHtml();
					HtmlTable table = new HtmlTable();
					table.setAttribute(BORDER, "1");
					html.appendChild(table);
					HtmlTr row = new HtmlTr();
					table.appendChild(row);
					
					addImageToRow(TD_WIDTH, png, imgWidth, imgHeight, row);
					addSvgToRow(TD_WIDTH, svgAnnotFile1, row);
					
					XMLUtil.debug(html, new File(pdfTableDir, "doubleTable"+pngRootSuffix+".html"), 1);
				}
			}
		}
	}
	
	@Test
	/** copies key directories for display and creates CSV file.
	 * 
	 */
	public void testCreateTableShow() throws IOException {
		String root = CM_UCL_DIR.toString();
		String srcRoot = root+"/corpus-oa-pmr/";
        Path pmrFolder = Paths.get(srcRoot);
        String showRoot = root+"/corpus-oa-pmr-show/";
        Path showPath = Paths.get(showRoot);
        StringBuilder csvBuilder = new StringBuilder("CTree,table,\n");
        Files.walkFileTree(pmrFolder, new CopyFileVisitor(csvBuilder, srcRoot, showPath));
        FileUtils.write(new File(new File(root), "tables.csv"), csvBuilder.toString());
    }
	
	// ===================================

	private void addSvgToRow(double TD_WIDTH, File svgAnnotFile1, HtmlTr row) {
		HtmlTd svgCell = new HtmlTd();
		row.appendChild(svgCell);
		
		svgCell.setWidth(TD_WIDTH);
		HtmlDiv div = new HtmlDiv();
		svgCell.appendChild(div);
		svgCell.setStyle(VERTICAL_ALIGN_TOP);
		SVGElement svg = null;
		try {
			svg = SVGUtil.parseToSVGElement(new FileInputStream(svgAnnotFile1));
		} catch (FileNotFoundException fnfe) {
			LOG.debug("no SVG file:"+svgAnnotFile1);
		}
		if (svg != null) {
			SVGG g = new SVGG();
			XMLUtil.transferChildren(svg, g);
			svg.appendChild(g);
			Real2Range bbox = svg.getBoundingBox();
			double scale = TD_WIDTH / bbox.getXRange().getRange();
			Real2 corner = bbox.getCorners()[0];
			String transform = "scale("+scale+") ";
			transform += " translate("+(-1.0*corner.getX())+","+(-1.0*corner.getY())+")";
			g.addAttribute(new Attribute("transform", transform));
			SVGRect bboxRect = SVGRect.createFromReal2Range(bbox);
			bboxRect.setStroke("black");
			bboxRect.setStrokeWidth(2.);
			bboxRect.setFill("none");
			g.appendChild(bboxRect);
			div.appendChild(svg);
		}
	}

	private void addImageToRow(double TD_WIDTH, File png, int imgWidth, int imgHeight, HtmlTr row) {
		HtmlTd imageCell = new HtmlTd();
		row.appendChild(imageCell);
		imageCell.setStyle(VERTICAL_ALIGN_TOP);
		HtmlImg img = new HtmlImg();
		imageCell.appendChild(img);
		img.setSrc("../image/"+png.getName());
		img.setWidth(TD_WIDTH);
		img.setHeight(TD_WIDTH * (double) imgHeight / (double) imgWidth);
		img.setStyle(VERTICAL_ALIGN_TOP);
	}
}
class CopyFileVisitor extends SimpleFileVisitor<Path> {
	private static final Logger LOG = Logger.getLogger(CopyFileVisitor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

    private final Path targetPath;
    private Path sourcePath = null;
	private String srcRoot;
	private StringBuilder csvBuilder;
    
    public CopyFileVisitor(StringBuilder csvBuilder, String srcRoot, Path targetPath) {
        this.targetPath = targetPath;
        this.srcRoot = srcRoot;
        this.csvBuilder = csvBuilder;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir,
    		final BasicFileAttributes attrs) throws IOException {
    	File dirFile = dir.toFile();
    	String name = dirFile.getName();
        if (sourcePath == null) {
            sourcePath = dir;
        } else if (name.equals("png") || name.equals("svg")) {
        	LOG.trace("skipped dir "+dir);
            return FileVisitResult.SKIP_SUBTREE;
        } else if (name.equals("image") || name.equals("pdftable")) {
        	Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
        } else {
        	String filename = dir.toString().substring(srcRoot.length());
        	csvBuilder.append(String.valueOf(filename)+","+
        			","+"\n");
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file,
	    final BasicFileAttributes attrs) throws IOException {
    	File fileFile = file.toFile();
		Path targetFile = targetPath.resolve(sourcePath.relativize(file));
		boolean copy = true;
		if (fileFile.getName().endsWith(TableContentCreator.DOT_ANNOT_SVG)) {
			// skip SVG
			copy = false;
			LOG.debug("skipped "+file);
		} else if (!targetFile.toFile().exists()) {
			Files.copy(file, targetFile);
		}
		if (copy) {
        	if (!file.toFile().getName().endsWith(".png")) {
        		csvBuilder.append(String.valueOf(
        			file.toFile().getParentFile()).substring(srcRoot.length())+","+
        			file.toFile().getName()+","+"\n");
        	}
		}
	    return FileVisitResult.CONTINUE;
    }
}

