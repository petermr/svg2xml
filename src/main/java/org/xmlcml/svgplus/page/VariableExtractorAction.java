package org.xmlcml.svgplus.page;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.svgplus.core.AbstractActionElement;

/**
	<pageAction xpath="//svg:g[@id='chunk0.0.0']/svg:g/svg:g/svg:g[@name='para']/svg:text"
		title="pageMetadata" action="extract"
		regex="(.*)(BMC.*)(\d\d\d\d), (\d+):(\d+)\s*http\://www.biomedcentral.com/(\d+\-\d+)/(\d+)/(\d+)"
		variables="document.author document.journal document.year document.issue document.article document.doiSuffix document.issue1 document.article1" />
 * @author pm286
 *
 */
public class VariableExtractorAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(VariableExtractorAction.class);
	public VariableExtractorAction(AbstractActionElement pageActionCommand) {
		super(pageActionCommand);
	}
	
	@Override
	public void run() {
		extractVariables();
	}

	private void extractVariables() {
		String regex = this.getRegex();
		List<String> varsList = this.getVariables();
		String xpath = getXPath();
		Nodes nodes = getSVGPageCopy().query(xpath, CMLConstants.SVG_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			extractValueFromNode(regex, varsList, nodes.get(i));
		}
	}

	private void extractValueFromNode(String regex, List<String> varsList, Node node) {
		String value = node.getValue();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches()) {
			if (matcher.groupCount() == varsList.size()) {
				for (int i = 0; i < varsList.size(); i++) {
					String val = matcher.group(i+1);
					pageAnalyzer.putValue(varsList.get(i), val);
				}
			}
		}
	}


}
