package org.xmlcml.svg2xml.misc;

import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGText;


public class LAPDFWord extends LAPDFElement {

	private static final String PT = "pt";
	private static final String FONT = "font";
	private static final String STYLE = "style";
	public final static String TAG = "Word";
	private String font;
	
	public LAPDFWord() {
		super(TAG);
	}
	
	@Override
	protected void processAttribute(String name, String value)  {
		if (FONT.equals(name)) {
			this.setFont(value);
		} else {
			LOG.debug("Unprocessed attribute: "+name+" in "+this.getClass());
		}
	}

	protected void setFont(String font) {
		this.font = font;
	}

	protected SVGElement createSVGElement() {
		SVGText svg = new SVGText();
		svg.setXY(new Real2(this.getX1(), this.getY1()));
		svg.setText(this.getValue());
		svg.setFontFamily(this.getFont());
		Double size = this.getFontSize();
		svg.setFontSize(size);
		return svg;
	}

	Double getFontSize() {
		Double size = null;
		String style = this.getStyle();
		if (style != null) {
			String[] styles = style.split(";");
			for (String s : styles) {
				String[] nvs = s.split(":");
				String n = nvs[0].trim();
				String v = nvs[1].trim();
				if ("font-size".equals(n) && v.endsWith(PT)) {
					size = new Double(v.substring(0, v.length()-2));
				}
			}
		}
		return size;
	}

	private String getFont() {
		return font;
	}
	
	@Override
	protected SVGRect createRect() {return null;}
}
