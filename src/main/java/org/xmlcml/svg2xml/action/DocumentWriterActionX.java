package org.xmlcml.svg2xml.action;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.pdf2svg.util.MenuSystem;
import org.xmlcml.svg2xml.tools.RegexFilenameFilter;

public class DocumentWriterActionX extends DocumentActionX {

	private final static Logger LOG = Logger.getLogger(DocumentWriterActionX.class);
	
	private File outdir;
	
	public DocumentWriterActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	

	public final static String TAG ="documentWriter";

	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
//		ATTNAMES.add(PageActionX.ACTION);
		ATTNAMES.add(AbstractActionX.FILENAME);
		ATTNAMES.add(AbstractActionX.FORMAT);
		ATTNAMES.add(AbstractActionX.REGEX);
		ATTNAMES.add(AbstractActionX.XPATH);
	}

	/** constructor
	 */
	public DocumentWriterActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentWriterActionX(this);
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
				AbstractActionX.FILENAME,
		});
	}
	@Override
	public void run() {
		String outdirName = getFilename();
		outdir = new File(outdirName);
		String regex = getRegex();
		String format = getFormat(SVGPlusConstantsX.XML_FORMAT);
		if (SVGPlusConstantsX.HTML_MENU_FORMAT.equals(format)) {
			createHtmlMenu(regex);
		}
	}

	private void createHtmlMenu(String regex) {
		File[] files = null;
		if (regex == null) {
			files = outdir.listFiles();
		} else {
			files = outdir.listFiles(new RegexFilenameFilter(regex)) ;
		}
		if (files != null) {
			MenuSystem menuSystem = new MenuSystem(outdir);
			menuSystem.setRowWidth(180);
			menuSystem.setAddPdf(true);
			menuSystem.writeDisplayFiles(Arrays.asList(files), "_art");
		}
	}

}
