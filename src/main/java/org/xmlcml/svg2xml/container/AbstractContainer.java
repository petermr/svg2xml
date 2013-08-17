package org.xmlcml.svg2xml.container;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.analyzer.ChunkId;
import org.xmlcml.svg2xml.analyzer.PageAnalyzer;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

/** containers contain the outputs of PageAnalyzer
 * they roughly mirror the HTML hierarchy and all implement
 * createHTML methods. 
 * 
 * @author pm286
 *
 */
public abstract class AbstractContainer {

	public final static Logger LOG = Logger.getLogger(AbstractContainer.class);

	private static final String CHUNK = "chunk";

	protected List<AbstractContainer> containerList;
	protected PageAnalyzer pageAnalyzer;
	protected ChunkId chunkId;

	private SVGG svgChunk;

	public AbstractContainer(PageAnalyzer pageAnalyzer) {
		this.pageAnalyzer = pageAnalyzer;
		ensureContainerList();
	}

	public HtmlElement createHtmlElement() {
		HtmlElement div = new HtmlDiv();
		for (AbstractContainer container : containerList) {
			HtmlElement htmlElement = container.createHtmlElement();
			div.appendChild(htmlElement);
		}
		return div;
	}
	public abstract SVGG createSVGGChunk();
	
	private void ensureContainerList() {
		if (containerList == null) {
			this.containerList = new ArrayList<AbstractContainer>();
		}
	}
	
	public void add(AbstractContainer abstractContainer) {
		this.ensureContainerList();
		containerList.add(abstractContainer);
	}
	
	public List<AbstractContainer> getContainerList() {
		return this.containerList;
	}

	public String summaryString() {
		String clazz = this.getClass().getSimpleName();
		StringBuilder sb = new StringBuilder(">>>"+clazz+">>> \n");
		for (AbstractContainer container : containerList) {
			sb.append("\n   "+container.summaryString()+"\n");
		}
		sb.append("<<<"+clazz+"<<<\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()+"\n");
		sb.append("Containers: "+containerList.size()+"\n");
		for (AbstractContainer container : containerList) {
			LOG.debug("CONT: "+container.getClass());
			sb.append(container.toString()+"\n");
		}
		return sb.toString();
	}

	protected String outputList(String title, List<? extends AbstractContainer> containerList) {
		StringBuilder sb = new StringBuilder(title+" "+containerList.size()+"\n");
		for (AbstractContainer container : containerList) {
			sb.append("... "+SVG2XMLUtil.trim(container.toString(), 100)+"\n");
		}
		return sb.toString();
	}
	
	protected String outputSVGList(String title, List<? extends SVGElement> svgList) {
		StringBuilder sb = new StringBuilder();
		if (svgList.size() < 5){
			for (SVGElement element : svgList) {
				String s = element.toXML();
				int l = s.length();
				sb.append(s.subSequence(0, Math.min(80, l))+((l > 80) ? "..." : "")+"\n");
			}
		} else {
			sb.append(title+" "+svgList.size()+"\n");
		}
		return sb.toString();
	}

	public String getSuffix() {
		return this.getClass().getSimpleName().substring(0, 1);
	}


	/** character value of Container
	 * mainly for string-based containers
	 * @return
	 */
	public String getRawValue() {
		return "StringValue: "+this.getClass().getName();
	}

	public void setChunkId(ChunkId chunkId) {
		this.chunkId = chunkId;
	}

	public ChunkId getChunkId() {
		return chunkId;
	}

	public void setSVGChunk(SVGG svgChunk) {
		this.svgChunk = svgChunk;
	}
	
	public SVGG getSVGChunk() {
		return svgChunk;
	}

	public void writeFinalSVGChunk(File outputDocumentDir, Character cc, int humanPageNumber,
			int aggregatedContainerCount) {
		String filename = CHUNK+humanPageNumber+"."+  
			    (aggregatedContainerCount)+this.getSuffix()+String.valueOf(cc);
		SVG2XMLUtil.writeToSVGFile(outputDocumentDir, filename, svgChunk, false);
	}
}
