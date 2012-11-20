package org.xmlcml.svgplus.figure;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Int2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svgplus.core.Chunk;

public class PixelBoxAnnotator {
	private static Logger LOG = Logger.getLogger(PixelBoxAnnotator.class);

	private Map<Integer, String> fillMap;
	private PixelBox[][] pixelBoxArray;
	private Double xmin;
	private Double xmax;
	private Double ymin;
	private Double ymax;
	private Double deltax;
	private Double deltay;
	private int xBoxCount;
	private int yBoxCount;
	private int NCOLOR;

	private Set<Int2> pixelSet;

	private int fragmentCount;

	private List<? extends SVGElement> elements;

	private Chunk parentChunk;
	
	public PixelBoxAnnotator(Chunk parentChunk, List<? extends SVGElement> elements) {
		this.parentChunk = parentChunk;
		this.elements = elements;
		makeFillMap();
	}
	
	private void makeFillMap() {
		fillMap = new HashMap<Integer, String>();
		fillMap.put(-2, "none");
		fillMap.put(-1, "gray");
		fillMap.put(0, "pink");
		fillMap.put(1, "red");
		fillMap.put(2, "green");
		fillMap.put(3, "blue");
		fillMap.put(4, "yellow");
		fillMap.put(5, "cyan");
		fillMap.put(6, "magenta");
		fillMap.put(7, "brown");
		NCOLOR = 8;
	}

	void createPixelBoxMap(Real2 deltaXY) {
		xmin = parentChunk.getBoundingBox().getXRange().getMin();
		xmax = parentChunk.getBoundingBox().getXRange().getMax();
		ymin = parentChunk.getBoundingBox().getYRange().getMin();
		ymax = parentChunk.getBoundingBox().getYRange().getMax();
		deltax = deltaXY.getX();
		deltay = deltaXY.getY();
		Double x = xmin;
		xBoxCount = (int) ((xmax - xmin) / deltax) +1;
		yBoxCount = (int) ((ymax - ymin) / deltay) +1;
		pixelBoxArray = new PixelBox[xBoxCount][yBoxCount];
		for (int i = 0; i < xBoxCount; i++) {
			pixelBoxArray[i] = new PixelBox[yBoxCount];
			double y = ymin;
			RealRange xRange = new RealRange(x, x+deltax);
			for (int j = 0; j < yBoxCount; j++) {
				RealRange yRange = new RealRange(y, y+deltay);
				pixelBoxArray[i][j] = new PixelBox(xRange, yRange);
				pixelBoxArray[i][j].setClusterNumber(PixelBox.UNMARKED);
				y += deltay;
			}
			x += deltax;
		}
	}
	
	void markPixelsForElements() {
		pixelSet = new HashSet<Int2>();
		for (SVGElement element : elements) {
			markPixels(element);
		}
		LOG.trace("Pixel set: "+pixelSet.size());
//		drawBoxes(0.3);
	}
	
	private void markPixels(SVGElement element) {
		Real2Range bbox = element.getBoundingBox();
		RealRange xRange = bbox.getXRange();
		RealRange yRange = bbox.getYRange();
		int imin = Math.max(0,  (int) ((xRange.getMin()-deltax - xmin) / deltax));
		int jmin = Math.max(0,  (int) ((yRange.getMin()-deltay - ymin ) / deltay));
		int imax = Math.min(xBoxCount, (int) ((xRange.getMax()+deltax - xmin) / deltax));
		int jmax = Math.min(yBoxCount, (int) ((yRange.getMax()+deltay - ymin) / deltay));
		LOG.trace("imin, imax,jmin,jmax,"+imin+"/"+imax+"/"+jmin+"/"+jmax);
		for (int i = imin; i < imax; i++) {
			for (int j = jmin; j < jmax; j++) {
				pixelSet.add(new Int2(i,j));
				pixelBoxArray[i][j].setClusterNumber(PixelBox.UNPROCESSED);
			}
		}
	}
	
	List<FigureFragment> regroupElementsIntoFigureFragments() {
		List<SVGG> newChunks = new ArrayList<SVGG>();
		LOG.trace("fragment count: "+fragmentCount);
		String id = parentChunk.getId();
		for (int i = 0; i < fragmentCount; i++) {
			SVGG chunk = new SVGG();
			chunk.setTitle("whitespaceChunk"+i);
//			chunk.addAttribute(new Attribute(Chunk.CHUNK_STYLE, FigureAnalyzer.FRAGMENT));
			newChunks.add(chunk);
			parentChunk.appendChild(chunk);
			chunk.setId(id+"."+i);
		}
		for (SVGElement element : elements) {
			int clusterNumber = getClusterNumber(element);
			if (clusterNumber >= 0) {
				element.detach();
				newChunks.get(clusterNumber).appendChild(element);
			}
		}
		List<FigureFragment> fragmentList = new ArrayList<FigureFragment>();
		for (SVGG newChunk : newChunks) {
			FigureFragment fragment = new FigureFragment(newChunk);
			fragmentList.add(fragment);
		}
		return fragmentList;
	}
	
	private int getClusterNumber(SVGElement element) {
		Real2Range bbox = element.getBoundingBox();
		RealRange xRange = bbox.getXRange();
		RealRange yRange = bbox.getYRange();
		int imin = Math.max(0,  (int) ((xRange.getMin()-deltax - xmin) / deltax));
		int jmin = Math.max(0,  (int) ((yRange.getMin()-deltay - ymin ) / deltay));
		return pixelBoxArray[imin][jmin].getClusterNumber();
	}

	public void drawBoxes(double opacity) {
		int count = 0;
		for (int i = 0; i < xBoxCount; i++) {
			for (int j = 0; j < yBoxCount; j++) {
				Integer clusterNumber = pixelBoxArray[i][j].getClusterNumber();
				if (clusterNumber != PixelBox.UNMARKED) {
					String fill = fillMap.get(clusterNumber);
					SVGElement.drawBox(pixelBoxArray[i][j], parentChunk, "none", fill, 0.0, opacity);
					count++;
				}
			}
		}
		LOG.trace("marked pixels: "+count);
	}

	public void findFigureFragments() {
		fragmentCount = 0;
		Int2 ij = null;
		Deque<Int2> unprocessed = new ArrayDeque<Int2>();
		while (!pixelSet.isEmpty()) {
			if (unprocessed.isEmpty()) {
				fragmentCount++;
				ij = pixelSet.iterator().next();
				unprocessed.add(ij);
			} else {
				ij = unprocessed.remove();
			}
			pixelBoxArray[ij.getX()][ij.getY()].setClusterNumber(fragmentCount-1);
			pixelSet.remove(ij);
			List<Int2> neighbours = getUnprocessedNeighbours(ij);
			for (Int2 ii : neighbours) {
				if (pixelSet.contains(ii)) {
					unprocessed.add(ii);
				}
				pixelSet.remove(ii);
			}
		}
	}

	private List<Int2> getUnprocessedNeighbours(Int2 ij) {
		List<Int2> neighbours = new ArrayList<Int2>();
		for (int ii = -1; ii < 2; ii++) {
			int iii = ij.getX() + ii;
			if (iii < 0 || iii >= xBoxCount) {
				continue;
			}
			for (int jj = -1; jj < 2; jj++) {
				if (ii != 0 || jj != 0) { // omit the current point
					int jjj = ij.getY() + jj;
					if (jjj < 0 || jjj >= yBoxCount) {
						continue;
					}
					int clusterNumber = pixelBoxArray[iii][jjj].getClusterNumber();
					if (clusterNumber == PixelBox.UNPROCESSED) {
						neighbours.add(new Int2(iii,jjj));
					}
				}
			}
		}
		return neighbours;
	}

	public List<FigureFragment> groupIntoWhitespaceSeparatedFragments(Real2 deltaXY) {
		LOG.trace("create whitespace clusters: "+deltaXY);
		createPixelBoxMap(deltaXY);
		markPixelsForElements();
		findFigureFragments();
		List<FigureFragment> fragmentList = regroupElementsIntoFigureFragments();
		return fragmentList;
	}
}
