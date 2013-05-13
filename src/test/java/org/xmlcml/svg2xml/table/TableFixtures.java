package org.xmlcml.svg2xml.table;

import java.io.File;
import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.Fixtures;

public class TableFixtures {

	// full table
	public final static File TABLEFILE = new File(Fixtures.ANALYZER_DIR, "bmc174.table1.svg");
	public final static File TDBLOCKFILE = new File(Fixtures.ANALYZER_DIR, "bmc174.table1.tdbody.svg");
	public final static File CELL00FILE = new File(Fixtures.ANALYZER_DIR, "bmc174.table1.cell.0.0.svg");
	
	public final static File HROWFILE = new File(Fixtures.ANALYZER_DIR, "bmc174.table1.hrow.svg");
	public final static File HROW0FILE = new File(Fixtures.ANALYZER_DIR, "bmc174.table1.hrow.0.svg");
	public final static File HROW1FILE = new File(Fixtures.ANALYZER_DIR, "bmc174.table1.hrow.1.svg");
	public final static File HROW2FILE = new File(Fixtures.ANALYZER_DIR, "bmc174.table1.hrow.2.svg");
	
	public static final String TEXT_OR_PATH_XPATH = "//svg:text|//svg:path";
	public static final String PATH_XPATH = "//svg:path";
	public static final String TEXT_XPATH = "//svg:text";
	
	public static final Real2Range PAGE_BOX = new Real2Range(new RealRange(0., 600.), new RealRange(0., 800.));
	
	public static GenericChunk createGenericChunkFromElements(File file) {
		GenericChunk genericChunk = new GenericChunk();
		List<SVGElement> elementList = readFileAndXPathFilterToElementList(file, TEXT_OR_PATH_XPATH);
		genericChunk.setElementList(elementList);
		return genericChunk;
	}
	
	public static GenericChunk createCellFromMaskedElements(
			File file, RealRangeArray horizontalMask, RealRangeArray verticalMask) {
		List<? extends SVGElement> elementList = readFileAndXPathFilterToElementList(file, TEXT_OR_PATH_XPATH);
		elementList = SVGElement.filterHorizontally(elementList, horizontalMask);
		elementList = SVGElement.filterVertically(elementList, verticalMask);
		GenericChunk genericChunk = createGenericChunkFromElements(TableFixtures.CELL00FILE);
		return genericChunk;
	}

	public static List<SVGElement> readFileAndXPathFilterToElementList(File file, String xpath) {
		Element element = CMLUtil.parseQuietlyToDocument(file).getRootElement();
		SVGElement svgElement = SVGElement.readAndCreateSVG(element);
		List<SVGElement> elementList = SVGUtil.getQuerySVGElements(svgElement, xpath);
		return elementList;
	}

}
