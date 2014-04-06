package org.xmlcml.svg2xml.font.old;

import java.awt.Font;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

/** a simple font with character width
 * 
 * @author pm286
 *
 */
public class SimpleFont  {

	

	public enum FontStyle {
		BOLD_ITALIC,
		BOLD,
		ITALIC,
		NORMAL,
	}
	
	public final static String TAG = "simpleFont";
	public final static String FONT_DIR = "src/main/resources/org/xmlcml/graphics/font/";
	public static SimpleFont SIMPLE_FONT = null;
	static {
		try {
			SIMPLE_FONT = new SimpleFont(new FileInputStream(FONT_DIR+"simpleFont.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/parse font", e);
		}
	}

	private static final String NAME = "name";
	private static final String STYLE = "style";
	
	private String name;
	private FontStyle style = FontStyle.NORMAL;
	private Map<String, SimpleCharacter> characterMap;
	
	public SimpleFont(String name, String style) {
		this.name = name;
		this.setStyle(style);
		this.characterMap = new HashMap<String, SimpleCharacter>();
	}

	public SimpleFont(InputStream inputStream) {
		try {
			Element root = new Builder().build(inputStream).getRootElement();
			name = root.getAttributeValue(NAME);
			this.characterMap = new HashMap<String, SimpleCharacter>();
			Elements childElements = root.getChildElements();
			for (int i = 0; i < childElements.size(); i++) {
				SimpleCharacter simpleCharacter = new SimpleCharacter(childElements.get(i));
				this.addCharacter(simpleCharacter);
			}
		} catch (Exception e) {
			throw new RuntimeException("cannot read/parse font", e);
		}
	}

	private void setStyle(String styleS) {
		FontStyle style = FontStyle.valueOf(styleS);
		if (style != null) {
			this.style = style;
		}
	}
	
	public void addCharacter(SimpleCharacter simpleCharacter) {
		if (simpleCharacter == null) {
			throw new RuntimeException("cannot add null character: ");
		}
		String charr = simpleCharacter.getCharacter();
		if (characterMap.get(charr) != null) {
			throw new RuntimeException("adding duplicate character: "+charr+"; may need to remove first");
		}
		characterMap.put(charr, simpleCharacter);
	}

	/**
	 * removes character with value charr
	 * @param charr
	 * @return null if character not found
	 */
	public SimpleCharacter removeCharacter(String charr) {
		SimpleCharacter simpleCharacter = null;
		if (charr != null) {
			simpleCharacter = characterMap.remove(charr);
		}
		return simpleCharacter;
	}
	
	public Element toXML() {
		Element font = new Element(TAG);
		if (name != null) {
			font.addAttribute(new Attribute(NAME, name));
		}
		if (style != null) {
			font.addAttribute(new Attribute(STYLE, style.toString()));
		}
		String[] cc = characterMap.keySet().toArray(new String[characterMap.size()]);
		Arrays.sort(cc);
		for (String c : cc) {
			SimpleCharacter sc = characterMap.get(c);
			font.appendChild(sc.toXML().copy());
		}
		return font;
	}

	public SimpleCharacter getSimpleCharacter(String ch) {
		if (characterMap == null) {
			throw new RuntimeException("null characterMap");
		}
		return characterMap.get(ch);
	}

	/** guess amount of whitespace constituting 2-3 spaces
	 * very crude
	 * @param fontSize
	 * @return
	 */
	public Double guessMinimumInterTextSpacing(Double fontSize) {
		return (fontSize == null) ? 20 : fontSize*2;
	}

	/** guess amount of whitrespace constituting a space
	 * very crude
	 * @param fontSize
	 * @return
	 */
	public Double guessMinimumSpaceSize(Double fontSize) {
		return (fontSize == null) ? 10 : fontSize*0.5;
	}
	
	public static Map<String, Double> getWidthsByCharacter() {
		return getWidthsByCharacter(null);
	}
	public static Map<String, Double> getWidthsByCharacter(String family) {
/*
 * Arial
Arial Black
Arial Bold
Arial Bold Italic
Arial Italic
Arial Narrow
Arial Narrow Bold
Arial Narrow Bold Italic
Arial Narrow Italic
Arial Rounded MT Bold
Arial Unicode MS
Monospaced.bold
Monospaced.bolditalic
Monospaced.italic
Monospaced.plain
SansSerif.bold
SansSerif.bolditalic
SansSerif.italic
SansSerif.plain
Times New Roman
Times New Roman Bold
Times New Roman Bold Italic
Times New Roman Italic
		
 */
		if (family == null) {
			family = "SansSerif";
		}
		Font simpleFont = new java.awt.Font(family, Font.PLAIN, 100);

//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
//        Font[] allFonts = ge.getAllFonts();  
        Toolkit toolkit =  Toolkit.getDefaultToolkit();  
        FontMetrics fontMetrics = toolkit.getFontMetrics(simpleFont.deriveFont(100f));  
        Map<String, Double> widthsByCharMap = new HashMap<String, Double>();
        for (char c = 32; c < 128; c++) {
        	widthsByCharMap.put(""+c, (double) fontMetrics.charWidth((char)c) / 100.);
        }
        return widthsByCharMap;
    }  
	
	static Set<String> fontSet = new HashSet<String>();
	static {
		String[] fonts = {
 "Agency FB", "Agency FB Bold", "Aharoni Bold", "Algerian",
 "Andalus", "Angsana New", "Angsana New Bold", "Angsana New Bold Italic",
 "Angsana New Italic", "AngsanaUPC", "AngsanaUPC Bold", "AngsanaUPC Bold Italic",
 "AngsanaUPC Italic", "Aparajita", "Aparajita Bold", "Aparajita Bold Italic",
 "Aparajita Italic", "Arabic Typesetting", "Arial", "Arial Black",
 "Arial Bold", "Arial Bold Italic", "Arial Italic", "Arial Narrow",
 "Arial Narrow Bold", "Arial Narrow Bold Italic", "Arial Narrow Italic", "Arial Rounded MT Bold",
 "Arial Unicode MS", "Baskerville Old Face", "Batang", "BatangChe",
 "Bauhaus 93", "Bell MT", "Bell MT Bold", "Bell MT Italic",
 "Berlin Sans FB", "Berlin Sans FB Bold", "Berlin Sans FB Demi Bold", "Bernard MT Condensed",
 "Blackadder ITC", "Bodoni MT", "Bodoni MT Black", "Bodoni MT Black Italic",
 "Bodoni MT Bold", "Bodoni MT Bold Italic", "Bodoni MT Condensed", "Bodoni MT Condensed Bold",
 "Bodoni MT Condensed Bold Italic", "Bodoni MT Condensed Italic", "Bodoni MT Italic", "Bodoni MT Poster Compressed",
 "Book Antiqua", "Book Antiqua Bold", "Book Antiqua Bold Italic", "Book Antiqua Italic",
 "Bookman Old Style", "Bookman Old Style Bold", "Bookman Old Style Bold Italic", "Bookman Old Style Italic",
 "Bookshelf Symbol 7", "Bradley Hand ITC", "Britannic Bold", "Broadway",
 "Browallia New", "Browallia New Bold", "Browallia New Bold Italic", "Browallia New Italic",
 "BrowalliaUPC", "BrowalliaUPC Bold", "BrowalliaUPC Bold Italic", "BrowalliaUPC Italic",
 "Brush Script MT Italic", "Calibri", "Calibri Bold", "Calibri Bold Italic",
 "Calibri Italic", "Californian FB", "Californian FB Bold", "Californian FB Italic",
 "Calisto MT", "Calisto MT Bold", "Calisto MT Bold Italic", "Calisto MT Italic",
 "Cambria", "Cambria Bold", "Cambria Bold Italic", "Cambria Italic",
 "Cambria Math", "Candara", "Candara Bold", "Candara Bold Italic",
 "Candara Italic", "Castellar", "Centaur", "Century",
 "Century Gothic", "Century Gothic Bold", "Century Gothic Bold Italic", "Century Gothic Italic",
 "Century Schoolbook", "Century Schoolbook Bold", "Century Schoolbook Bold Italic",
 "Century Schoolbook Italic", "Chiller", "Colonna MT", "Comic Sans MS",
 "Comic Sans MS Bold", "Consolas", "Consolas Bold", "Consolas Bold Italic",
 "Consolas Italic", "Constantia", "Constantia Bold", "Constantia Bold Italic",
 "Constantia Italic", "Cooper Black", "Copperplate Gothic Bold", "Copperplate Gothic Light",
 "Corbel", "Corbel Bold", "Corbel Bold Italic", "Corbel Italic",
 "Cordia New", "Cordia New Bold", "Cordia New Bold Italic", "Cordia New Italic",
 "CordiaUPC", "CordiaUPC Bold", "CordiaUPC Bold Italic", "CordiaUPC Italic",
 "Courier New", "Courier New Bold", "Courier New Bold Italic", "Courier New Italic",
 "Curlz MT", "DFKai-SB", "DaunPenh", "David",
 "David Bold", "Dialog.bold", "Dialog.bolditalic", "Dialog.italic",
 "Dialog.plain", "DialogInput.bold", "DialogInput.bolditalic",
 "DialogInput.italic", "DialogInput.plain", "DilleniaUPC", "DilleniaUPC Bold",
 "DilleniaUPC Bold Italic", "DilleniaUPC Italic",
 "DokChampa", "Dotum",
 "DotumChe", "Ebrima",
 "Ebrima Bold", "Edwardian Script ITC",
 "Elephant", "Elephant Italic",
 "Engravers MT", "Eras Bold ITC",
 "Eras Demi ITC", "Eras Light ITC",
 "Eras Medium ITC", "Estrangelo Edessa",
 "EucrosiaUPC", "EucrosiaUPC Bold",
 "EucrosiaUPC Bold Italic", "EucrosiaUPC Italic",
 "Euphemia", "FangSong",
 "Felix Titling", "Footlight MT Light",
 "Forte", "FrankRuehl",
 "Franklin Gothic Book", "Franklin Gothic Book Italic",
 "Franklin Gothic Demi", "Franklin Gothic Demi Cond",
 "Franklin Gothic Demi Italic", "Franklin Gothic Heavy",
 "Franklin Gothic Heavy Italic", "Franklin Gothic Medium",
 "Franklin Gothic Medium Cond", "Franklin Gothic Medium Italic",
 "FreesiaUPC", "FreesiaUPC Bold",
 "FreesiaUPC Bold Italic", "FreesiaUPC Italic",
 "Freestyle Script", "French Script MT",
 "Gabriola", "Garamond",
 "Garamond Bold", "Garamond Italic",
 "Gautami", "Gautami Bold",
 "Georgia", "Georgia Bold",
 "Georgia Bold Italic", "Georgia Italic",
 "Gigi", "Gill Sans MT",
 "Gill Sans MT Bold", "Gill Sans MT Bold Italic", "Gill Sans MT Condensed",
 "Gill Sans MT Ext Condensed Bold",
 "Gill Sans MT Italic", "Gill Sans Ultra Bold",
 "Gill Sans Ultra Bold Condensed", "Gisha",
 "Gisha Bold", "Gloucester MT Extra Condensed",
 "Goudy Old Style", "Goudy Old Style Bold",
 "Goudy Old Style Italic", "Goudy Stout",
 "Gulim", "GulimChe",
 "Gungsuh", "GungsuhChe",
 "Haettenschweiler", "Harlow Solid Italic",
 "Harrington", "High Tower Text",
 "High Tower Text Italic", "Impact",
 "Imprint MT Shadow", "Informal Roman",
 "IrisUPC", "IrisUPC Bold",
 "IrisUPC Bold Italic", "IrisUPC Italic",
 "Iskoola Pota", "IskoolaPota-Bold",
 "JasmineUPC", "JasmineUPC Bold",
 "JasmineUPC Bold Italic", "JasmineUPC Italic",
 "Jokerman", "Juice ITC",
 "KaiTi", "Kalinga",
 "Kalinga Bold", "Kartika",
 "Kartika Bold", "Khmer UI",
 "Khmer UI Bold", "KodchiangUPC",
 "KodchiangUPC Bold", "KodchiangUPC Bold Italic",
 "KodchiangUPC Italic", "Kokila",
 "Kokila Bold", "Kokila Bold Italic",
 "Kokila Italic", "Kristen ITC",
 "Kunstler Script", "Lao UI",
 "Lao UI Bold", "Latha",
 "Latha Bold", "Leelawadee",
 "Leelawadee Bold", "Levenim MT",
 "Levenim MT Bold", "LilyUPC",
 "LilyUPC Bold", "LilyUPC Bold Italic",
 "LilyUPC Italic", "Lucida Bright Regular",
 "Lucida Bright Demibold", "Lucida Bright Demibold Italic",
 "Lucida Bright Italic", "Lucida Bright Regular",
 "Lucida Calligraphy Italic", "Lucida Console",
 "Lucida Fax Demibold", "Lucida Fax Demibold Italic",
 "Lucida Fax Italic", "Lucida Fax Regular",
 "Lucida Handwriting Italic", "Lucida Sans Demibold",
 "Lucida Sans Demibold", "Lucida Sans Demibold Roman",
 "Lucida Sans Italic", "Lucida Sans Regular",
 "Lucida Sans Typewriter Bold", "Lucida Sans Typewriter Bold",
 "Lucida Sans Typewriter Oblique", "Lucida Sans Typewriter Regular",
 "Lucida Sans Unicode", "MS Gothic",
 "MS Mincho", "MS Outlook",
 "MS PGothic", "MS PMincho",
 "MS Reference Sans Serif", "MS Reference Specialty",
 "MS UI Gothic", "MT Extra",
 "MV Boli", "Magneto Bold",
 "Maiandra GD", "Malgun Gothic",
 "Malgun Gothic Bold", "Mangal",
 "Mangal Bold", "Marlett",
 "Matura MT Script Capitals", "Meiryo",
 "Meiryo Bold", "Meiryo Bold Italic",
 "Meiryo Italic", "Meiryo UI",
 "Meiryo UI Bold", "Meiryo UI Bold Italic",
 "Meiryo UI Italic", "Microsoft Himalaya",
 "Microsoft JhengHei", "Microsoft JhengHei Bold",
 "Microsoft New Tai Lue", "Microsoft New Tai Lue Bold",
 "Microsoft PhagsPa", "Microsoft PhagsPa Bold",
 "Microsoft Sans Serif", "Microsoft Tai Le",
 "Microsoft Tai Le Bold", "Microsoft Uighur",
 "Microsoft YaHei", "Microsoft YaHei Bold",
 "Microsoft Yi Baiti", "MingLiU",
 "MingLiU-ExtB", "MingLiU_HKSCS",
 "MingLiU_HKSCS-ExtB", "Miriam",
 "Miriam Fixed", "Mistral",
 "Modern No. 20", "Mongolian Baiti",
 "Monospaced.bold", "Monospaced.bolditalic",
 "Monospaced.italic", "Monospaced.plain",
 "Monotype Corsiva", "MoolBoran",
 "NSimSun", "Narkisim",
 "Niagara Engraved", "Niagara Solid",
 "Nyala", "OCR A Extended",
 "Old English Text MT", "Onyx",
 "PMingLiU", "PMingLiU-ExtB",
 "Palace Script MT", "Palatino Linotype",
 "Palatino Linotype Bold", "Palatino Linotype Bold Italic",
 "Palatino Linotype Italic", "Papyrus",
 "Parchment", "Perpetua",
 "Perpetua Bold", "Perpetua Bold Italic",
 "Perpetua Italic", "Perpetua Titling MT Bold",
 "Perpetua Titling MT Light", "Plantagenet Cherokee",
 "Playbill", "Poor Richard",
 "Pristina", "Raavi",
 "Raavi Bold", "Rage Italic",
 "Ravie", "Rockwell",
 "Rockwell Bold", "Rockwell Bold Italic",
 "Rockwell Condensed", "Rockwell Condensed Bold",
 "Rockwell Extra Bold", "Rockwell Italic",
 "Rod", "SWGamekeys MT",
 "SWMacro", "Sakkal Majalla",
 "Sakkal Majalla Bold", "SansSerif.bold",
 "SansSerif.bolditalic", "SansSerif.italic",
 "SansSerif.plain", "Script MT Bold",
 "Segoe Print", "Segoe Print Bold",
 "Segoe Script", "Segoe Script Bold",
 "Segoe UI", "Segoe UI Bold",
 "Segoe UI Bold Italic", "Segoe UI Italic",
 "Segoe UI Light", "Segoe UI Semibold",
 "Segoe UI Symbol", "Serif.bold",
 "Serif.bolditalic", "Serif.italic",
 "Serif.plain", "Shonar Bangla",
 "Shonar Bangla Bold", "Showcard Gothic",
 "Shruti", "Shruti Bold",
 "SimHei", "SimSun",
 "SimSun-ExtB", "Simplified Arabic",
 "Simplified Arabic Bold", "Simplified Arabic Fixed",
 "Snap ITC", "Stencil",
 "Sylfaen", "Symbol",
 "Tahoma", "Tahoma Bold",
 "Tempus Sans ITC", "Times New Roman",
 "Times New Roman Bold", "Times New Roman Bold Italic",
 "Times New Roman Italic", "Traditional Arabic",
 "Traditional Arabic Bold", "Trebuchet MS",
 "Trebuchet MS Bold", "Trebuchet MS Bold Italic",
 "Trebuchet MS Italic", "Tunga",
 "Tunga Bold", "Tw Cen MT",
 "Tw Cen MT Bold", "Tw Cen MT Bold Italic",
 "Tw Cen MT Condensed", "Tw Cen MT Condensed Bold",
 "Tw Cen MT Condensed Extra Bold", "Tw Cen MT Italic",
 "Utsaah", "Utsaah Bold",
 "Utsaah Bold Italic", "Utsaah Italic",
 "Vani", "Vani Bold",
 "Verdana", "Verdana Bold",
 "Verdana Bold Italic", "Verdana Italic",
 "Vijaya", "Vijaya Bold",
 "Viner Hand ITC", "Vivaldi Italic",
 "Vladimir Script", "Vrinda",
 "Vrinda Bold", "Webdings",
 "Wide Latin", "Wingdings",
 "Wingdings 2", "Wingdings 3",
		};
		for (String font : fonts) {
			fontSet.add(font);
		}
	};

//	public Element createElement() {
//		Element 
//	}
}
