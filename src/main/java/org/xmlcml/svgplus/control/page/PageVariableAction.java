package org.xmlcml.svgplus.control.page;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractActionElement;

public class PageVariableAction extends PageAction {

	private final static Logger LOGGER = Logger.getLogger(PageVariableAction.class);
	
	public static final Pattern NAME_PATTERN = Pattern.compile("(p|d)\\.[a-zA-Z][a-zA-Z0-9_]*");
	
	public PageVariableAction(AbstractActionElement pageActionCommand) {
		super(pageActionCommand);
	}
	
	@Override
	public void run() {
		String name = getName();
		String value = getValue();
		if (name == null || value == null) {
			throw new RuntimeException("must give name and value attributes: "+getActionCommandElement().toXML());
		}
		checkValidName(name);
		pageAnalyzer.putValue(name, value);
//		log(getLog());
	}

	private void checkValidName(String name) {
		Matcher matcher = NAME_PATTERN.matcher(name);
		if (!matcher.matches()) {
			throw new RuntimeException("Bad name ("+name+") must match: "+NAME_PATTERN);
		}
	}

}
