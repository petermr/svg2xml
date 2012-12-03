package org.xmlcml.svgplus.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.pdf2svg.util.PDF2SVGUtil;
import org.xmlcml.svgplus.command.PageEditor;
import org.xmlcml.svgplus.tools.BoundingBoxManager.BoxEdge;

/**
 * Chunk tags an SVG container (svg:g or svg:svg) as a chunk. 
 * chunks can be created by construction from an existing container where
 * the attributes and children are transferred to the chunk
 * it adds an attribute svgx:role="chunk" and a number of chuck-specific methods
 * @author pm286
 *
 */
public class Chunk extends SVGG {
	
	public static final String ROLE = "role";
	public static final String CHUNK = "chunk";
	private final static Logger LOG = Logger.getLogger(Chunk.class);
	
	private List<SVGElement> descendantSVGElementList;
	protected BoundingBoxManager boundingBoxManager;
	private List<Real2Range> emptyBoxList;
	private Set<Class<?>> descendantSVGClassSet;
	
	protected Chunk() {
		PDF2SVGUtil.setSVGXAttribute(this, ROLE, CHUNK);
	}

	public Chunk(SVGElement svgElement) {
		this();
		if (svgElement == null) {
			throw new RuntimeException("Cannot create chunk from null element");
		}
		if (!(svgElement instanceof SVGG || svgElement instanceof SVGSVG)) {
			throw new RuntimeException("svgChunk must be svg:g or svg:svg");
		}
		copyAttributesAndChildrenFromSVGElement(svgElement);
	}

	void writeTo(File outputDir, String type, int i) throws IOException  {
		throw new RuntimeException("NYI");
	}

	public void createElementListAndCalculateBoundingBoxes() {
		createElementListAndCalculateBoundingBoxes(this);
		this.boundingBox = null;
	}

	void createElementListAndCalculateBoundingBoxes(SVGElement element) {
		// remove grouping elements and defs
		descendantSVGElementList = SVGUtil.getQuerySVGElements(element, 
				".//svg:*[not(self::svg:svg or self::svg:g or self::*[ancestor-or-self::svg:defs])]");
		calculateBoundingBoxes();
	}
	
	private List<Real2Range> calculateBoundingBoxes() {
		int i = 0;
		ensurePopulatedBoundingBoxManager();
		for (SVGElement element : descendantSVGElementList) {
			LOG.trace("pre "+element.getClass());
			Real2Range boundingBox = element.getBoundingBox();
			LOG.trace("BB "+(i++));
			boundingBoxManager.add(boundingBox);
		}
		return boundingBoxManager.getBBoxList();
	}

	private void ensurePopulatedBoundingBoxManager() {
		if (boundingBoxManager == null) {
			boundingBoxManager = new BoundingBoxManager();
			boundingBoxManager.addBoxesFromElementList(descendantSVGElementList);
		}
	}
	
	public List<SVGElement> getDescendantSVGElementList() {
		if (descendantSVGElementList == null) {
			descendantSVGElementList = SVGUtil.getQuerySVGElements(this, ".//svg:*");
		}
		return descendantSVGElementList;
	}
	
	
	public List<Chunk> splitIntoChunks(Double chunkWidth, BoxEdge edge) {
		getDescendantSVGElementList();
		ensureEmptyBoxList(edge);
		SVGUtil.setBoundingBoxCached(descendantSVGElementList, true);
		Long time0 = System.currentTimeMillis();
		descendantSVGElementList = BoundingBoxManager.getElementsSortedByEdge(descendantSVGElementList, edge);
		LOG.trace("sort edge: "+edge);
		addTerminatingEmptyBox(1.5*chunkWidth, edge);
		List<Chunk> chunkList = new ArrayList<Chunk>();
		if (emptyBoxList == null) {
			return chunkList;
		}
		Chunk newChunk = null;
		LOG.trace("emptyBoxes "+emptyBoxList.size());
		Iterator<Real2Range> boxIterator = emptyBoxList.iterator();
		Iterator<SVGElement> elementIterator = descendantSVGElementList.iterator();
		Real2Range box = boxIterator.next();
		SVGElement element = elementIterator.next();
		int count = 0;
		while (element != null) {
			LOG.trace("count: "+count);
			count++;
			if (box == null || elementLagsBehindBox(edge, box, element)) {
				time0 = System.currentTimeMillis();
				if (newChunk == null) {
					newChunk = makeChunk(chunkWidth, edge, PageEditor.DECIMAL_PLACES, count);
					chunkList.add(newChunk);
					this.appendChild(newChunk);
				}
				newChunk.addSVGElement(element);
				element = elementIterator.hasNext() ? elementIterator.next() : null;
				LOG.trace("addChunk/element: "+(System.currentTimeMillis()-time0));
			} else {
				box = null;
				newChunk = null; // ?
				if (boxIterator.hasNext()) {
					newChunk = null;
					while (boxIterator.hasNext()) {
						box = boxIterator.next();
						if (boxLargeEnough(chunkWidth, edge, box)) {
							break;
						}
					}
				}
			}
		}
		LOG.trace("emptyBoxCount "+emptyBoxList.size());
		for (Real2Range r2r : emptyBoxList) {
			LOG.trace("EmptyBox "+r2r);
			if (r2r.getXRange() == null || r2r.getYRange() == null) {
				throw new RuntimeException("Null empty box");
			}
		}
		LOG.trace("======");
		LOG.trace("iterations: "+count+" loop count time: "+(System.currentTimeMillis()-time0));
		for (Chunk chunk0 : chunkList) {
			chunk0.setBoundingBoxAttribute(PageEditor.DECIMAL_PLACES);
		}
		LOG.trace("reformat chunkList: "+chunkList.size()+"/"+(System.currentTimeMillis()-time0));
		return chunkList;
	}
	
	private Real2Range addTerminatingEmptyBox(double chunkWidth, BoxEdge edge) {
		Real2Range bbox = null;
		double cc;
		if (descendantSVGElementList.size() > 0) {
			SVGElement lastElement = descendantSVGElementList.get(descendantSVGElementList.size()-1);
			Real2Range lastR2R = lastElement.getBoundingBox();
			if (BoxEdge.YMIN.equals(edge)) {
				cc = lastR2R.getYRange().getMax();
				bbox = new Real2Range(lastR2R.getXRange(), new RealRange(cc, cc+chunkWidth));
			} else if (BoxEdge.XMIN.equals(edge)) {
				cc = lastR2R.getXRange().getMax();
				bbox = new Real2Range(new RealRange(cc, cc+chunkWidth), lastR2R.getYRange());
			} else {
				throw new RuntimeException("unsupported edge: "+edge);
			}
			if (!bbox.isValid()) {
				if (lastElement instanceof SVGText) {
					LOG.debug("text w/o bbox "+lastElement.toXML());
					LOG.debug("lastR2R "+lastR2R);
					SVGText svgText = (SVGText) lastElement;
					String textContent = svgText.getText();
					if (textContent == null) {
						throw new RuntimeException("Null text");
					}
					LOG.debug("text "+textContent.length());
					LOG.debug("char "+(textContent.length() > 0 ? "NULL" : textContent.charAt(0)));
				}
				throw new RuntimeException("Invalid box: "+cc+" / "+bbox.getXRange()+" / "+bbox.getYRange()+" /"+lastElement.getClass());
			}
			emptyBoxList.add(bbox);
		}
		return bbox;
	}

	private boolean boxLargeEnough(Double chunkWidth, BoxEdge edge, Real2Range emptyBox) {
		RealRange rr = getRange(emptyBox, edge);
		return (rr == null) ? false : rr.getRange() >= chunkWidth;
	}

	public void copyAttributesAndChildrenFromSVGElement(SVGElement g) {
		SVGElement gcopy = (SVGElement) g.copy();
		Elements gcopyChildren = gcopy.getChildElements();
		for (int i = 0; i < gcopyChildren.size(); i++) {
			Element child = gcopyChildren.get(i);
			child.detach();
			this.appendChild(child);
		}
		this.copyAttributes(g);
//		this.setChunkStyleValue(this.getChunkStyleName());
	}
	
	private RealRange getRange(Real2Range box, BoxEdge edge) {
		RealRange r = null;
		if (box == null) {
			r = null;
		} else if (BoxEdge.YMIN.equals(edge) || BoxEdge.YMAX.equals(edge)) {
			r = box.getYRange();
		} else if (BoxEdge.XMIN.equals(edge) || BoxEdge.XMAX.equals(edge)) {
			r = box.getXRange();
		}
		return r;
	}

	private void ensureEmptyBoxList(BoxEdge edge) {
		if (emptyBoxList == null) {
			ensurePopulatedBoundingBoxManager();
			emptyBoxList = boundingBoxManager.createEmptyBoxList(edge);
			if (emptyBoxList == null) {
				throw new RuntimeException("emptyBoxList should not be null");
			}
		}
	}

	private boolean elementLagsBehindBox(BoxEdge edge, Real2Range box, SVGElement element) {
		Boolean lags = null;
		if (box == null && element != null) {
			lags = true;
		} else {
			element.setBoundingBoxCached(true);
			double elemCoord = getRange(element.getBoundingBox(), edge).getMin();
			RealRange rr = getRange(box, edge);
			if (rr != null) {
				double boxCoord = rr.getMax();
				lags = elemCoord < boxCoord;
			}
		}
		return (lags == null) ? false : lags;
	}

	private void addSVGElement(SVGElement element) {
		ensureDescendantList();
		descendantSVGElementList.add(element);
		if (this.getParent() != null) {
			element.detach();
			this.appendChild(element);
		}
		ensureBoundingBoxManager();
		boundingBoxManager.add(element.getBoundingBox());
	}
	
	private void ensureDescendantList() {
		if (descendantSVGElementList == null) {
			descendantSVGElementList = new ArrayList<SVGElement>();
		}
	}

	void setBoundingBoxCacheForSelfAndDescendants(boolean cached) {
		Long time0 = System.currentTimeMillis();
		for (SVGElement element : descendantSVGElementList) {
			element.setBoundingBoxCached(cached);
		}
		LOG.trace("set cache "+(System.currentTimeMillis()-time0));
	}
	
	private Chunk makeChunk(Double chunkWidth, BoxEdge edge, Integer decimalPlaces, int count) {
		Chunk chunk;
		chunk = new Chunk();
		chunk.setBoundingBoxCached(true);
		chunk.setBoundingBoxAttribute(decimalPlaces);
		chunk.addAttribute(new Attribute("edge", ""+edge));
		chunk.addAttribute(new Attribute("width", ""+chunkWidth));
		chunk.setTitle("chunk"+count);
		return chunk;
	}

	public static Chunk createAndReplace(SVGElement element) {
		Chunk chunk = new Chunk();
		chunk.copyAttributesAndChildrenFromSVGElement(element);
		element.getParent().replaceChild(element, chunk);
		return chunk;
	}
	
	public Boolean isTextChunk() {
		ensureDescendantSVGClassSet();
		return descendantSVGClassSet != null && descendantSVGClassSet.size() == 1 && descendantSVGClassSet.contains(SVGText.class);
	}

	private void ensureDescendantSVGClassSet() {
		descendantSVGClassSet = new HashSet<Class<?>>();
		for (SVGElement element : descendantSVGElementList) {
			descendantSVGClassSet.add(element.getClass());
		}
	}

	public String getStringValue() {
		
		StringBuilder sb = new StringBuilder();
		for (SVGElement line : descendantSVGElementList) {
			sb.append(line.getValue()+"\n");
		}
		return sb.toString();
	}

	public BoundingBoxManager getBoundingBoxManager() {
		ensurePopulatedBoundingBoxManager();
		return boundingBoxManager;
	}
	
	private void ensureBoundingBoxManager() {
		if (boundingBoxManager == null) {
			boundingBoxManager = new BoundingBoxManager();
		}
	}

}
