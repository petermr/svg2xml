package org.xmlcml.svg2xml.paths;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.svg2xml.page.BoundingBoxManager;

public class ElementNeighbourhood {
	private SVGElement element;
	private List<SVGElement> neighbourList;
	private Real2Range extendedBBox;
	
	public ElementNeighbourhood(SVGElement element) {
		this.element = element;
	}

	public SVGElement getElement() {
		return element;
	}

	public List<SVGElement> getNeighbourList() {
		ensureNeighbourList();
		return neighbourList;
	}
	
	public Real2Range ensureExtendedBoundingBox(double eps) {
		if (extendedBBox == null) {
			extendedBBox = BoundingBoxManager.createExtendedBox(element, eps);
		}
		return extendedBBox;
	}

	public boolean isTouching(SVGElement fpn, double eps) {
		ensureExtendedBoundingBox(eps);
		Real2Range fpnBox = fpn.getBoundingBox();
		return fpnBox.intersectionWith(extendedBBox) != null;
	}

	public void addNeighbour(SVGElement neighbour) {
		ensureNeighbourList();
		neighbourList.add(neighbour);
	}

	public void addNeighbourList(List<SVGElement> newNeighbourList) {
		ensureNeighbourList();
		neighbourList.addAll(newNeighbourList);
	}

	private void ensureNeighbourList() {
		if (neighbourList == null) {
			neighbourList = new ArrayList<SVGElement>();
		}
	}

	public void remove(SVGElement oldElem) {
		if (neighbourList != null) {
			if (!neighbourList.remove(oldElem))  {
				throw new RuntimeException("cannot remove oldElem"); 
			}
		}
	}
	
	/*
	private SVGElement element;
	private List<SVGElement> neighbourList;
	private Real2Range extendedBBox;
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n ......element "+ element.getId());
		sb.append("\n ......neighbours: "+((neighbourList == null) ? 0 : neighbourList.size()));
		if (neighbourList != null) {
			for (SVGElement neighbour : neighbourList) {
				sb.append("\n ........."+neighbour.getId());
			}
		}
		return sb.toString();
	}
}
