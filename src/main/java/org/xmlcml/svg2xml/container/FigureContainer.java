package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.html.HtmlElement;
import org.xmlcml.graphics.html.HtmlP;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.page.PageAnalyzer;

public class FigureContainer extends AbstractContainerOLD  {

	public final static Logger LOG = Logger.getLogger(FigureContainer.class);

	private List<ScriptContainerOLD> captionList;
	private List<ImageContainer> imageContainerList;
	private List<ShapeContainer> pathContainerList;
	
	public FigureContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
		ensureContainerLists();
	}

	private void ensureContainerLists() {
		if (captionList == null) {
			captionList = new ArrayList<ScriptContainerOLD>();
		}
		if (imageContainerList == null) {
			imageContainerList = new ArrayList<ImageContainer>();
		}
		if (pathContainerList == null) {
			pathContainerList = new ArrayList<ShapeContainer>();
		}
	}

	@Override
	public HtmlElement createHtmlElement() {
		this.createFigureHtmlElement();
		HtmlP p = new HtmlP("FIGURE");
		htmlElement.appendChild(p);
		return htmlElement;
	}

	public SVGG createSVGGChunk() {
		SVGG g = new SVGG();
		for (AbstractContainerOLD container : captionList) {
			g.appendChild(container.createSVGGChunk());
		}
		for (AbstractContainerOLD container : imageContainerList) {
			g.appendChild(container.createSVGGChunk());
		}
		for (AbstractContainerOLD container : pathContainerList) {
			g.appendChild(container.createSVGGChunk());
		}
		return g;
	}

	@Override
	public String summaryString() {
		StringBuilder sb = new StringBuilder(">>>FIG>>>"+this.getClass().getSimpleName()+"\n");
		sb.append(super.toString()+"\n");
		sb.append(outputList("Captions", captionList));
		sb.append(outputList("Images", imageContainerList));
		sb.append(outputList("Paths", pathContainerList));
		sb.append("<<<FIG<<<");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()+"\n");
		sb.append(super.toString()+"\n");
		sb.append(outputList("Captions", captionList));
		sb.append(outputList("Images", imageContainerList));
		sb.append(outputList("Paths", pathContainerList));
		return sb.toString();
	}
	

}
