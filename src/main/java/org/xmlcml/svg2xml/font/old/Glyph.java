package org.xmlcml.svg2xml.font.old;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPathPrimitive;
import org.xmlcml.graphics.svg.path.LinePrimitive;
import org.xmlcml.graphics.svg.path.PathPrimitiveList;

/**
  <glyph character="?3" signature="MLLCCCLLLCCCCCCCLLLZMLCCCCCCCLL">
    <path style="clip-path:url(#clipPath1); stroke:none;" 
    d="M0.903 6.751 L1.529 3.753 L2.58 3.753 C2.939 3.753 3.199 3.79 3.36 3.861 C3.518 3.934 3.674 4.085 ...
     ...3.022 L1.682 3.022 L2.16 0.742 " xmlns="http://www.w3.org/2000/svg"/>
  </glyph>
  or
  <glyph character="m" width="4.3">
  </glyph>
  
 * @author pm286
 *
 */
public class Glyph {

	private static final String CHARACTER = "character";
	private static final String GLYPH = "glyph";
	private static final String SIGNATURE = "signature";
	
	Element glyphElement;
	private SVGPath svgPath;
	private String sig;
	private String glyphChar;
	
	public Glyph(Element glyphElement) {
		this.glyphElement = (Element) glyphElement.copy();
		processChar();
		processSVGPath();
		processSig();
	}
	
	public Glyph(String character, double width) {
		
	}
	
	private Glyph() {
		this.glyphElement = new Element(GLYPH);
	}
		
	public Glyph(String character) {
		this();
		setCharacter(character);
	}
	
	public String getCharacter() {
		return glyphElement.getAttributeValue(Glyph.CHARACTER);
	}
	
	private void setCharacter(String character) {
		if (glyphElement.getAttribute(CHARACTER) != null) {
			throw new RuntimeException("Cannot reset glyph@character");
		}
		glyphElement.addAttribute(new Attribute(CHARACTER, character));
	}

	private void processChar() {
		glyphChar = glyphElement.getAttributeValue(CHARACTER);
		if (glyphChar == null) {
			throw new RuntimeException("No @char given for <glyph>");
		}
	}
	
	private void processSVGPath() {
		Nodes paths = glyphElement.query("./*[local-name()='"+SVGPath.TAG+"']");
		if (paths.size() == 0) {
			throw new RuntimeException("<glyph> Missing svg:path child");
		}
		if (paths.size() == 2) {
			throw new RuntimeException("<glyph> cannot process multiple svg:path children ("+paths.size()+")");
		}
		Element path = (Element) paths.get(0);
		svgPath = (SVGPath) SVGElement.readAndCreateSVG(path);
		path.getParent().replaceChild(path,  svgPath);
		
	}
	
	private void processSig() {
		sig = svgPath.getSignature();
		if (glyphElement.getAttribute(SIGNATURE) == null) {
			glyphElement.addAttribute(new Attribute(SIGNATURE, sig));
		} else {
			String sigValue = glyphElement.getAttributeValue(SIGNATURE);
			if (!sigValue.equals(sig)) {
				throw new RuntimeException("mismatched signature ("+sigValue+") with glyph@signature: "+sig);
			}
		}
	}

	public SVGPath getPath() {
		return svgPath;
	}

	public void addPath(SVGPath svgPath) {
		Nodes paths = glyphElement.query("./*[local-name()='"+SVGPath.TAG+"']");
		if (paths.size() != 0) {
			throw new RuntimeException("<glyph> already has svg:path child");
		}
		this.svgPath = new SVGPath(svgPath);
		glyphElement.appendChild(this.svgPath);
	}

	public void setSignature(String sig) {
		if (glyphElement.getAttribute(SIGNATURE) != null) {
			throw new RuntimeException("glyph already has signature");
		} else {
			glyphElement.addAttribute(new Attribute(SIGNATURE, sig));
		}
	}

	public String getSignature() {
		return glyphElement.getAttributeValue(SIGNATURE);
	}

	public String validate() {
		String character = getCharacter();
		if (character == null) {
			throw new RuntimeException("missing char for glyph, element ");
		}
		SVGPath path = getPath();
		if (path == null) {
			throw new RuntimeException("glyph must have a path");
		}
		String sig = getSignature();
		if (sig == null) {
			sig = path.getSignature();
			setSignature(sig);
		}
		return character;
	}
	
	public List<Vector2> getLineVectors() {
		List<Vector2> vectorList = new ArrayList<Vector2>();
		PathPrimitiveList primitiveList = svgPath.ensurePrimitives();
		for (int i = 1; i < primitiveList.size(); i++) {
			SVGPathPrimitive primitive = primitiveList.get(i);
			if (primitive instanceof LinePrimitive) {
				Real2 lastCoords = primitiveList.get(i-1).getLastCoord();
				Line2 line = new Line2(lastCoords, primitive.getFirstCoord());
				Vector2 vector2 = line.getVector();
				vectorList.add(vector2);
			}
		}
		return vectorList;
	}
	


}
