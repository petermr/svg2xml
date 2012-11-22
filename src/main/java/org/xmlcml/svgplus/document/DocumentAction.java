package org.xmlcml.svgplus.document;

import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;

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
