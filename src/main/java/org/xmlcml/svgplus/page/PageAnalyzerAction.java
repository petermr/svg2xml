package org.xmlcml.svgplus.page;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.command.AbstractAnalyzer;
import org.xmlcml.svgplus.core.DocumentAnalyzer;
import org.xmlcml.svgplus.core.PageAnalyzer;
import org.xmlcml.svgplus.core.SemanticDocumentAction;

public class PageAnalyzerAction extends AbstractAction {

	private final static Logger LOG = Logger.getLogger(PageAnalyzerAction.class);
	protected PageAnalyzer pageAnalyzer;
	private long timeout = 60000L; // timeout in millis
	
	public PageAnalyzerAction(PageAnalyzerElement pageAnalyzerActionElement) {
		super(pageAnalyzerActionElement);
		this.ensurePageAnalyzer();
	}
	
	public PageAnalyzerAction(AbstractActionElement actionElement) {
		super(actionElement);
	}
	
	private AbstractAnalyzer ensurePageAnalyzer() {
		if (pageAnalyzer == null) {
			this.pageAnalyzer = new PageAnalyzer();
			pageAnalyzer.setSemanticDocumentAction(semanticDocumentAction);
		}
		return this.pageAnalyzer;
	}

	protected AbstractAnalyzer getAnalyzer() {
		return pageAnalyzer;
	}
	
	public PageAnalyzer getPageAnalyzer() {
		return (PageAnalyzer) getAnalyzer();
	}
	
	public String getActionValue() {
		return getActionElement().getAttributeValue(AbstractActionElement.ACTION);
	}

	public void setSVGPage(SVGSVG svgPage) {
		pageAnalyzer.setSVGPage(svgPage);
	}


	public void run() {
//		pageActionCommandElements = pageAnalyzerActionCommand.getPageActionCommandElements();
		if (exceededFileSize()) {
			// skipped
		} else {
			timeout = getTimeout(16000L);
	
			try {
				runThread();
			} catch (Exception e) {
				throw new RuntimeException("Thread failed: ", e);
			}
		}
		LOG.trace("finished page");
	}


	private boolean exceededFileSize() {
		boolean exceeded = false;
		Double maxMbyte = getDouble(PageAnalyzerElement.MAX_MBYTE);
		if (maxMbyte != null) {
			double maxByteSize = maxMbyte * 1048576;
			String fileSizeS = "9999999";
//			String fileSizeS = pageAnalyzer.getSVGPage().getAttributeValue(PDF2SVGReader.FILE_SIZE);
			if (fileSizeS != null) {
				Long fileSize = new Long(fileSizeS);
				if (fileSize > maxByteSize) {
					LOG.debug("SVG filesize exceeded ("+fileSize+" > "+maxByteSize+")");
					exceeded = true;
				}
			}
		} 
		return exceeded;
	}

	private void runThread() throws InterruptedException {
		RunPageAnalyzer runPageAnalyzer = new RunPageAnalyzer(this);
		Thread t = new Thread(runPageAnalyzer); 
		long startTime = System.currentTimeMillis();

		t.start();
		while (t.isAlive()) {
		    t.join(1000);
		    long delta = System.currentTimeMillis() - startTime;
		    if (delta > timeout && t.isAlive()) {
			    LOG.debug("***************************** exited after timeout: "+delta+" millis");
                t.interrupt();
		    	break;
            }		    
		}
	}


	void runActions() {
//		for (PageAction pageAction: pageActions) {
//			PageSelector pageSelector = pageAction.getPageSelector();
//			if (pageSelector == null || pageSelector.isSet(zeroBasedPageNumber)) {
//				try {
//					pageAction.run();
//				} catch (RuntimeException e) {
//					throw new RuntimeException("failed on instruction "+pageAction.getActionElement().toXML(), e);
//				} catch (Exception e) {
//					throw new RuntimeException("problem on instruction "+pageAction.getActionElement().toXML(), e);
//				}
//			}
//		}
	}
	
}
class RunPageAnalyzer implements Runnable {

	private final static Logger LOG = Logger.getLogger(RunPageAnalyzer.class);
	private PageAnalyzerAction pageAnalyzerAction;
	public RunPageAnalyzer(PageAnalyzerAction pageAnalyzerAction) {
		this.pageAnalyzerAction = pageAnalyzerAction;
	}
	
	public void run() {
        try {
        	pageAnalyzerAction.runActions();
        } catch (Exception e) {
        	LOG.debug("Exception running thread ", e);
        }
	}
}
