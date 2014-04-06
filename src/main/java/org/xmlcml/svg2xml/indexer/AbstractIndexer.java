package org.xmlcml.svg2xml.indexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.svg2xml.pdf.ChunkId;
import org.xmlcml.svg2xml.pdf.PDFIndex;

/** superclass for semantic analysis of text chunks
 * 
 * @author pm286
 *
 */
public abstract class AbstractIndexer {

	private final static Logger LOG = Logger.getLogger(AbstractIndexer.class);
	
	private PDFIndex pdfIndex;

	List<ChunkId> idList;

	List<Integer> serialList;

	public AbstractIndexer(PDFIndex pdfIndex) {
		this.pdfIndex = pdfIndex;
	}

	public AbstractIndexer() {
		// TODO Auto-generated constructor stub
	}

	/** Pattern for the content for this analyzer
	 * 
	 * @return pattern (default null)
	 */
	protected Pattern getPattern() {
		return null;
	}

	/** (constant) title for this analyzer
	 * 
	 * @return title (default null)
	 */
	public String getTitle() {
		String s = this.getClass().getSimpleName();
		return s;
	}

	public Integer indexAndLabelChunk(String content, ChunkId id) {
			Pattern pattern = getPattern();
			String title = getTitle();
			Integer serial = AbstractIndexer.getSerial(pattern, content);
			if (serial != null) {
				LOG.trace(title+"-"+serial);
				ensureIdSerialList();
				serialList.add(serial);
				idList.add(id);
				pdfIndex.addUsedId(id);
	//			pdfIndex.pdfAnalyzer.htmlEditor.labelChunk(id, title, serial);
			}
			return serial;
		}

	private void ensureIdSerialList() {
		if (idList == null) {
			idList = new ArrayList<ChunkId>();
			serialList = new ArrayList<Integer>();
		}
	}

	/** returns null if no match, -1 if serial not found, else serial
	 * 
	 * @param pattern
	 * @param content
	 * @return
	 */
	public static Integer getSerial(Pattern pattern, String content) {
		Integer serial = null;
		if (content != null) {
			Matcher matcher = pattern.matcher(content);
			if (matcher.matches()) {
				if (matcher.groupCount() == 0) {
					serial = -1;
				} else {
					String s = matcher.group(1);
					if (s != null) {
						s = s.trim();
						serial= -1;
						try {
							serial = new Integer(s);
						} catch (Exception e) {
							// not a number
						}
					}
				}
			}
		}
		return serial;
	}

}
