package org.xmlcml.svgplus.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.pdf2svg.util.PDF2SVGUtil;
import org.xmlcml.svgplus.command.PageEditor;
import org.xmlcml.svgplus.tools.BoundingBoxManager.BoxEdge;

public class Chunk extends SVGG {
	
	private static final String ROLE = "role";
	private static final String CHUNK = "chunk";
	private final static Logger LOG = Logger.getLogger(Chunk.class);
	
	protected PageEditor currentPage;
	public Chunk() {
		PDF2SVGUtil.setSVGXAttribute(this, ROLE, CHUNK);
	}

	public Chunk(PageEditor currentPage) {
		this();
		this.currentPage = currentPage;
	}

	public void writeTo(File outputDir, String type, int i) throws IOException  {
		throw new RuntimeException("NYI");
	}

	public void createElementListAndCalculateBoundingBoxes() {
		throw new RuntimeException("NYI");
	}

	public void copyAttributesAndChildrenFromSVGElement(SVGElement captionElement) {
		throw new RuntimeException("NYI");
	}

	public List<SVGElement> getElementList() {
		LOG.error("NYI");
		return null;
	}

	public String getStringValue() {
		throw new RuntimeException("NYI");
	}

	public static Chunk createAndReplace(SVGElement chunkElement) {
		throw new RuntimeException("NYI");
	}

	public void setBoundingBoxCacheForSelfAndDescendants(boolean b) {
		throw new RuntimeException("NYI");
	}

	public boolean isScript() {
		throw new RuntimeException("NYI");
	}

	public boolean isTextChunk() {
		throw new RuntimeException("NYI");
	}

	public List<Chunk> splitIntoChunks(Double ySEP_0, BoxEdge ymin) {
		throw new RuntimeException("NYI");
	}

}
