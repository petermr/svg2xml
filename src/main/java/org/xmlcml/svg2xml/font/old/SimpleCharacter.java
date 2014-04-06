package org.xmlcml.svg2xml.font.old;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.graphics.svg.SVGUtil;

/** holds a character in a SimpleFont, optinally with width
 * 
 * @author pm286
 *
 */
public class SimpleCharacter {

	public final static String TAG = "simpleCharacter";
	private static final String WIDTH = "width";
	
	private String charr;
	private Double width;

	public SimpleCharacter(String charr) {
		this.charr = charr;
	}

	public SimpleCharacter(String charr, double width) {
		this(charr);
		this.width = width;
	}
	
	public SimpleCharacter(SimpleCharacter sc) {
		this.charr = sc.charr;
		this.width = sc.width;
	}

	public SimpleCharacter(Element sc) {
		readXML(sc);
	}

	public Element toXML() {
		Element element = new Element(TAG);
		if (width != null) {
			width = SVGUtil.decimalPlaces(width, 3);
			element.addAttribute(new Attribute(WIDTH, ""+width));
		}
		element.appendChild(charr);
		return element;
	}
	
	public void readXML(Element simpleChar) {
		if (simpleChar.getLocalName().equals(TAG)) {
			String value = simpleChar.getValue();
			if (value.length() != 1) {
				throw new RuntimeException("Only characters of length 1 allowed, found: "+value);
			}
			this.charr = value;
			Attribute widthAttribute = simpleChar.getAttribute(WIDTH);
			if (widthAttribute != null) {
				try {
					this.width = new Double(widthAttribute.getValue());
				} catch (NumberFormatException nfe) {
					throw new RuntimeException("width must be number", nfe);
				}
			}
		}
	}
	
	public String getCharacter() {
		return charr;
	}
	
	public Double getWidth() {
		return width;
	}
}
