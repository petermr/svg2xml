package org.xmlcml.svgplus.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.core.AbstractAction;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.core.DocumentAnalyzer;

public class DocumentActionFactory {
	private final static Logger LOG = Logger.getLogger(DocumentActionFactory.class);

	/** action values
	 * 
	 */
	private static final String DEBUG = "debug";
	private static final String READ_PAGES = "readPages";
	private static final String RUN_PAGE_COMMANDS = "runPageCommands";
	private static final String SELECT_PAGES = "selectPages";
	private static final String WRITE_FILE = "writeFile";
	private static final String BREAK = "break";

	public static final List<String> ACTIONS = new ArrayList<String>();

	
	static {
		ACTIONS.add(READ_PAGES);
		ACTIONS.add(SELECT_PAGES);
		ACTIONS.add(WRITE_FILE);
	}
	
	public AbstractAction createAction(AbstractActionElement command, DocumentAnalyzer documentAnalyzer) {

		AbstractAction documentAction = null;
		if (command == null) {
			throw new RuntimeException("No action given: ");
		} else if(command instanceof DocumentIteratorElement) {
			documentAction = new DocumentIteratorAction(command);
		} else if(command instanceof DocumentBreakElement) {
			documentAction = new DocumentBreakAction(command);
		} else if (command instanceof DocumentReaderElement) {
			documentAction = new DocumentReaderAction(command);
		} else if(command instanceof PageSelectorElement) {
			documentAction = new DocumentPageSelectorAction(command);
		} else if(command instanceof PageIteratorElement) {
			documentAction = new DocumentPageRunnerAction(command);
		} else if(command instanceof DocumentWriterElement) {
			documentAction = new DocumentWriterAction(command);
		} else if(command instanceof DocumentDebuggerElement) {
			documentAction = new DocumentDebuggerAction(command);
		} else {
			throw new RuntimeException("Unknown action: "+command.getClass());
		}
		return documentAction;
	}


}
