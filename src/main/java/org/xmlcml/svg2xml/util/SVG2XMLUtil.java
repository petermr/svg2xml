package org.xmlcml.svg2xml.util;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

public class SVG2XMLUtil {

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
	
	private static void tidyChildren(Element element, String tag) {
		int nChild = element.getChildCount();
		System.out.println();
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

	private static void printElement(String x, Element element) {
		int nChild = element.getChildCount();
		System.out.print(x+"...");
		for (int i = 0; i < nChild; i++){
			Node child = element.getChild(i);
			System.out.print("{"+child.toXML()+"}"+((child instanceof Element)? ((Element)child).getChildCount() : ""));
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


}
