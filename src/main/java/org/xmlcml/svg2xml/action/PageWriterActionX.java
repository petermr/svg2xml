package org.xmlcml.svg2xml.action;

import java.io.File;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.util.GraphUtil;
import org.xmlcml.svg2xml.util.ToXML;
import org.xmlcml.html.HtmlUl;

public class PageWriterActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(PageWriterActionX.class);
	
	private File file;
	private String filename;
	private HtmlUl ul;
	public PageWriterActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	
	
	public final static String TAG ="pageWriter";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	public static String MAKE_DISPLAY = "makeDisplay";

	static {
		ATTNAMES.add(AbstractActionX.ACTION);
		ATTNAMES.add(PageActionX.DELETE_XPATHS);
		ATTNAMES.add(AbstractActionX.DELETE_NAMESPACES);
		ATTNAMES.add(AbstractActionX.FILENAME);
		ATTNAMES.add(AbstractActionX.FORMAT);
		ATTNAMES.add(AbstractActionX.NAME);
		ATTNAMES.add(AbstractActionX.XPATH);
		ATTNAMES.add(MAKE_DISPLAY);
	}

	/** constructor
	 */
	public PageWriterActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageWriterActionX(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
				AbstractActionX.FILENAME,
		});
	}
	
	@Override
	public void run() {
		String format = getFormat();
		if (format != null) {
			LOG.warn("FORMAT NYI");
		}
		filename = getFilename();
		String xpath = getXPath();
		String name = getName();
		if (xpath != null) {
			writeSVGForXPath(xpath);
		} else if (name != null) {
			writeVariableValueToFile(name);
		} else {
			writeFile(getSVGPage());
		}
	}

	private void writeVariableValueToFile(String name) {
		Object obj = semanticDocumentActionX.getVariable(name);
		if (obj instanceof ToXML) {
			writeFile((ToXML) obj);
		} else {
			writeFile(obj.toString());
		}
	}

	private void writeSVGForXPath(String xpath) {
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(getSVGPage(), xpath);
		if (elements.size() == 0) {
			warn("No elements found: "+xpath);
		} else if (elements.size() > 1) {
			info("Multiple elements found ("+elements.size()+"): "+xpath);
		} else {
			SVGElement svgElement = elements.get(0);
			SVGSVG svg = null;
			if (!(svgElement instanceof SVGSVG)) {
				svg = new SVGSVG();
				svgElement.detach();
				svg.appendChild(svgElement);
			} else {
				svg = (SVGSVG) svgElement;
			}
			writeFile(svg);
		}
	}

	private void writeFile(ToXML toXML) {
		writeFile(toXML.toXML());
	}
	
	private void writeFile(String s) {
		try {
			GraphUtil.createFile(filename);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(s.getBytes());
			fos.close();
		} catch (IOException ioe) {
			throw new RuntimeException("Cannot write string to file: ", ioe);
		}
	}
	
	private void writeFile(Element element) {
		LOG.debug("writing "+new File(filename).getAbsolutePath());
		GraphUtil.writeFileAsSVGSVGWithMouse(filename, element);
	}
	
	private void deleteXPaths(SVGSVG page, String deleteXPaths) {
		String[] delXPath = deleteXPaths.split(CMLConstants.S_SEMICOLON);
		for (String deleteXPath : delXPath) {
			deleteXPath = deleteXPath.trim();
			Nodes nodes = page.query(deleteXPath);
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
		}
	}

	private SVGElement deleteNamespaces(SVGSVG page, String[] namespaces) {
		String xml = page.toXML();
		for (String namespace : namespaces) {
			String nn = "xmlns=\""+namespace+"\"";
			xml = xml.replaceAll(nn, "");
		}
		Element element = null;
		try {
			element = new Builder().build(new StringReader(xml)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("BUG in deleteNamespaces", e);
		}
		return SVGElement.readAndCreateSVG(element);
	}

}
