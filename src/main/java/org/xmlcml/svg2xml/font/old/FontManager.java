package org.xmlcml.svg2xml.font.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;

/** manages the fonts available for parsing the PDF
 * 
 * @author pm286
 *
 */
public class FontManager {
	private static final String FONT_LIST_RESOURCES = "fontListResources.txt";

	public final static String packageName = FontManager.class.getPackage().getName();
	public final static String packageBase = packageName.replace(".", "/");
	
	List<OutlineFont> fontList = new ArrayList<OutlineFont>();

	private Map<String, OutlineFont> fontByNameMap = new HashMap<String, OutlineFont>();
	
	public static FontManager DEFAULT = new FontManager();
	static {
		try {
			DEFAULT.readFonts(packageBase+"/"+FONT_LIST_RESOURCES);
		} catch (Exception e) {
			throw new RuntimeException("cannot create default fonts", e);
		}
	};

	
	public FontManager() {
	}
	
	public FontManager(String resourceFile) {
		try {
			readFonts(resourceFile);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read font file: "+resourceFile);
		}
	}
	
	public void readFonts(String resourceFile) throws IOException {
		fontList = new ArrayList<OutlineFont>();
		InputStream inputStream = FontManager.class.getClassLoader().getResourceAsStream(resourceFile);
		if (inputStream == null) {
			throw new RuntimeException("Cannot load/find fontfile: "+resourceFile);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		while (true) {
			String fontResource = null;
			try {
				fontResource = reader.readLine();
				if (fontResource == null) break;
				fontResource = fontResource.trim();
				if (fontResource.length() != 0) {
					OutlineFont outlineFont = OutlineFont.readAndCreateFont(packageBase+"/"+fontResource);
					if (outlineFont != null) {
						fontList.add(outlineFont);
						register(outlineFont);
					} else {
						throw new RuntimeException("Cannot create font: "+fontResource);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot read/parse "+fontResource, e);
			}
		}
	}
	public List<OutlineFont> getLocalFonts() {
		return fontList;
	}
	
	public OutlineFont getFontForName(String fontName) {
		return fontByNameMap.get(fontName);
	}
	
	public void register(OutlineFont font) {
		String fontName = font.getFontName();
		if (fontByNameMap.get(fontName) != null) {
			throw new RuntimeException("font already registered: "+fontName);
		}
		fontByNameMap.put(fontName, font);
	}
	
	public OutlineFont getDefaultFont() {
		List<OutlineFont> fontList = getLocalFonts();
		return fontList.size() == 0 ? null : fontList.get(0);
	}

	public SVGText createCharacter(SVGPath path) {
		SVGText text = null;
		String sig = path.getSignature();
		Glyph glyph = getGlyph(sig);
		if (glyph != null) {
			text = new SVGText(path.getOrigin(), glyph.getCharacter());
		}
		return text;
	}
	
	public Glyph getGlyph(String signature) {
		Glyph glyph = null;
		for (OutlineFont font : fontList) {
			glyph = font.getGlyphBySig(signature);
			if (glyph != null) {
				break;
			}
		}
		return glyph;
	}
}
