package org.xmlcml.svg2xml.table;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRange.Direction;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlB;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlCaption;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlHead;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.svg2xml.analyzer.PathAnalyzerX;

/** holds temporary table as list of chunks.
 * might disappear into TableAnalyzer later?
 * @author pm286
 *
 */
public class TableTable extends GenericChunk {

	private final static Logger LOG = Logger.getLogger(TableTable.class);

	public static final double HALF_SPACE = 2.0;
	
	private List<GenericChunk> chunkList;
	private int maxHorizontalChunks;
	private List<GenericChunk> noColumnChunkList;
	private List<GenericChunk> maxColumnChunkList;
	private List<GenericChunk> otherChunkList;
	private GenericChunk headerChunk;
	private GenericChunk bodyChunk;
	private GenericChunk captionChunk;
	private GenericChunk footerChunk;
	private HtmlTable htmlTable;

	private List<SVGPath> pathList;
	private List<SVGText> textList;
	private Real2Range pathBox;
	private Real2Range textBox;
	private Real2Range totalBox;

	private List<GenericChunk> genericTextChunkList;

	public TableTable() {
		init();
	}
	
	public TableTable(List<SVGPath> pathList, List<SVGText> textList) {
		this();
		this.pathList = pathList;
		this.textList = textList;
	}

	private void init() {
		chunkList = new ArrayList<GenericChunk>();
	}
	
	public void add(GenericChunk tableChunk) {
		chunkList.add(tableChunk);
	}

	/** simple at present (does not do paths in table cells
	 * 
	 */
	public RealRangeArray createCoarseVerticalMask() {
		textBox = SVGUtil.createBoundingBox(textList);
		totalBox = textBox;
		if (pathList != null && pathList.size() > 0) {
			verticalMask = createVerticalMaskFromPaths();
			verticalMask.addTerminatingCaps(totalBox.getYRange().getMin(), totalBox.getYRange().getMax());
		} else {
			verticalMask = new RealRangeArray(textBox, RealRange.Direction.VERTICAL);
		}
		verticalMask = verticalMask.inverse();
		LOG.trace("YMask: "+verticalMask);
		return verticalMask;
		
	}
		
	private RealRangeArray createVerticalMaskFromPaths() {
	
		this.pathList = PathAnalyzerX.removeDuplicatePaths(pathList);
		this.pathBox = SVGUtil.createBoundingBox(pathList);
		totalBox = totalBox.plus(pathBox);
		// because some "lines" (e.g. in BMC) are multiple paths. This is a mess and needs more 
		// heuristics
		List<Real2Range> pathBboxList = SVGUtil.createNonOverlappingBoundingBoxList(pathList);
		verticalMask = new RealRangeArray(pathBboxList, RealRange.Direction.VERTICAL);
		return verticalMask;
	}

	public void analyze() {
		getMaxHorizontalChunks();
		createChunkLists();
		if (maxColumnChunkList.size() == 2) {
			headerChunk = maxColumnChunkList.get(0);
			bodyChunk = maxColumnChunkList.get(1);
		}
		if (noColumnChunkList.size() > 0) {
			captionChunk = noColumnChunkList.get(0);
			captionChunk.debug();
		}
		if (noColumnChunkList.size() > 1) {
			footerChunk = maxColumnChunkList.get(1);
		}
		if (otherChunkList.size() > 0) {
			System.out.println("OTHER");
			for (GenericChunk chunk : otherChunkList) { 
				LOG.trace(">> "+chunk);
			}
		}
		LOG.trace("maxCol "+maxColumnChunkList.size());
		LOG.trace("noCol "+noColumnChunkList.size());
		LOG.trace("otherCol "+otherChunkList.size());
		LOG.trace("caption "+captionChunk);
		LOG.trace("header "+headerChunk);
		LOG.trace("body "+bodyChunk);
		LOG.trace("footer "+footerChunk);
	}
	
	public HtmlTable createTable() {
		htmlTable = new HtmlTable();
		createAndAddCaption();
		createAndAddHeader();
		createAndAddRows();
		createAndAddFooter();
		return htmlTable;
	}

	private void createAndAddFooter() {
		if (footerChunk != null) {
//			HtmlFooter htmlFooter = new HtmlFooter();
//			HtmlP para = makePara(footerChunk);
//			htmlFooter.appendChild(para);
//			htmlTable.appendChild(htmlFooter);
		}
	}

	private void createAndAddHeader() {
		if (headerChunk != null) {
			HtmlHead htmlHead = new HtmlHead();
			htmlTable.appendChild(htmlHead);
			HtmlTh th = new HtmlTh();
			htmlHead.appendChild(th);
			for (int i = 0; i < maxHorizontalChunks; i++) {
				String text = headerChunk.getHorizontalChunk(i);
				th.appendChild(text);
			}
		}
	}

	private void createAndAddRows() {
		if (bodyChunk != null) {
			HtmlBody htmlBody = new HtmlBody();
			htmlTable.appendChild(htmlBody);
			
//			for (//)
//			HtmlTr tr = new HtmlTr();
//			htmlBody.appendChild(tr);
//			for (int i = 0; i < maxHorizontalChunks; i++) {
//				String text = headerChunk.getHorizontalChunk(i);
//				tr.appendChild(text);
//			}
		}
	}

	private void createAndAddCaption() {
		if (captionChunk != null) {
			HtmlCaption htmlCaption = new HtmlCaption();
			HtmlP para = makePara(captionChunk);
			htmlCaption.appendChild(para);
			htmlTable.appendChild(htmlCaption);
		}
	}

	private HtmlP makePara(GenericChunk chunk) {
		HtmlP p = new HtmlP();
		for (Element element : chunk.getElementList()) {
			p.appendChild(element.getValue());
		}
		return p;
	}

	private void createChunkLists() {
		noColumnChunkList = new ArrayList<GenericChunk>();
		maxColumnChunkList = new ArrayList<GenericChunk>();
		otherChunkList = new ArrayList<GenericChunk>();
		for (GenericChunk tableChunk : chunkList) {
			int horizontalChunkCount = tableChunk.getHorizontalGaps().size();
			if (horizontalChunkCount == 1) {
				noColumnChunkList.add(tableChunk);
			} else if (horizontalChunkCount == maxHorizontalChunks) {
				maxColumnChunkList.add(tableChunk);
			} else {
				otherChunkList.add(tableChunk);
			}
		}
	}

	public int getMaxHorizontalChunks() {
		maxHorizontalChunks = 0;
		for (GenericChunk tableChunk : chunkList) {
			int horizontalChunkCount = tableChunk.getHorizontalGaps().size();
			maxHorizontalChunks = Math.max(horizontalChunkCount, maxHorizontalChunks);
		}
		return maxHorizontalChunks;
	}

	public List<SVGPath> getPathList() {
		return pathList;
	}

	public List<GenericChunk> createVerticalTextChunks() {
		this.createCoarseVerticalMask();
		genericTextChunkList = new ArrayList<GenericChunk>();
		if (verticalMask != null) {
			for (RealRange realRange : verticalMask) {
				GenericChunk AbstractTableChunk = new GenericChunk();
				genericTextChunkList.add(AbstractTableChunk);
				for (SVGText text : textList) {
					RealRange textRange = text.getRealRange(Direction.VERTICAL);
					if (realRange.includes(textRange)) {
						AbstractTableChunk.add(text);
					}
				}
			}
		}
		return genericTextChunkList;
	}

	public List<GenericChunk> getGenericTextChunkList() {
		return genericTextChunkList;
	}

	public List<GenericChunk> analyzeVerticalTextChunks() {
		createVerticalTextChunks();
		int index = 0;
		for (GenericChunk abstractTableChunk : genericTextChunkList) {
//			AbstractTableChunk.createHorizontalMask();
			abstractTableChunk.createHorizontalMaskWithTolerance(HALF_SPACE);
			int cols = abstractTableChunk.getHorizontalMask().size();
			GenericChunk abstractChunk = null;
			if (cols == 1) {
				abstractChunk = new TableCaption(abstractTableChunk);
			} else {
				TableBody tableBody = new TableBody(abstractTableChunk);
				tableBody.createStructuredRows();
				abstractChunk = tableBody;
			}
			LOG.trace(">> "+abstractTableChunk.getHorizontalMask());
			// replace with new class
			genericTextChunkList.set(index, abstractChunk);
			index++;
		}
		return genericTextChunkList;
	}
	
	public HtmlElement getHtml() {
		HtmlTable table = new HtmlTable();
		table.setBorder(1);
		HtmlHead head = new HtmlHead();
		table.appendChild(head);
		HtmlBody body = new HtmlBody();
		table.appendChild(body);
		for (GenericChunk chunk : genericTextChunkList) {
			HtmlElement htmlElement = chunk.getHtml();
			if (htmlElement instanceof HtmlTable) {
				if (htmlElement.query("*[local-name()='tr']").size() == 1) {
					htmlElement = TableRow.convertBodyHeader(htmlElement);
				} else {
					CMLUtil.transferChildren(htmlElement, body);
				}
				body.appendChild(htmlElement);
			} else if (htmlElement instanceof HtmlCaption) {
				TableCaption.addCaptionTo(table, (HtmlCaption)htmlElement);
			} else {
				LOG.trace("HTML: "+htmlElement);
			}
		}
		removeEmptyTables(body);
		int nn = table.query("//*").size();
		try {
			CMLUtil.debug(table, new FileOutputStream("target/table"+nn+".html"), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return table;
	}

	private void removeEmptyTables(HtmlBody body) {
		Nodes tables = body.query(".//*[local-name()='table' and count(*)=0]");
		for (int i = 0; i < tables.size(); i++) {
			tables.get(i).detach();
		}
	}

}
