package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.page.TextAnalyzerOLD;

/** 
 * A word in a ScriptLine
 * 
 * @author pm286
 */
public class ScriptWordOLD extends ScriptLineOLD {
	
	private List<String> characterList;

	public ScriptWordOLD(int nLines) {
		super(new TextStructurerOLD((TextAnalyzerOLD) null));
		textLineList = new ArrayList<TextLineOLD>();
		for (int i = 0; i < nLines; i++) {
			textLineList.add(new TextLineOLD());
		}
		textStructurer.setTextLines(textLineList);
		textStructurer.setTextCharacters(new ArrayList<SVGText>());
	}
	
	public void add(SVGText character, int line) {
		ensureCharacterList();
		if (line >= 0 && line < textLineList.size()) {
			textLineList.get(line).add(character);
		}
		characterList.add(character.getText());
		textStructurer.getTextList().add(character);
	}
	
	private void ensureCharacterList() {
		if (characterList == null) {
			characterList = new ArrayList<String>();
		}
	}

	@Override
	public String summaryString() {
		StringBuilder sb = new StringBuilder();
		ensureCharacterList();
		for (String s : characterList) {
			sb.append(s);
		}
		return sb.toString();
	}
}
