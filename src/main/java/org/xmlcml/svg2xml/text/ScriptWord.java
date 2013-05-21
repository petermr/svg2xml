package org.xmlcml.svg2xml.text;

import java.util.ArrayList;

import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.analyzer.TextAnalyzerX;

public class ScriptWord extends ScriptLine {

	public ScriptWord(int nLines) {
		super(new TextStructurer((TextAnalyzerX)null));
		textLineList = new ArrayList<TextLine>();
		for (int i = 0; i < nLines; i++) {
			textLineList.add(new TextLine());
		}
	}
	
	public void add(SVGText character, int line) {
		if (line >= 0 && line < textLineList.size()) {
			textLineList.get(line).add(character);
		}
	}
}
