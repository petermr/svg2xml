package org.xmlcml.svgplus.figure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.core.AbstractActionElement;


public class FigureAnalyzerElement extends AbstractActionElement {

	public final static String TAG ="figureAnalyzer";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static final String ANALYZE_FRAGMENTS = "analyzeFragments";
	static final String CREATE_FRAGMENTS = "createFragments";
	final static String CREATE_WORDS_LINES = "createWordsLines";
	static final String FRAGMENT_COLOURS = "clusterColours";
	final static String FRAGMENT_MARGINS = "clusterMargins";
	final static String LOCATION_STRATEGY = "locationStrategy";
	final static String PANEL_SEPARATION = "panelSeparation";

	static {
		ATTNAMES.add(ANALYZE_FRAGMENTS);
		ATTNAMES.add(CREATE_FRAGMENTS);
		ATTNAMES.add(CREATE_WORDS_LINES);
		ATTNAMES.add(FILENAME);
		ATTNAMES.add(FRAGMENT_COLOURS);
		ATTNAMES.add(FRAGMENT_MARGINS);
		ATTNAMES.add(LOCATION_STRATEGY);
		ATTNAMES.add(PANEL_SEPARATION);
	}

	/** constructor
	 */
	public FigureAnalyzerElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public FigureAnalyzerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new FigureAnalyzerElement(this);
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
				LOCATION_STRATEGY,
		});
	}


}
