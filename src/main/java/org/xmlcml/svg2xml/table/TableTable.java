package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlCaption;
import org.xmlcml.html.HtmlHead;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.html.HtmlTr;

/** holds temporary table as list of chunks.
 * might disappear into TableAnalyzer later?
 * @author pm286
 *
 */
public class TableTable extends AbstractTableChunk {

	private final static Logger LOG = Logger.getLogger(TableTable.class);
	
	private List<GenericTableChunk> chunkList;
	private int maxHorizontalChunks;
	private List<GenericTableChunk> noColumnChunkList;
	private List<GenericTableChunk> maxColumnChunkList;
	private List<GenericTableChunk> otherChunkList;
	private GenericTableChunk headerChunk;
	private AbstractTableChunk bodyChunk;
	private AbstractTableChunk captionChunk;
	private AbstractTableChunk footerChunk;
	private HtmlTable htmlTable;

	public TableTable() {
		init();
	}
	
	private void init() {
		chunkList = new ArrayList<GenericTableChunk>();
	}
	
	public void add(GenericTableChunk tableChunk) {
		chunkList.add(tableChunk);
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
			for (AbstractTableChunk chunk : otherChunkList) { 
				System.out.println(">> "+chunk);
			}
		}
		LOG.debug("maxCol "+maxColumnChunkList.size());
		LOG.debug("noCol "+noColumnChunkList.size());
		LOG.debug("otherCol "+otherChunkList.size());
		LOG.debug("caption "+captionChunk);
		LOG.debug("header "+headerChunk);
		LOG.debug("body "+bodyChunk);
		LOG.debug("footer "+footerChunk);
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

	private HtmlP makePara(AbstractTableChunk chunk) {
		HtmlP p = new HtmlP();
		for (Element element : chunk.getElementList()) {
			p.appendChild(element.getValue());
		}
		return p;
	}

	private void createChunkLists() {
		noColumnChunkList = new ArrayList<GenericTableChunk>();
		maxColumnChunkList = new ArrayList<GenericTableChunk>();
		otherChunkList = new ArrayList<GenericTableChunk>();
		for (GenericTableChunk tableChunk : chunkList) {
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
		for (GenericTableChunk tableChunk : chunkList) {
			int horizontalChunkCount = tableChunk.getHorizontalGaps().size();
			maxHorizontalChunks = Math.max(horizontalChunkCount, maxHorizontalChunks);
		}
		return maxHorizontalChunks;
	}
}
