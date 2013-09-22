package org.xmlcml.svg2xml.font.old;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class OutlineFont {
	private final static Logger LOG = Logger.getLogger(OutlineFont.class);
	
	private static final String CHAR = "char";
	private static final String STROKE_COUNT = "strokeCount";
	public static final String GENERIC_FAMILY = "genericFamily";
	public static final String FONT_STYLE = "fontStyle";
	public static final String FONT_NAME = "fontName";
	public static final String GLYPH = "glyph";
	private static final String DEFAULT_NAME = "name";
	private static final String DEFAULT_FAMILY = "family";
	
	private Map<String, Glyph> glyphBySigMap = new HashMap<String, Glyph>();
	private Element fontRoot;
	private String fontName;
	private String fontStyle;
	private String genericFamily;
	private Elements glyphs;
	private Map<Integer, List<Glyph>> glyphByCodePoint = new HashMap<Integer, List<Glyph>>();
	private Map<String, Glyph> glyphByCharacterMap = new HashMap<String, Glyph>();
	private double MAXCHARSIZE = 10.0;
	
	private String outfile;
	private Map<String, Integer> countBySigMap;
	private Map<Glyph, Integer> countByGlyphMap;

	public Map<Glyph, Integer> getCountByGlyphMap() {
		return countByGlyphMap;
	}

	public OutlineFont() {
		this(DEFAULT_NAME, DEFAULT_FAMILY);
	}
	
	public OutlineFont(String name, String genericFamily) {
		fontRoot = new Element("font");
		fontRoot.addAttribute(new Attribute(FONT_NAME, name));
		fontRoot.addAttribute(new Attribute(GENERIC_FAMILY, genericFamily));
	}
	
	public OutlineFont(Element fontRoot) {
		this();
		this.fontRoot = (Element) fontRoot.copy();
		analyzeFont();
	}
	
	public static OutlineFont readAndCreateFont(String resource) {
		InputStream inputStream = OutlineFont.class.getClassLoader().getResourceAsStream(resource);
		if (inputStream == null) {
			throw new RuntimeException("Cannot find resource: "+resource);
		}
		return readAndCreateStream(inputStream);
	}

	public static OutlineFont readAndCreateStream(InputStream inputStream) {
		OutlineFont outlineFont = null;
		try {
			Element fontRoot = new Builder().build(inputStream).getRootElement();
			outlineFont = new OutlineFont(fontRoot);
		} catch (Exception e) {
			throw new RuntimeException("Cannot find/parse font", e);
		}
		return outlineFont;
	}
	
	public Element getFontRoot() {
		return fontRoot;
	}

	public void setFontRoot(Element fontRoot) {
		this.fontRoot = fontRoot;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public String getGenericFamily() {
		return genericFamily;
	}

	public void setGenericFamily(String genericFamily) {
		this.genericFamily = genericFamily;
	}

	private void analyzeFont() {
		fontName = fontRoot.getAttributeValue(FONT_NAME);
		fontStyle = fontRoot.getAttributeValue(FONT_STYLE);
		genericFamily = fontRoot.getAttributeValue(GENERIC_FAMILY);
		glyphs = fontRoot.getChildElements(GLYPH);
		registerGlyphs();
	}
	
	public void sortByCodePoint() {
		for (int i = 127; i> 32; i--) {
			List<Glyph> glyphList = glyphByCodePoint.get((Integer)i);
			if (glyphList != null) {
				for (Glyph glyph : glyphList) {
					glyph.glyphElement.detach();
					fontRoot.insertChild(glyph.glyphElement, 0);
				}
			} else {
//				System.out.println("Null "+i);
			}
		}
	}
	
	private void registerGlyphs() {
		for (int i = 0; i < glyphs.size(); i++) {
			Glyph glyph = new Glyph((Element) glyphs.get(i));
			registerGlyph(glyph, i);
		}
	}
	
	private void registerGlyph(Glyph glyph, int serial) {
		glyph.validate();
		String character = glyph.getCharacter();
		Integer codePoint = (int) character.codePointAt(0);
		List<Glyph> glyphList = glyphByCodePoint.get(codePoint);
		if (glyphList == null) {
			glyphList = new ArrayList<Glyph>();
			glyphByCodePoint.put(codePoint, glyphList);
		}
		if (!glyphList.contains(glyph)) {
			glyphList.add(glyph);
		}
		glyphBySigMap.put(glyph.getSignature(), glyph);
	}

//	public void processEquivalence(Equivalence equivalence) {
//		equivalence.checkAgainstFont(this);
//	}
	
	public String toString() {
		return "fontName: "+fontName+"; style: "+fontStyle+"; family "+genericFamily;
	}

	public Glyph getGlyphBySig(String sig) {
		return glyphBySigMap.get(sig);
	}

	public void addGlyph(Glyph glyph) {
		Element glyphElement = (Element) glyph.glyphElement.copy();
		fontRoot.appendChild(glyphElement);
		glyphBySigMap.put(glyph.getSignature(), glyph);
	}
	
//	public void analyzePaths(ShapeAnalyzer analyzer) {
//		analyzePaths(analyzer.getPaths());
//	}
	
	public Map<String, Integer> analyzePaths(List<SVGPath> svgPathList) {
		countBySigMap = new HashMap<String, Integer>();
		countByGlyphMap = new HashMap<Glyph, Integer>();
		int unknown = 0;
		for (SVGPath svgPath : svgPathList) {
			Real2Range bbox = svgPath.getBoundingBox();
			if (bbox.getYRange().getRange() > MAXCHARSIZE ) {
				continue;
			}
			String sig = svgPath.getSignature();
			Glyph glyph = glyphBySigMap.get(sig);
			if (glyph == null) {
				glyph = addNewGlyph("?"+(++unknown), svgPath);
			}
			Integer count = countBySigMap.get(sig);
			if (count == null) {
				count = new Integer(0);
				countBySigMap.put(sig, count);
				countByGlyphMap.put(glyph, count);
			}
			count = count+1;
			countBySigMap.put(sig, count);
			countByGlyphMap.put(glyph, count);
		}
		return countBySigMap;
	}

	@Deprecated // NYI
	public List<Glyph> listGlyphsByFrequency() {
		List<Integer> frequencies = Arrays.asList(countBySigMap.keySet().toArray(new Integer[0]));
		return null;
	}

	public void plotGlyphs(OutputStream glyphStream) {
		SVGSVG svg = new SVGSVG();
		double xmin = 20;
		double xmax = 500;
		double x = 0;
		double y = 20;
		double dx = 40;
		double dy = 75;
		double sx = 30.;
		double sy = 30.;
		this.sortByCodePoint();
		//get scales
		Real2Range maxbox = new Real2Range();
		glyphByCharacterMap = new HashMap<String, Glyph>();
		for (String sig : glyphBySigMap.keySet()) {
			Glyph glyph = glyphBySigMap.get(sig);
			SVGPath path = (SVGPath)glyph.getPath();
			if (path != null) {
				Real2Range bbox = path.getBoundingBox();
				maxbox = maxbox.plus(bbox);
				glyphByCharacterMap.put(glyph.getCharacter(), glyph);
			}
		}
		double scale = Math.max(maxbox.getXRange().getRange(), maxbox.getYRange().getRange());
		sx = sx/scale;
		sy = sy/scale;
		
		List<String> keys = new ArrayList<String>(glyphByCharacterMap.keySet());
		Collections.sort(keys);
		for (String character : keys) {
			Glyph glyph = glyphByCharacterMap.get(character);
			SVGPath path = (SVGPath)glyph.getPath();
			if (path != null) {
				SVGG g = new SVGG();
				g.applyTransform(new Transform2(new double[]{1, 0, x, 0, 1, y, 0, 0, 1}));
				svg.appendChild(g);
				SVGG gglyph = new SVGG();
				g.appendChild(gglyph);
//				gglyph.applyTransform(new Transform2(new double[]{sx, 0, 0, 0, sy, 0, 0, 0, 1}));
				x += dx;
				if (x > xmax) {
					x = xmin;
					y += dy;
				}
				gglyph.appendChild(path.copy());
				SVGText text = new SVGText(new Real2(0, dy*0.5), character);
				text.addAttribute(new Attribute("font-size", ""+10));
				g.appendChild(text);
			}
		}
		try {
			CMLUtil.debug(svg, glyphStream, 2);
		} catch (IOException e) {
			throw new RuntimeException("Cannout output glyphs", e);
		}
	}
	
	public void debug(String msg) {
		CMLUtil.debug(fontRoot, msg);
	}

	Glyph addNewGlyph(String charname, SVGPath svgPath) {
		String sig = svgPath.getSignature();
		Glyph glyph = new Glyph(charname);
		svgPath.normalizeOrigin();
		glyph.addPath(svgPath);
		glyph.setSignature(sig);
		addGlyph(glyph);
		return glyph;
	}

	
	private void analyze(SVGElement pathElement) {
		this.analyze(pathElement, false);
	}

	private void analyze(SVGElement pathElement, Boolean stats) {
		if (pathElement != null) {
			Nodes paths = pathElement.query("//*[local-name()='g']/*[local-name()='path']");
			List<SVGPath> svgPathList = new ArrayList<SVGPath>();
			for (int i = 0; i < paths.size(); i++) {
				svgPathList.add((SVGPath) paths.get(i));
			}
			this.analyzePaths(svgPathList);
		}
	}

	private static void runFont(String[] args) throws Exception {

		OutlineFont outlineFont = new OutlineFont();
		String genericFamily = null;
		String infontfile = null;
		String outglyphfile = null;
		String name = null;
		String pathfile = null;
		String outfontfile = null;
		OutlineFont font = null;
		
		SVGElement pathElement = null;
		Boolean stats = false;
		int i = 0;
		while (i < args.length) {
//			System.out.println(i);
			if (false) {
			} else if ("-c".equals(args[i])) {
				outlineFont.setPathfile(args[++i]);i++;
			} else if ("-gf".equals(args[i])) {
				outlineFont.setGenericFamily(args[++i]);i++;
			} else if ("-og".equals(args[i])) {
				outlineFont.setOutglyphfile(args[++i]);i++;
			} else if ("-if".equals(args[i])) {
				outlineFont.setInfontfile(args[++i]);i++;
			} else if ("-n".equals(args[i])) {
				outlineFont.setName(args[++i]);i++;
			} else if ("-of".equals(args[i])) {
				outlineFont.setOutfontfile(args[++i]);i++;
			} else if ("-st".equals(args[i])) {
				outlineFont.setStats(true);i++;
			} else {
				LOG.trace("Unknown arg: ("+args[i++]+")");
			}
		}
		if (infontfile == null) {
			if (name != null && genericFamily != null) {
				font = new OutlineFont(name, genericFamily);
			} else {
				System.err.println("Must give fontname and generic family to create new font");
			}
		} else {
			font = OutlineFont.readAndCreateStream(new FileInputStream(infontfile));
		}
		
		if (pathfile != null) {
			Element element = new Builder().build(pathfile).getRootElement();
			pathElement = SVGElement.readAndCreateSVG(element);
		}
		font.analyze(pathElement, stats);
		if (outfontfile != null) {
			CMLUtil.debug(font.fontRoot, new FileOutputStream(outfontfile), 2);
		}
		if (outglyphfile != null) {
			font.plotGlyphs(new FileOutputStream(outglyphfile));
		}
	}

	private void setName(String string) {
		// TODO Auto-generated method stub
		
	}

	private void setStats(boolean b) {
		// TODO Auto-generated method stub
		
	}

	private void setOutfontfile(String string) {
		// TODO Auto-generated method stub
		
	}

	private void setInfontfile(String string) {
		// TODO Auto-generated method stub
		
	}

	private void setOutglyphfile(String string) {
		// TODO Auto-generated method stub
		
	}

	private void setPathfile(String string) {
		// TODO Auto-generated method stub
		
	}

	private static void usage() {
		System.out.println(" -if <fontfile.xml> -c <corpus.xml> -of <outfont.xml> -og <glyphs.xml> -n <name> - gf <genfamily> -st");
		System.out.println("   corpus is SVG file for finding glyphs; og is glyph output; of is updated font;");
		System.out.println("   if no input font, use name and generic family to create new one");
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			usage();
		} else {
			try {
				runFont(args);
			} catch (Exception e) {
				throw new RuntimeException("Cannot run fonts", e);
			}
		}
	}

	public void analyzeGlyphStrokes() {
		Set<String> vectorStrings = new HashSet<String>();
		Multimap<String, Glyph> vectorMultimap = ArrayListMultimap.create();
		Multimap<String, Glyph> slopeMultimap = ArrayListMultimap.create();
		for (Glyph glyph : countByGlyphMap.keySet()) {
			Integer count = countByGlyphMap.get(glyph);
			if (count < 10) continue;
			System.out.println(glyph+"..."+count+"... "+glyph.getSignature());
			List<Vector2> vectors = glyph.getLineVectors();
			for (Vector2 lineVector : vectors) {
				// normalize to NE/SE quadrants
				if (lineVector.getX() < 0) {
					lineVector = new Vector2(lineVector.multiplyBy(-1.0));
				}
				Real2 slope = null;
				try {
					slope = lineVector.getUnitVector();
				} catch (Exception e) {
					slope = lineVector;
					// ignore zero-length vectors
				}
				String slopeS = slope.format(2).toString();
				String vs = new Real2(lineVector).format(2).toString();
				vectorMultimap.put(vs, glyph);
				slopeMultimap.put(slopeS, glyph);
				System.out.print(vs+"  ");
				vectorStrings.add(vs);
			}
			System.out.println();
		}
		System.out.println("vector");
		for (String vs : vectorMultimap.keySet() ) {
			System.out.println(vs+"   "+vectorMultimap.get(vs).size());
		}
		System.out.println("slope");
		for (String vs : slopeMultimap.keySet() ) {
			System.out.println(vs+"   "+slopeMultimap.get(vs).size());
		}
	}
}
