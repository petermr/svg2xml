package org.xmlcml.svg2xml.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGTitle;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlStyle;
import org.xmlcml.html.HtmlTitle;
import org.xmlcml.svg2xml.action.PageEditorX;
import org.xmlcml.svg2xml.action.SVGPlusConstantsX;
import org.xmlcml.svg2xml.action.SemanticDocumentActionX;
import org.xmlcml.svg2xml.container.AbstractContainer;
import org.xmlcml.svg2xml.tools.Chunk;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

/**
 * @author pm286
 *
 */
public class PageAnalyzer extends AbstractAnalyzer {


	private static final Logger LOG = Logger.getLogger(PageAnalyzer.class);
	
	public final static Pattern PATTERN = null;
	public final static String TITLE = "PAGE";
	
	private int pageNumber;
	private SVGSVG svgPage;
	private PDFAnalyzer pdfAnalyzer; 
	private List<AbstractContainer> pageAnalyzerContainerList;

	private static final String CHUNK = "chunk";
	public static final String PAGE = "page";

	private List<SVGG> gOutList;
	private int humanPageNumber;
	private int aggregatedContainerCount;
	
	public PageAnalyzer(PDFAnalyzer pdfAnalyzer, int pageCounter) {
		this.pdfAnalyzer = pdfAnalyzer;
		this.pageNumber = pageCounter;
	}

	public PageAnalyzer(SVGSVG svgPage) {
		this.svgPage = svgPage;
	}

	public void analyze() {
	}
	
	@Override
	public SVGG oldAnnotateChunk() {
		throw new RuntimeException("annotate NYI");
	}
	
	public Integer indexAndLabelChunk(String content, ChunkId id) {
		Integer serial = super.indexAndLabelChunk(content, id);
		// index...
		return serial;
	}
	
	SVGSVG splitChunksAnnotateAndCreatePage() {
 		List<SVGElement> gList = createSVGGList();
		
		humanPageNumber = pageNumber+1;
		svgPage = createBlankSVGPageWithNumberAndSize(humanPageNumber);
		gOutList = new ArrayList<SVGG>();
		for (int ichunk = 0; ichunk < gList.size(); ichunk++) {
			List<? extends AbstractContainer> newContainerList = new ArrayList<AbstractContainer>();
			SVGG gOrig = (SVGG) gList.get(ichunk);
			AbstractAnalyzer analyzerX = AbstractAnalyzer.createSpecificAnalyzer(gOrig);
			analyzerX.setPageAnalyzer(this);
			newContainerList = 	analyzerX.createContainers(this);
			annotateAndOutput(newContainerList, analyzerX);
		}
		for (SVGG g : gOutList) {
			svgPage.appendChild(g);
		}
		return svgPage;
	}

	private void annotateAndOutput (List<? extends AbstractContainer> newContainerList, AbstractAnalyzer analyzer) {
		ensureAggregatedContainerList();
		Character cc = 'a';
		for (AbstractContainer newContainer : newContainerList) {
			ChunkId chunkId = new ChunkId(humanPageNumber, aggregatedContainerCount);
			getPageAnalyzerContainerList().add(newContainer);
			SVGG gOut = newContainer.createSVGGChunk();
			gOut = annotateChunkAndAddIdAndAttributes(gOut, chunkId, analyzer, PageEditorX.DECIMAL_PLACES);
			String filename = CHUNK+humanPageNumber+"."+  
				    (aggregatedContainerCount)+newContainer.getSuffix()+String.valueOf(cc);
			SVG2XMLUtil.writeToSVGFile(pdfAnalyzer.getExistingOutputDocumentDir(), filename, gOut, false);
			cc++;
			gOutList.add(gOut);
		}
		aggregatedContainerCount++;
	}

	private void ensureAggregatedContainerList() {
		if (getPageAnalyzerContainerList() == null) {
			setPageAnalyzerContainerList(new ArrayList<AbstractContainer>());
		}
	}

	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	@Override
	protected Pattern getPattern() {
		return PATTERN;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

	private SVGG annotateChunkAndAddIdAndAttributes(SVGG gOrig, ChunkId chunkId, AbstractAnalyzer analyzerX, int decimalPlaces) {
		if (analyzerX == null) {
			throw new RuntimeException("Null analyzer");
		}
		List<SVGElement> gList = SVGUtil.getQuerySVGElements(gOrig, "//svg:g");
		SVGG gOut = analyzerX.annotateChunk(gList);
		gOut.setId(chunkId.toString());
		CMLUtil.copyAttributes(gOrig, gOut);
		gOut.format(decimalPlaces);
		return gOut;
	}

	private String createPageRoot(int pageNumber) {
		String pageRoot = PAGE+(pageNumber+1);
		return pageRoot;
	}

	private SVGSVG createBlankSVGPageWithNumberAndSize(int pageNumber) {
		SVGSVG svgOut = new SVGSVG();
		svgOut.setWidth(600.0);
		svgOut.setHeight(800.0);
		String pageId = "p."+pageNumber;
		svgOut.setId(pageId);
		return svgOut;
	}

	void writeSVGPage(File outputDocumentDir) {
		try {
			String pageRoot = createPageRoot(pageNumber);
			outputDocumentDir.mkdirs();
			String id = svgPage.getId();
			LOG.trace("ID "+id);
			CMLUtil.debug(
				svgPage, new FileOutputStream(new File(outputDocumentDir, pageRoot+SVGPlusConstantsX.DOT_SVG)), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * <title stroke="black" stroke-width="1.0">char: 981; name: null; f: Symbol; fn: PHHOAK+Symbol; e: Dictionary</title>
	 * @param svg
	 */
	private void processNonUnicodeCharactersInTitles(SVGSVG svg) {
		List<SVGElement> textTitles = SVGUtil.getQuerySVGElements(svg, ".//svg:title");
		for (SVGElement t : textTitles) {
			SVGTitle title = (SVGTitle) t;
			String s = title.getValue();
			String[] chunks =s.split(";");
			Integer ss = null;
			for (String chunk : chunks) {
				String[] sss = chunk.split(":");
				if (sss[0].equals("char") && !sss[1].equals("null")) {
					ss = new Integer(sss[1].trim());
					break;
				}
				if (sss[0].equals("name") && !sss[1].equals("null")) {
//					ss = sss[1];
					ss = 127;
					break;
				}
			}
			SVGElement text = ((SVGElement)title.getParent());
			int cc =text.getChildCount();
			for (int i = 0; i < cc; i++) {
				text.getChild(0).detach();
			}
			char c =  (char)(int)ss;
			LOG.trace("> "+c);
			try {
				text.appendChild(""+c);
			} catch (Exception e) {
				LOG.trace("skipped problem character: "+(int)c);
			}
//			text.debug("XX");
		}
	}
	
	private List<SVGElement> createSVGGList() {
		String pageRoot = createPageRoot(pageNumber);
		// messy
		String pageSvg = pageRoot+SVGPlusConstantsX.DOT_SVG;
		if (!pdfAnalyzer.fileRoot.equals("")) {
			pageSvg = pdfAnalyzer.fileRoot+"-"+pageSvg;
		} else {
			pageSvg = "target"+"-"+pageRoot+SVGPlusConstantsX.DOT_SVG;
		}
		LOG.trace(pageNumber+" "+pageRoot+" "+pageSvg);
		File svgPageFile = new File(pdfAnalyzer.svgDocumentDir, pageSvg);
		LOG.debug("reading SVG "+svgPageFile);
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(svgPageFile); // moderately expensive
		processNonUnicodeCharactersInTitles(svg);
		SemanticDocumentActionX semanticDocumentAction = 
				SemanticDocumentActionX.createSemanticDocumentActionWithSVGPage(svg);
		List<Chunk> chunkList = 
				WhitespaceChunkerAnalyzerX.chunkCreateWhitespaceChunkList(semanticDocumentAction);
		WhitespaceChunkerAnalyzerX.drawBoxes(chunkList, "red", "yellow", 0.5);
		List<SVGElement> gList = SVGG.generateElementList(svg, "svg:g/svg:g/svg:g[@edge='YMIN']");
		
		return gList;
	}
	

	void annotatePage() {
		List<SVGG> gList = SVGG.extractGs(SVGUtil.getQuerySVGElements(svgPage, ".//svg:g[@id]"));
		for (SVGG g : gList) {
			ChunkId chunkId = new ChunkId(g.getId());
			boolean indexed = pdfIndex.getUsedIdSet().contains(chunkId);
			LOG.trace("ID written "+chunkId+" "+indexed);
			if (indexed) {
				Real2Range bbox = g.getBoundingBox();
				Real2[] corners = bbox.getCorners();
				SVGLine line = new SVGLine(corners[0], corners[1]);
				line.setOpacity(0.3);
				line.setWidth(5.0);
				line.setFill("green");
				g.appendChild(line);
			}
		}
	}

	public void add(AbstractContainer container) {
		ensureContainerList();
		getPageAnalyzerContainerList().add(container);
	}

	private void ensureContainerList() {
		if (getPageAnalyzerContainerList() == null) {
			setPageAnalyzerContainerList(new ArrayList<AbstractContainer>());
		}
	}

	public String summaryString() {
		StringBuilder sb = new StringBuilder("Page: "+pageNumber+"\n");
		sb.append("Containers: "+getPageAnalyzerContainerList().size()+"\n");
		for (AbstractContainer container : getPageAnalyzerContainerList()) {
			sb.append(container.summaryString()+"\n........................\n");
		}
		return sb.toString();
	}
	
	protected HtmlElement createHtml() {
		HtmlHtml html = new HtmlHtml();
		addStyle(html);
		HtmlTitle title = new HtmlTitle("Page: "+pageNumber);
		html.appendChild(title);
		HtmlBody body = new HtmlBody();
		html.appendChild(body);
		HtmlDiv div = new HtmlDiv();
		body.appendChild(div);
		HtmlP htmlP = new HtmlP();
		htmlP.appendChild("Containers: "+getPageAnalyzerContainerList().size());
		div.appendChild(htmlP);
		for (AbstractContainer container : getPageAnalyzerContainerList()) {
			div.appendChild(container.createHtmlElement());
		}
		return html;
	}

	private void addStyle(HtmlHtml html) {
		HtmlStyle style = new HtmlStyle();
		style.addCss("div {border : solid 1pt; margin: 2pt; padding : 2pt}");
		html.insertChild(style, 0);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Page: "+pageNumber+"\n");
		sb.append("Containers: "+getPageAnalyzerContainerList().size()+"\n");
		for (AbstractContainer container : getPageAnalyzerContainerList()) {
			sb.append(container.toString()+"\n");
		}
		return sb.toString();
	}

	public List<AbstractContainer> getPageAnalyzerContainerList() {
		return pageAnalyzerContainerList;
	}

	public void setPageAnalyzerContainerList(
			List<AbstractContainer> pageAnalyzerContainerList) {
		this.pageAnalyzerContainerList = pageAnalyzerContainerList;
	}

	/** removes empty/unnecessary spans etc.
	 *  
	 * @param div
	 */
	public static void cleanHtml(HtmlElement div) {
		removeWhitespaceSpan(div);
		removeSpanSubSupBI(div);
		replaceSpansByTextChildren(div);
	}

	private static void replaceSpansByTextChildren(HtmlElement div) {
//		String xpath = "//*[local-name()='span' and count(text()) = 1  and count(*) = 0]";
//		replaceNodesWithChildren(div, xpath);
		String xpath = "//*[local-name()='span' and count(text()) = 1  and count(*) = 0]/text()";
		replaceParentsWithNodes(div, xpath);
	}

	private static void replaceNodesWithChildren(HtmlElement div, String xpath) {
		Nodes nodes = div.query(xpath);
		for (int i = 0; i < nodes.size(); i++) {
			Node spanNode = nodes.get(i);
			ParentNode parent = spanNode.getParent();
			int index = parent.indexOf(spanNode);
			int nchild = spanNode.getChildCount();
			for (int j = nchild-1; j >=0; j--) {
				Node child = spanNode.getChild(j);
				child.detach();
				parent.insertChild(child, index);
			}
			parent.detach();
		}
	}

	private static void removeWhitespaceSpan(HtmlElement div) {
		removeNodes(div, "//*[local-name()='span' and normalize-space(.)='']");
	}

	private static void removeNodes(HtmlElement div, String xpath) {
		Nodes nodes = div.query(xpath);
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}

	private static void removeSpanSubSupBI(HtmlElement div) {
		replaceParentsWithNodes(div, "//*[local-name()='span']/*[local-name()='sub' or local-name()='sup' or local-name()='b' or local-name()='i']");
	}

	/** not tested
	 * 
	 * @param div
	 * @param xpath
	 */
	private static void replaceParentsWithNodes(HtmlElement div, String xpath) {
		Nodes nodes = div.query(xpath);
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			replaceParentWith(node);
		}
	}

	private static void replaceParentWith(Node node) {
		Element parent = (Element) node.getParent();
		Element grandParent = (Element) parent.getParent();
		node.detach();
		grandParent.replaceChild(parent, node);
	}
}
