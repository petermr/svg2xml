package org.xmlcml.svg2xml.action;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;

/**
	<pageAction xpath="//svg:g[@id='chunk0.0.0']/svg:g/svg:g/svg:g[@name='para']/svg:text"
		title="pageMetadata" action="extract"
		regex="(.*)(BMC.*)(\d\d\d\d), (\d+):(\d+)\s*http\://www.biomedcentral.com/(\d+\-\d+)/(\d+)/(\d+)"
		variables="document.author document.journal document.year document.issue document.article document.doiSuffix document.issue1 document.article1" />
 * @author pm286
 *
 */
public class VariableExtractorActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(VariableExtractorActionX.class);
	
	public VariableExtractorActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	
	
	public final static String TAG ="variableExtractor";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(AbstractActionX.ACTION);
		ATTNAMES.add(PageActionX.PAGE_RANGE);
		ATTNAMES.add(AbstractActionX.REGEX);
		ATTNAMES.add(AbstractActionX.TITLE);
		ATTNAMES.add(AbstractActionX.VARIABLES);
		ATTNAMES.add(AbstractActionX.XPATH);
	}

	/** constructor
	 */
	public VariableExtractorActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new VariableExtractorActionX(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
				AbstractActionX.REGEX,
				AbstractActionX.VARIABLES,
				AbstractActionX.XPATH,
		});
	}
	
	@Override
	public void run() {
		extractVariables();
	}

	private void extractVariables() {
		String regex = this.getRegex();
		List<String> varsList = this.getVariables();
		String xpath = getXPath();
		Nodes nodes = getSVGPage().query(xpath, CMLConstants.SVG_XPATH);
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
					String name = varsList.get(i);
					semanticDocumentActionX.setVariable(name, val);
				}
			}
		}
	}


}
