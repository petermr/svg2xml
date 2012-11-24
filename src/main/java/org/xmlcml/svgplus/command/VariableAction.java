package org.xmlcml.svgplus.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class VariableAction extends PageAction {

	private final static Logger LOG = Logger.getLogger(VariableAction.class);
	
	public static final Pattern NAME_PATTERN = Pattern.compile("(s|p|d)\\.[a-zA-Z][a-zA-Z0-9_]*");
	
	public VariableAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		String name = getName();
		String value = getValue();
		if (name == null || value == null) {
			throw new RuntimeException("must give name and value attributes: "+getActionElement().toXML());
		}
		checkValidName(name);
		LOG.trace("SD "+semanticDocumentAction);
		semanticDocumentAction.setVariable(name, value);
//		log(getLog());
	}

	private void checkValidName(String name) {
		Matcher matcher = NAME_PATTERN.matcher(name);
		if (!matcher.matches()) {
			throw new RuntimeException("Bad name ("+name+") must match: "+NAME_PATTERN);
		}
	}

}
