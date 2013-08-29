package org.xmlcml.svg2xml.old;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.html.HtmlB;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlImg;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.svg2xml.analyzer.AbstractAnalyzer;
import org.xmlcml.svg2xml.analyzer.ChunkId;
import org.xmlcml.svg2xml.analyzer.PDFAnalyzer;
import org.xmlcml.svg2xml.analyzer.PDFIndex;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;
import org.xmlcml.svg2xml.text.TextStructurer;
import org.xmlcml.svg2xml.util.SVGPlusConstantsX;

/** container for  HtmlElement
 * Used to manipulate HTML. 
 * 
 * Two main approaches:
 * (a) add an analyzer and create HTML using it
 * (b) add HTMLElement at creation
 * 
 * @author pm286
 *
 */
public class HtmlAnalyzer extends AbstractAnalyzer {

	static final Logger LOG = Logger.getLogger(HtmlAnalyzer.class);
	private static final String REMOVED_SVG = "REMOVED_SVG";
	static final String OMIT = "omit";
	
	private static final String LAST = "last";
	private static final String NEXT = "next";
	
	private PDFAnalyzer pdfAnalyzer;
	private AbstractAnalyzer analyzer;
	private HtmlElement htmlElement;
	private HtmlEditor htmlEditor;
	private Integer serial;
	
	public HtmlAnalyzer() {
		super();
	}
	
	/** 
	 * 
	 * @param htmlEditor
	 * @param analyzer the analyzer used to create the HTML
	 */
	public HtmlAnalyzer(HtmlEditor htmlEditor, AbstractAnalyzer analyzer) {
		this(htmlEditor);
		this.analyzer = analyzer;
	}

	public HtmlAnalyzer(HtmlEditor htmlEditor) {
		this.htmlEditor = htmlEditor;
	}

	public HtmlAnalyzer(HtmlElement htmlElement, HtmlEditor htmlEditor) {
		this(htmlEditor);
		this.setHtmlElement(htmlElement);
	}

	void setHtmlElement(HtmlElement htmlElem) {
		this.htmlElement = htmlElem;
	}

	/** creates HTML using the analyzer
	 * 
	 */
	public HtmlElement createHtmlElement() {
		this.htmlElement = getAnalyzer().createHtmlElement();
		return htmlElement;
	}
	
	public HtmlElement getHtmlElement() {
		return htmlElement;
	}

	void removeSVGNodes() {
		Nodes nodes = htmlElement.query(".//*[local-name()='svg']");
		for (int i = 0; i < nodes.size(); i++) {
			Element svg = (Element) nodes.get(0);
			Element svgParent = (Element) svg.getParent();
			HtmlP p = new HtmlP();
			p.appendChild(REMOVED_SVG);
			svgParent.replaceChild(svg, p);
		}
	}

	void addIdSeparator(HtmlElement parentHtmlElement) {
		HtmlSpan span = new HtmlSpan();
		HtmlB b = new HtmlB();
		b.appendChild(" ["+this.getId()+"] ");
		span.appendChild(b);
		parentHtmlElement.appendChild(span);
		LOG.trace(">> "+this.getId());
	}

	@Override
	public SVGG oldAnnotateChunk() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean containsDivImage() {
		boolean contains = false;
		if (htmlElement != null) {
			Nodes nodes = htmlElement.query(".//*[local-name()='img']");
			contains = nodes.size() > 0;
		}
		return contains;
	}

	public String getClassAttribute() {
		return htmlElement == null ? null : htmlElement.getClassAttribute();
	}

	public String getId() {
		String id = null;
		if (htmlElement != null) {
			id = htmlElement.getId();
			if (id == null) {
				LOG.debug("null id");
			}
		}
		return id;
	}

	public void setClassAttribute(String value) {
		if (htmlElement != null) htmlElement.setClassAttribute(value);
	}


	public void setId(String id) {
		if (htmlElement != null) htmlElement.setId(id);
	}

	public String toString() {
		return htmlElement.toString();
	}

	public String getAttributeValue(String attName) {
		return htmlElement == null ? null : htmlElement.getAttributeValue(attName);
	}

	public Elements getChildElements() {
		return htmlElement == null ? null : htmlElement.getChildElements();
	}

	private void setLink(String direction, ChunkId chunkId) {
		if (htmlElement != null) htmlElement.addAttribute(new Attribute(direction, chunkId.toString()));
	}

	void addLinks(HtmlAnalyzer lastAnalyzer) {
		ChunkId lastChunkId = lastAnalyzer == null ? null : new ChunkId(lastAnalyzer.getId());
		if (lastChunkId != null) {
			setLink( LAST, lastChunkId);
		}
		ChunkId chunkId = new ChunkId(htmlElement.getId());
		if (lastAnalyzer != null) {
			lastAnalyzer.setLink(NEXT, chunkId);
		}
	}


	void addImageDivTo(HtmlAnalyzer previousAnalyzer) {
		if (previousAnalyzer != null && previousAnalyzer.containsDivImage()) {
			HtmlImg img = (HtmlImg) previousAnalyzer.getChildElements().get(0);
			LOG.trace("Merged image: "+previousAnalyzer.getId()+" -> "+this.getId());
			previousAnalyzer.setClassAttribute(OMIT);
//			img.detach();
			this.insertChild((HtmlElement)img.copy(), 0);
		}
	}

	private void insertChild(HtmlElement child, int position) {
		if (htmlElement != null) htmlElement.insertChild(child, position);
	}

	String  addTypeSerialAttributes() {
		String classAttribute = this.getClassAttribute(); 
		classAttribute = (classAttribute == null) ? null : classAttribute.trim(); 
		String chunkType = null;
		Integer serial = null;
		if (classAttribute != null) {
			if (pdfAnalyzer != null) {
				for (AbstractAnalyzer analyzer : pdfAnalyzer.getIndex().getAnalyzerList()) {
					if (analyzer.isChunk(classAttribute)) {
						chunkType = analyzer.getTitle();
						serial = new Integer(classAttribute.substring(chunkType.length()).trim());
						htmlElement.addAttribute(new Attribute(PDFIndex.CHUNK_TYPE, chunkType));
						htmlElement.addAttribute(new Attribute("serial", ""+serial));
						break;
					}
				}
			}
		}
		return chunkType;
	}

	HtmlAnalyzer getPreviousHtmlAnalyzer(Map<ChunkId, HtmlAnalyzer>htmlAnalyzerByIdMap) {
		String previous = this.getAttributeValue(LAST);
		HtmlAnalyzer previousAnalyzer = (previous == null) ? null : htmlAnalyzerByIdMap.get(new ChunkId(previous));
		return previousAnalyzer;
	}
	
	void outputElementAsHtml(File outputDir) {
		if (outputDir != null) {
			String id = this.getId();
			if (id == null) {
				throw new RuntimeException("No id");
			}
			ChunkId chunkId = new ChunkId(id);
			try {
				outputDir.mkdirs();
				String chunkFileRoot = null;
				String chunkType = this.getChunkType();
				if (chunkType == null) {
					chunkType = PDFAnalyzer.Z_CHUNK;
					chunkFileRoot = chunkType+chunkId.getPageNumber()+"-"+chunkId.getChunkNumber();
				} else {
					chunkFileRoot = chunkType+"-"+serial;					
				}
				File outfile = new File(outputDir, chunkFileRoot+SVGPlusConstantsX.DOT_HTML);
				LOG.debug("writing "+outfile);
				OutputStream os = new FileOutputStream(outfile);
				CMLUtil.debug(htmlElement, os, 1);
				os.close();
			} catch (Exception e) {
				throw new RuntimeException("cannot write HTML: ",e);
			}
		}
	}


	public String getValue() {
		return (htmlElement) == null ? null : htmlElement.getValue();
	}

	public String toXML() {
		return (htmlElement) == null ? null : htmlElement.toXML();
	}

	public String getChunkType() {
		return getAttributeValue(PDFIndex.CHUNK_TYPE);
	}

	public void setChunkType(String type) {
		addAttribute(new Attribute(PDFIndex.CHUNK_TYPE, type));
	}
	
	public void addAttribute(Attribute attribute) {
		htmlElement.addAttribute(attribute);
	}

	void addClassAttributeIfMissing(String title, Integer serial) {
		String classX = getClassAttribute();
		if (classX == null) {
			setClassAttribute(title+" "+serial);
		}
		this.serial = serial;
	}

	public AbstractAnalyzer getAnalyzer() {
		return analyzer;
	}

	public void setSerial(int serial) {
		this.serial =  serial;
	}

	public TextStructurer getTextStructurer() {
		TextStructurer textStructurer = null;
		if (analyzer != null && analyzer instanceof TextAnalyzerX) {
			textStructurer = ((TextAnalyzerX) analyzer).getTextStructurer();
		}
		return textStructurer;
	}

	public boolean mergeLinesWithPrevious(HtmlAnalyzer lastAnalyzer, HtmlElement topDiv) {
		HtmlP lastTopP = getLastPara(topDiv);
		boolean merged = false;
		HtmlElement lastElement = lastAnalyzer.getHtmlElement();
		LOG.trace("LAST "+lastElement.toXML());
		HtmlP lastP = getLastPara(lastElement);

		//this is a mess
//		String lastS = lastP.getValue();
//		int l = lastS.length();
//		lastS = lastS.substring(Math.max(0, l-20));
		LOG.trace("THIS "+htmlElement.toXML());
		HtmlP thisP = getFirstPara(htmlElement);
		int idx = (thisP == null) ? -1 : htmlElement.indexOf(thisP);
//		LOG.trace("THIS PARA "+thisP.toXML());
		
//		String thisS = thisP.getValue();
//		thisS = thisS.substring(0, Math.min(20, thisS.length()));
//		LOG.trace("merging ["+lastAnalyzer.getId()+"... "+lastS+" ... "+thisS+" ..."+this.getId()+"]");
		if (lastTopP!= null && thisP != null) {
			addIdSeparator(lastTopP);
			copyToFrom(lastTopP, thisP, 0);
			copyToFrom(topDiv, htmlElement, idx+1);
			merged = true;
		}
		return merged;
	}

	private void copyToFrom(HtmlElement to, HtmlElement from, int start) {
		for (int i = start; i < from.getChildCount(); i ++) {
			Node child = from.getChild(i);
			if (child instanceof Text) {
				to.appendChild(new Text((Text)child));
			} else {
				to.appendChild(HtmlElement.create((Element)child));
			}
		}
	}

	private HtmlP getLastPara(HtmlElement element) {
		List<HtmlP> paraList = getParagraphs(element);
		return paraList.size() == 0? null : paraList.get(paraList.size() - 1);
	}

	private HtmlP getFirstPara(HtmlElement element) {
		List<HtmlP> paraList = getParagraphs(element);
		return paraList.size() == 0? null : paraList.get(0);
	}

	private List<HtmlP> getParagraphs(HtmlElement element) {
		Nodes nodes = element.query(".//*[local-name()='p']");
		List<HtmlP> paraList = new ArrayList<HtmlP>();
		for (int i = 0; i < nodes.size(); i++) {
			Element elem = (Element) nodes.get(i);
			HtmlP newPara = (elem instanceof HtmlP) ? (HtmlP) elem : (HtmlP) HtmlElement.create(elem);
			paraList.add(newPara);
		}
		return paraList;
	}

}
