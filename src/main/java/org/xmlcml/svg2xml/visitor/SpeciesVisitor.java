package org.xmlcml.svg2xml.visitor;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Nodes;
import nu.xom.Text;

import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlI;
import org.xmlcml.svg2xml.page.PageAnalyzer;



public class SpeciesVisitor extends AbstractVisitor {

	// ===================called on Visitables===================
	
	@Override
	public void visit(TextVisitable textVisitable) {
		searchPageForSpecies(textVisitable.getPageAnalyzer());
	}

	@Override
	public void visit(TableVisitable tableVisitable) {
	}

	@Override
	public void visit(SemanticVisitable figureVisitable) {
	}

	// =======================called by visitables===============
	
	// =========================methods=========================
	
	private void searchPageForSpecies(PageAnalyzer pageAnalyzer) {
		HtmlElement htmlElement = pageAnalyzer.getRunningHtmlElement();
		List<Text> italicList = getItalicTextContent(htmlElement);
	}

	private List<Text> getItalicTextContent(HtmlElement htmlElement) {
		List<Text> texts = makeTextList(htmlElement);
		texts = normalizeItalic(texts);
		return texts;
	}
	
	private List<Text> makeTextList(HtmlElement htmlElement) {
		Nodes nodes = htmlElement.query(".//text()");
		List<Text> textList = new ArrayList<Text>();
		for (int i = 0; i < nodes.size(); i++) {
			textList.add((Text)nodes.get(i));
		}
		return textList;
	}

	private List<Text> normalizeItalic(List<Text> texts) {
		for (int i = texts.size() - 1; i > 0; i++) {
			Text texti = texts.get(i);
			if (texti.getParent() instanceof HtmlI) {
				if (i >= 0) {
					i = mergePrecedingWhiteSpaceAndDecrement(texts.get(i - 1), i, texti);
				}
				if (i > 0) {
					i = mergeWithPrecedingTextAndDecrement(texts.get(i - 1), i, texti);
				}
			}
		}
		return texts;
	}

	private int mergeWithPrecedingTextAndDecrement(Text preceding, int textPointer, Text texti) {
		if (preceding.getParent() instanceof HtmlI) {
			String newValue = preceding.getValue() + texti.getValue();
			preceding.setValue(newValue);
			texti.detach();
			textPointer--;
		}
		return textPointer;
	}

	private int mergePrecedingWhiteSpaceAndDecrement(Text preceding, int textPointer, Text texti) {
		String value = preceding.getValue();
		// whitespace? merge with this and delete
		if (value.trim().length() == 0) {
			String newValue = (new StringBuilder(value).append(texti.getValue())).toString();
			texti.setValue(newValue);
			preceding.detach();
			textPointer--;
		}
		return textPointer;
	}


	private List<HtmlElement> dehyphenate(List<HtmlElement> htmlElements) {
		for (int i = htmlElements.size() - 1; i > 0; i--) {
			
		}
		return  null;
	}

	private List<HtmlElement> mergeOrContractSpaces(List<HtmlElement> htmlElements) {
		// TODO Auto-generated method stub
		return null;
	}

}
