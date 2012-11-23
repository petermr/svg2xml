package org.xmlcml.svgplus.paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Node;

import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.command.DocumentWriterAction;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;

/*
<path d="M0.0 0.0 L57.984 0.0 L57.984 77.279 L0.0 77.279 L0.0 0.0 Z"/>
 */
public class PathElement extends AbstractActionElement {

	public final static String TAG ="path";
	
	private static final String D = "d";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	static {
			ATTNAMES.add(D);
	};

	private SVGPath svgPath;
	/** constructor
	 */
	public PathElement() {
		super(TAG);
	}
	
	/** constructor
	 */
	public PathElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PathElement(this);
    }

	

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public SVGPath getSVGPath() {
		if (svgPath == null) {
			svgPath = (SVGPath) SVGElement.readAndCreateSVG(this);
		}
		return svgPath;
	}

	public String getD() {
		return this.getAttributeValue(D);
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
		});
	}

	@Override
	protected AbstractAction createAction() {
		return null;
//		return new PathAction(this);
	}
}
