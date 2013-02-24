package org.xmlcml.svg2xml.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Node;

import org.apache.log4j.Logger;

public class VariableActionX extends PageActionX {

	private final static Logger LOG = Logger.getLogger(VariableActionX.class);
	
	public static final Pattern NAME_PATTERN = Pattern.compile("(s|p|d)\\.[a-zA-Z][a-zA-Z0-9_]*");
	
	public VariableActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	

	public final static String TAG ="variable";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();

	private static final String FILE = "file";
	
	static {
		ATTNAMES.add(AbstractActionX.LOGAT);
		ATTNAMES.add(AbstractActionX.NAME);
		ATTNAMES.add(AbstractActionX.TYPE);
		ATTNAMES.add(PageActionX.VALUE);
	}

	/** constructor
	 */
	public VariableActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new VariableActionX(this);
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
				AbstractActionX.NAME,
				PageActionX.VALUE,
		});
	}
	
	@Override
	public void run() {
		String name = getName();
		String value = getValueString();
		Object objectValue = value;
		String type = getType();
		if (name == null || value == null) {
			throw new RuntimeException("must give name and value attributes: "+this.toXML());
		}
		checkValidName(name);
		if (FILE.equalsIgnoreCase(type)) {
			objectValue = new File(value);
		}
		LOG.trace("SD "+semanticDocumentActionX);
		semanticDocumentActionX.setVariable(name, objectValue);
//		log(getLog());
	}

	private void checkValidName(String name) {
		Matcher matcher = NAME_PATTERN.matcher(name);
		if (!matcher.matches()) {
			throw new RuntimeException("Bad name ("+name+") must match: "+NAME_PATTERN);
		}
	}

}
