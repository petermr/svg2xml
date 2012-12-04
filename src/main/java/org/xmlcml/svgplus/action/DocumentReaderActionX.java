package org.xmlcml.svgplus.action;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;

public class DocumentReaderActionX extends DocumentActionX {

	private final static Logger LOG = Logger.getLogger(DocumentReaderActionX.class);
	
	public DocumentReaderActionX(AbstractActionX actionElement) {
		super(actionElement);
	}

	public final static String TAG ="documentReader";

	private static final List<String> ATTNAMES = new ArrayList<String>();

	static {
		ATTNAMES.add(AbstractActionX.FILENAME);
		ATTNAMES.add(AbstractActionX.FORMAT);
	}

	/** constructor
	 */
	public DocumentReaderActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentReaderActionX(this);
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
//				AbstractActionElement.FILENAME,
		});
	}
	
	@Override
	public void run() {
		LOG.trace("executing: \n"+this.getString());
		String filename = getFilename();
		LOG.trace("reading file: "+filename);
		LOG.trace(this.getString());
		File file = new File(filename);
		String skip = getSkip();
		if (!file.exists() || !file.isDirectory()) {
			throw new RuntimeException("file does not exist or is not a directory: "+file.getAbsolutePath());
		}
		
	}

}
