package org.xmlcml.svg2xml.util;

import java.io.File;
import java.io.FileOutputStream;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svg2xml.action.SVGPlusConstantsX;

public class SVG2XMLUtil {

	private final static Logger LOG = Logger.getLogger(SVG2XMLUtil.class);
	
	public static void replaceNodeByChildren(Element node) {
		Element spanParent = (Element) node.getParent();
		int index = spanParent.indexOf(node);
		int nchild = node.getChildCount();
		for (int i = nchild-1; i >= 0; i--) {
			Node spanChild = node.getChild(i);
			spanChild.detach();
			spanParent.insertChild(spanChild, index);
		}
		node.detach();
	}
	
	public static void tidyTagWhiteTag(Element element, String tag) {
		String query = "descendant-or-self::*[count(*[local-name()='"+tag+"']) > 1]'";
		Nodes nodes = element.query(query);
		for (int i = 0; i < nodes.size(); i++) {
			tidyChildren((Element) nodes.get(i), tag);
		}
	}
	
	public static void removeTagWithWhiteContent(Element element, String tag) {
		String query = ".//*[*[local-name()='"+tag+"' and normalize-space(.)='']]'";
		Nodes nodes = element.query(query);
		for (int i = 0; i < nodes.size(); i++) {
			removeTagWithWhiteContent((Element) nodes.get(i));
		}
	}
	
	private static void removeTagWithWhiteContent(Element element) {
		String value = element.getValue();
		Element parent = (Element) element.getParent();
		parent.replaceChild(element, new Text(value));
	}

	private static void tidyChildren(Element element, String tag) {
		int nChild = element.getChildCount();
		for (int i = nChild - 1; i >= 2; i--) {
			Node n0 = element.getChild(i);
			Node n1 = element.getChild(i-1);
			Node n2 = element.getChild(i-2);
			if (n0 instanceof Element && 
					n1 instanceof Text && 
					n2 instanceof Element) {
				Element e0 = (Element) n0;
				Element e2 = (Element) n2;
				Text text = (Text) n1;
				String value = text.getValue();
				if (e0.getLocalName().equalsIgnoreCase(tag) &&
					e2.getLocalName().equalsIgnoreCase(tag) &&
					value.trim().length() == 0) {
					text.detach();
					appendText(e2, text);
					for (int j = 0; j < e0.getChildCount(); j++) {
						Node e0Child = e0.getChild(0);
						e0Child.detach();
						if (e0Child instanceof Text) {
							appendText(e2, (Text) e0Child); 
						} else {
							e2.appendChild(e0Child);
						}
					}
					e0.detach();
					i--;
				}
			}
		}
	}

	private static void appendText(Element e, Text text) {
		if (e.getChildCount() == 0) {
			e.appendChild(text);
		} else {
			Node lastChild = e.getChild(e.getChildCount()-1);
			if (lastChild instanceof Text) {
				String a = lastChild.getValue()+text.getValue();
				((Text) lastChild).setValue(a);
			} else {
				e.appendChild(text);
			}
		}
	}

	public static void writeToSVGFile(File dir, String filename,SVGElement svgElement, boolean debug) {
		if (!(svgElement instanceof SVGSVG)) { 
			SVGSVG svg = new SVGSVG();
			svg.setWidth(600.);
			svg.setHeight(800.);
			svg.appendChild(SVGElement.readAndCreateSVG(svgElement));
			svgElement = svg;
		}
		try {
			if (!filename.endsWith(SVGPlusConstantsX.DOT_SVG)) {
				filename += SVGPlusConstantsX.DOT_SVG;
			}
			File outFile = new File(dir, filename);
			if (debug) {LOG.debug("wrote: "+outFile);}
			CMLUtil.debug(svgElement, new FileOutputStream(outFile), 1);
		} catch (Exception e) {
			throw new RuntimeException("cannot write", e);
		}
	}

	/** trims characters off RHS of string
	 * adds actual length
	 * 
	 * @param string
	 * @param maxlen
	 * @return
	 */
	public static String trim(String string, int maxlen) {
		int l = string.length();
		return (l <= maxlen) ? string : string.substring(0, l)+"... ("+l+" chars)";
	}

	public static String trimText(int max, String s) {
		int l = s.length();
		return (l < max) ? s : s.substring(0,  max)+" ("+l+")...";
	}


}
