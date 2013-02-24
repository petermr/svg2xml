package org.xmlcml.svg2xml.text;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Real;
import org.xmlcml.graphics.svg.SVGTSpan;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svgplus.analyzer.SubSupAnalyzerX;
import org.xmlcml.svgplus.analyzer.SubSupAnalyzerX.SubSup;

public class TypedNumber {

	private final static Logger LOG = Logger.getLogger(TypedNumber.class);
	
	Number number = null;
	private List<Number> numberList = null;
	private String dataType = null;

	/** create either from the text value of Child TSpans
	 * 
	 * @param text
	 */
	TypedNumber(SVGText text) {
		if (text.getChildElements().size() == 0) {
			createFromString(text.getValue().trim());
		} else {
			TypedNumber typedNumber = createFromText(text);
			if (typedNumber != null) {
				this.number = typedNumber.number;
				this.dataType = typedNumber.dataType;
			}
		}
	}

	public TypedNumber(Double dubble) {
		this.number = dubble;
		dataType = CMLConstants.XSD_DOUBLE;
	}

	public TypedNumber(Double abscissa, Integer power) {
		Double exponentiated  =Math.pow(10.0, (double) power);
		this.number = abscissa * exponentiated;
		dataType = CMLConstants.XSD_DOUBLE;
	}

	private void createFromString(String value) {
		createInteger(value);
		createDouble(value);
	}

	/** create from SVGText 
	 * may have textString value
	 * 1 TSpan with value
	 * 2 tSpans with exponential SUPERSCRIPT
	 * @param text
	 * @return
	 */
	public static TypedNumber createFromText(SVGText text) {
		TypedNumber typedNumber = null;
		List<SVGTSpan> tSpans = text.getChildTSpans();
		if (tSpans.size() == 0) {
			typedNumber = new TypedNumber(text); 
		} else if (tSpans.size() == 1) {
			typedNumber = TypedNumber.createNumber(tSpans.get(0)); 
		} else if (tSpans.size() == 2) {
			typedNumber = interpretExponentialNotation(tSpans);
		}
		return typedNumber;
	}

	/** requires a list of exactly 2 
	 * 
	 * @param tSpans
	 * @return
	 */
	public static TypedNumber interpretExponentialNotation(List<SVGTSpan> tSpans) {
		TypedNumber typedNumber = null;
		// of form 1.2x10<sup>34</sup>
		if (tSpans.size() == 2) {
			SVGTSpan tSpan0 = tSpans.get(0);
			SVGTSpan tSpan1 = tSpans.get(1);
			Integer power = null;
			if (SubSup.SUPERSCRIPT.toString().equals(tSpan1.getAttributeValue(SubSupAnalyzerX.SCRIPT_TYPE))) {
				try {
					power = new Integer(tSpan1.getValue());
					typedNumber = createAndParseExponentialForm(tSpan0.getValue().trim(), power);
				} catch (Exception e) {
				}
			}
		}
		return typedNumber;
	}

	public static TypedNumber createAndParseExponentialForm(String abscissaText, Integer power) {
		TypedNumber typedNumber = null;
		if (abscissaText.endsWith("10")) {
			abscissaText = abscissaText.substring(0,  abscissaText.length()-2);
			if (abscissaText.length() == 0) {
				abscissaText = abscissaText + "1.0";
			} else {
				// deal with multiplier (times) character
				if (abscissaText.endsWith("x") || abscissaText.endsWith("X")) {
					abscissaText = abscissaText.substring(0,  abscissaText.length()-1);
				}
			}
			abscissaText = abscissaText + "E";
			if (power >= 0) {
				abscissaText = abscissaText + "+";
			}
			abscissaText = abscissaText + power;
			LOG.trace("SUPERSCRIPTED NUMBER "+abscissaText);
			double dd = Real.parseDouble(abscissaText);
			if (!Double.isNaN(dd)) {                  
				typedNumber = new TypedNumber(new Double(dd));
			}
		}
		return typedNumber;
	}
	
	public static TypedNumber createNumber(SVGText text) {
		TypedNumber number = new TypedNumber(text);
		return number.number == null ? null : number;
	}

	public Number getNumber() {
		return number;
	}

	public String getDataType() {
		return dataType;
	}

	private void createDouble(String value) {
		if (number == null) {
			try {
				Double dubble = new Double(value);
				number = dubble;
				dataType = CMLConstants.XSD_DOUBLE;
			} catch (Exception e1) {
			}
		}
	}

	private void createInteger(String value) {
		try {
			Integer integer = new Integer(value);
			number = integer;
			dataType = CMLConstants.XSD_INTEGER;
		} catch (Exception e) {
		}
	}

	void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void convertToDouble() {
		if (!(number instanceof Double) || !CMLConstants.XSD_DOUBLE.equals(dataType)) {
			number = new Double((Integer)number);
			dataType = CMLConstants.XSD_DOUBLE;
		}
	}

}
