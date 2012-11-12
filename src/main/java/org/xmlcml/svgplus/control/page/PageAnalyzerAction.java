package org.xmlcml.svgplus.control.page;

import java.util.ArrayList;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.AbstractAction;
import org.xmlcml.svgplus.control.AbstractActionElement;
import org.xmlcml.svgplus.core.AbstractAnalyzer;
import org.xmlcml.svgplus.core.DocumentAnalyzer;
import org.xmlcml.graphics.svg.SVGSVG;

public class PageAnalyzerAction extends AbstractAction {

	private final static Logger LOG = Logger.getLogger(PageAnalyzerAction.class);
	private List<PageAction> pageActions;
	protected DocumentAnalyzer documentAnalyzer;
	protected PageAnalyzer pageAnalyzer;
	private PageAnalyzerElement pageAnalyzerActionCommand;
	private List<AbstractActionElement> pageActionCommandElements;
	private Integer zeroBasedPageNumber = null;
	private long timeout = 60000L; // timeout in millis
	
	public PageAnalyzerAction(PageAnalyzerElement pageAnalyzerActionCommand, DocumentAnalyzer documentAnalyzer) {
		super(pageAnalyzerActionCommand);
		this.setDocumentAnalyzer(documentAnalyzer);
		this.ensurePageAnalyzer(documentAnalyzer);
		this.pageAnalyzerActionCommand = pageAnalyzerActionCommand;
	}
	

	private AbstractAnalyzer ensurePageAnalyzer(DocumentAnalyzer documentAnalyzer) {
		if (pageAnalyzer == null) {
			this.pageAnalyzer = new PageAnalyzer();
			pageAnalyzer.setDocumentAnalyzer(documentAnalyzer);
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
		return getActionCommandElement().getAttributeValue(AbstractActionElement.ACTION);
	}

	private void setDocumentAnalyzer(DocumentAnalyzer documentAnalyzer) {
		this.documentAnalyzer = documentAnalyzer;
	}

	public void setSVGPage(SVGSVG svgPage) {
		ensurePageAnalyzer(documentAnalyzer);
		pageAnalyzer.setSVGPage(svgPage);
	}

	private List<PageAction> createPageActions() {
		if (pageActions == null) {
			PageActionFactory factory = new PageActionFactory();
			pageActions = new ArrayList<PageAction>();
			for (AbstractActionElement command : pageActionCommandElements) {
				PageAction pageAction = factory.createAction(command);
				pageAction.setPageAnalyzer(pageAnalyzer);
				pageActions.add(pageAction);
			}
		}
		return pageActions;
	}


	public void run() {
		pageActionCommandElements = pageAnalyzerActionCommand.getPageActionCommandElements();
		if (exceededFileSize()) {
			// skipped
		} else {
			timeout = getTimeout(16000L);
			createPageActions();
			LOG.trace("pageActions: "+pageActions.size()+" on page "+pageAnalyzer.getPageNumber());
	
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
                // we should get the threads to kill themselves
//                t.join();
		    	break;
            }		    
		}
	}


	void runActions() {
		for (PageAction pageAction: pageActions) {
			PageSelector pageSelector = pageAction.getPageSelector();
			if (pageSelector == null || pageSelector.isSet(zeroBasedPageNumber)) {
				try {
					pageAction.run();
				} catch (RuntimeException e) {
					throw new RuntimeException("failed on instruction "+pageAction.getActionCommandElement().toXML(), e);
				} catch (Exception e) {
					throw new RuntimeException("problem on instruction "+pageAction.getActionCommandElement().toXML(), e);
				}
			}
		}
	}
	
	public void setZeroBasedPageNumber(int i) {
		this.zeroBasedPageNumber = i;
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
