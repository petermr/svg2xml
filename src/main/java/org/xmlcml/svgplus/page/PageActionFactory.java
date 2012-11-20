package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.core.AbstractActionElement;
import org.xmlcml.svgplus.figure.FigureAnalyzerAction;
import org.xmlcml.svgplus.figure.FigureAnalyzerElement;
//import org.xmlcml.graphics.tree.TreeAction;
//import org.xmlcml.graphics.tree.TreeElement;

public class PageActionFactory {
	private final static Logger LOG = Logger.getLogger(PageActionFactory.class);

	/** action values
	 * 
	 */
	public static final String CREATE_WHITESPACE_CHUNKS = "createWhitespaceChunks";
	public static final String DELETE = "delete";
	public static final String DRAW_BOXES = "drawBoxes";
	public static final String EXTRACT = "extract";
	public static final String MAKE_TEXT_CHUNKS = "makeTextChunks";
	public static final String NORMALIZE = "normalize";
	public static final String NORMALIZE_PATHS = "normalizePaths";
	public static final String PROCESS_STYLES = "processStyles";
	public static final String REPLACE_ROUNDED_BOX = "replaceRoundedBox";
	public static final String WRITE_FILE = "writeFile";

	public static final List<String> ACTIONS = new ArrayList<String>();

	
	static {
		ACTIONS.add(CREATE_WHITESPACE_CHUNKS);
		ACTIONS.add(DELETE);
		ACTIONS.add(DRAW_BOXES);
		ACTIONS.add(EXTRACT);
		ACTIONS.add(MAKE_TEXT_CHUNKS);
		ACTIONS.add(NORMALIZE);
		ACTIONS.add(PROCESS_STYLES);
		ACTIONS.add(REPLACE_ROUNDED_BOX);
		ACTIONS.add(WRITE_FILE);
	}
	
	public PageAction createAction(AbstractActionElement command) {

		PageAction pageAction = null;
		if (false) {
		} else if(command instanceof ChunkAnalyzerElement) {
			pageAction = new ChunkAnalyzerAction(command);
		} else if(command instanceof BoxDrawerElement) {
			pageAction = new BoxDrawerAction(command);
		} else if(command instanceof BoxProcessorElement) {
			pageAction = new BoxProcessorAction(command);
		} else if(command instanceof ElementStylerElement) {
			pageAction = new ElementStylerAction(command);
		} else if(command instanceof NodeDeleterElement) {
			pageAction = new NodeDeleterAction(command);
		} else if(command instanceof FigureAnalyzerElement) {
			pageAction = new FigureAnalyzerAction(command);
		} else if(command instanceof PageNormalizerElement) {
			pageAction = new PageNormalizerAction(command);
		} else if(command instanceof PageVariableElement) {
			pageAction = new PageVariableAction(command);
		} else if(command instanceof PageWriterElement) {
			pageAction = new PageWriterAction(command);
		} else if(command instanceof PathNormalizerElement) {
			pageAction = new PathNormalizerAction(command);
		} else if(command instanceof TextChunkerElement) {
			pageAction = new TextChunkerAction(command);
		} else if(command instanceof WhitespaceChunkerElement) {
			pageAction = new WhitespaceChunkerAction(command);
		} else if(command instanceof VariableExtractorElement) {
			pageAction = new VariableExtractorAction(command);
			
		} else if(command instanceof PageAssertElement) {
			pageAction = new PageAssertAction(command);
			
//		} else if(command instanceof TreeElement) {
//			pageAction = new TreeAction(command);
			
		} else {
			throw new RuntimeException("Unknown action: "+command.getClass()+" "+command.toXML());
		}
		return pageAction;
	}


}
