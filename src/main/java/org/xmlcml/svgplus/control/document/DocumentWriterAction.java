package org.xmlcml.svgplus.control.document;


import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.util.MenuSystem;
import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.util.PConstants;

public class DocumentWriterAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentWriterAction.class);
	
	private File outdir;
	
	public DocumentWriterAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
	}
	
	@Override
	public void run() {
		String outdirName = getFilename();
		outdir = new File(outdirName);
		String regex = getRegex();
		String format = getFormat(PConstants.XML_FORMAT);
		if (PConstants.HTML_MENU_FORMAT.equals(format)) {
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
