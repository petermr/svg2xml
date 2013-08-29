package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.html.HtmlTr;

public class TableRow extends TableChunk {

	private final static Logger LOG = Logger.getLogger(TableRow.class);
	private List<TableCell> cellList;
	
	public TableRow(RealRangeArray horizontalMask, RealRangeArray verticalMask) {
		super(horizontalMask, null);
	}
	
	public TableRow() {
		super();
	}

	public List<TableCell> createCells() {
		setCellList(new ArrayList<TableCell>());
		return getCellList();
	}

	public void createAndAnalyzeCells(RealRangeArray horizontalMask) {
		createCells();
		for (RealRange range : horizontalMask) {
			TableCell cell = new TableCell();
			getCellList().add(cell);
			for (SVGElement element : elementList) {
				RealRange elemRange = element.getBoundingBox().getXRange();
				if (range.includes(elemRange)) {
					cell.add(element);
				}
			}
		}
	}

	public List<TableCell> getCellList() {
		return cellList;
	}

	public void setCellList(List<TableCell> cellList) {
		this.cellList = cellList;
	}

	public HtmlElement createHtmlElement() {
		HtmlTr tr = new HtmlTr();
		for (TableCell cell : cellList) {
			tr.appendChild(cell.createHtmlElement());
		}
		return tr;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("{");
		for (TableCell cell : cellList) {
			sb.append("{"+cell.toString()+"}");
		}
		sb.append("}");
		return sb.toString();
	}

	public static HtmlElement convertBodyHeader(HtmlElement bodyOneTr) {
		Nodes nodes = bodyOneTr.query(".//*[local-name()='td']");
		HtmlElement tr = new HtmlTr();
		for (int i = 0; i < nodes.size();i++) {
			HtmlTh th = new HtmlTh();
			tr.appendChild(th);
			CMLUtil.transferChildren((Element) nodes.get(i), th);
		}
		return tr;
	}
}
