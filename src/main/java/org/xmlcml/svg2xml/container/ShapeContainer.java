package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.svg2xml.figure.FigureGraphic;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.page.ShapeAnalyzer;
import org.xmlcml.svg2xml.pdf.ChunkId;
import org.xmlcml.svg2xml.pdf.PDFIndex;
import org.xmlcml.svg2xml.util.SVG2XMLUtil;

public class ShapeContainer extends AbstractContainer  {

	public final static Logger LOG = Logger.getLogger(ShapeContainer.class);

	private ShapeAnalyzer shapeAnalyzer;
	private List<SVGShape> shapeList;

	public ShapeContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}
	
	public ShapeContainer(ShapeAnalyzer shapeAnalyzer) {
		super(shapeAnalyzer);
		this.shapeAnalyzer = shapeAnalyzer;
		this.shapeAnalyzer.setShapeContainer(this);
	}

	@Override
	public HtmlElement createHtmlElement() {
		htmlElement = null;
		if (svgChunk != null) {
			ChunkId chunkId = getChunkId();
			String id = chunkId == null ? String.valueOf(System.currentTimeMillis()) : chunkId.toString();
			super.createHtmlElement();
			String imageName = pageAnalyzer.getPageIO().createImageFilename(id);
			String svgName = pageAnalyzer.getPageIO().createSvgFilename(id);
			HtmlDiv div = FigureGraphic.createHtmlImgDivElement(imageName, "20%");
			htmlElement.appendChild(div);
			FigureGraphic figureGraphic = new FigureGraphic(pageAnalyzer);
			removeAnnotatedRects(svgChunk);
			figureGraphic.setSVGContainer(svgChunk);
			figureGraphic.createAndWriteImageAndSVG(imageName, div, svgName);
		} else {
			LOG.trace("Null Shape Chunk: "/*+shapeList+" "+((shapeList != null) ? shapeList.size() : "null"*/);
		}
		return htmlElement;
	}

	// this is a mess
	private void removeAnnotatedRects(SVGG svgChunk) {
		Nodes nodes = svgChunk.query("//*[@title='org.xmlcml.svg2xml.page.ShapeAnalyzer1']");
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}

	public List<SVGShape> getShapeList() {
		ensureShapeList();
		return shapeList;
	}

	private void ensureShapeList() {
		if (shapeList == null) {
			this.shapeList = new ArrayList<SVGShape>();
		}
	}
	
	@Override
	public SVGG createSVGGChunk() {
		SVGG g = new SVGG();
		for (SVGShape shape : shapeList) {
			g.appendChild(shape.copy());
		}
		return g;
	}
	

	@Override
	public String summaryString() {
		StringBuilder sb = new StringBuilder(">>>ShapeContainer>>>"+" shapes: "+shapeList.size()+"\n");
		for (SVGShape shape : shapeList) {
			sb.append(SVG2XMLUtil.trimText(20, shape.getSignature())+"\n");
		}
		sb.append("<<<ShapeContainer<<<");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString()+"\n");
		sb.append(outputSVGList("Shapes", shapeList));
		return sb.toString();
	}

	public void addToIndexes(PDFIndex pdfIndex) {
		String shapeString = this.toString();
		pdfIndex.addToShapeIndex(shapeString, this);
	}

	public void setShapeList(List<SVGShape> shapeList) {
		this.shapeList = shapeList;
	}

}
