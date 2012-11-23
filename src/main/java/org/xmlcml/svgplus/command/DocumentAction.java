package org.xmlcml.svgplus.command;


public abstract class DocumentAction extends AbstractAction {

	public DocumentAction(AbstractActionElement documentActionElement) {
		super(documentActionElement);
	}

	protected DocumentAction() {
		super();
	}

	/** execute the command
	 * 
	 */
	public abstract void run();

}
