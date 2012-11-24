package org.xmlcml.svgplus.tools;

import java.util.regex.Pattern;

import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svgplus.command.CurrentPage;
import org.xmlcml.svgplus.text.TextAnalyzer;

/**
 * structure is:
 * <g chunkStyle="CAPTION" ...>
 *     <text>... individual characters ...</text>
 *     <text>... individual characters ...</text>
 *     <text>... individual characters ...</text>
 *     <g name="para>
 *       <text ...>Figure 1 ...</text>
 *     </g>
 *  </g>
 *    
 *    style:
 *<style target="text" target="figureCaption" name="figureCaption" regex="Figure\s+\d+\s+[A-Z].*">
    <path d="M0.0 0.0 L74.653 0.0 L74.653 99.495 L0.0 99.495 L0.0 0.0 Z" />
    <font size="797" interline="9.9"/>
  </style>

 *    
 *    
 * @author pm286
 *
 */
public class Caption extends Chunk {
	
	public final static String CAPTION = "CAPTION";
	public enum CaptionType {
		APPENDIX,
		FIGURE,
		TABLE,
	}
	private String regex ;
	private SVGG gName;
	public SVGText svgText;
	private SVGText label; // e.g. figure or table
	private Pattern captionPattern;
	
	public Caption(CurrentPage currentPage) {
		super(currentPage);
	}

	public void setText(String text) {
		SVGText svgText = TextAnalyzer.getConcatenatedText(this);
		if (svgText != null) {
			svgText.setText(text);
		}
	}
	
	public Real2Range getBoundingBox() {
		this.createElementListAndCalculateBoundingBoxes();
		return super.getBoundingBox();

	}

	public void createElementListAndCalculateBoundingBoxes() {
		// TODO Auto-generated method stub
		
	}
}
