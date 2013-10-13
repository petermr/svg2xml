package org.xmlcml.svg2xml.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;
import org.xmlcml.svg2xml.page.PageAnalyzer;

public class FigureContainer extends AbstractContainer  {

	public final static Logger LOG = Logger.getLogger(FigureContainer.class);

	private List<ScriptContainer> captionList;
	private List<ImageContainer> imageContainerList;
	private List<ShapeContainer> pathContainerList;
	
	public FigureContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
		ensureContainerLists();
	}

	private void ensureContainerLists() {
		if (captionList == null) {
			captionList = new ArrayList<ScriptContainer>();
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
		for (AbstractContainer container : captionList) {
			g.appendChild(container.createSVGGChunk());
		}
		for (AbstractContainer container : imageContainerList) {
			g.appendChild(container.createSVGGChunk());
		}
		for (AbstractContainer container : pathContainerList) {
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
