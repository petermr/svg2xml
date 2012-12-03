package org.xmlcml.svgplus.action;



public abstract class DocumentActionX extends AbstractActionX {

	public DocumentActionX(AbstractActionX documentActionElement) {
		super(documentActionElement);
	}

	protected DocumentActionX() {
		super();
	}

	protected DocumentActionX(String tag) {
		super(tag);
	}

	/** execute the command
	 * 
	 */
	public abstract void run();

	// attributes and other common methods could go here
}
