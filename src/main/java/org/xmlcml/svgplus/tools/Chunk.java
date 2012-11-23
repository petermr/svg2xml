package org.xmlcml.svgplus.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.tools.BoundingBoxManager.BoxEdge;

public class Chunk extends SVGElement {
	
	private final static Logger LOG = Logger.getLogger(Chunk.class);
	
	public Chunk() {
		super("chunk");
	}

//	public Chunk(PageChunkSplitter pageChunkSplitter, SVGSVG svgPage) {
//		this();
//		// TODO Auto-generated constructor stub
//	}


	public Chunk(PageChunkSplitter pageChunkSplitter, SVGSVG svgPage) {
		super("grot");
		// TODO Auto-generated constructor stub
	}

	public void writeTo(File outputDir, String type, int i) throws IOException  {
		// TODO Auto-generated method stub
		
	}

	public void createElementListAndCalculateBoundingBoxes() {
		// TODO Auto-generated method stub
		
	}

	public void copyAttributesAndChildrenFromSVGElement(SVGElement captionElement) {
		// TODO Auto-generated method stub
		
	}

	public List<SVGElement> getElementList() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getStringValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public static Chunk createAndReplace(SVGElement chunkElement) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBoundingBoxCacheForSelfAndDescendants(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public boolean isScript() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTextChunk() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Chunk> splitIntoChunks(Double ySEP_0, BoxEdge ymin) {
		// TODO Auto-generated method stub
		return null;
	}

}
