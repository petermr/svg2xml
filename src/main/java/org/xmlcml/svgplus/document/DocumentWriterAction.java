package org.xmlcml.svgplus.document;


import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.xmlcml.pdf2svg.util.MenuSystem;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.core.SVGPlusConstants;
import org.xmlcml.svgplus.page.tools.RegexFilenameFilter;

public class DocumentWriterAction extends DocumentAction {

	private final static Logger LOG = Logger.getLogger(DocumentWriterAction.class);
	
	private File outdir;
	
	public DocumentWriterAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	@Override
	public void run() {
		String outdirName = getFilename();
		outdir = new File(outdirName);
		String regex = getRegex();
		String format = getFormat(SVGPlusConstants.XML_FORMAT);
		if (SVGPlusConstants.HTML_MENU_FORMAT.equals(format)) {
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
