package org.xmlcml.svgplus.page;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgplus.util.GraphUtil;
import org.xmlcml.svgplus.util.ToXML;
import org.xmlcml.html.HtmlUl;

public class PageWriterAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(PageWriterAction.class);
	
	private File file;
	private String filename;
	private HtmlUl ul;
	public PageWriterAction(AbstractActionElement pageActionCommand) {
		super(pageActionCommand);
	}
	
	@Override
	public void run() {
		String format = getFormat();
		if (format != null) {
			LOG.warn("FORMAT NYI");
		}
		String deleteXPaths = getDeleteXPaths();
		SVGSVG svgPageCopy = getSVGPageCopy();
		if (deleteXPaths != null) {
			deleteXPaths(svgPageCopy, deleteXPaths);
		}
		String[] deleteNamespaces = getDeleteNamespaces();
		if (deleteNamespaces != null) {
			svgPageCopy = (SVGSVG) deleteNamespaces(svgPageCopy, deleteNamespaces);
		}
		filename = getFilename();
		String xpath = getXPath();
		String name = getName();
		if (xpath != null) {
			List<SVGElement> elements = SVGUtil.getQuerySVGElements(svgPageCopy, xpath);
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
		} else if (name != null) {
			Object obj = getSemanticDocumentAction().getVariable(name);
			if (obj instanceof ToXML) {
				writeFile((ToXML) obj);
			} else {
				writeFile(obj.toString());
			}
		} else {
			writeFile(svgPageCopy);
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
