package org.xmlcml.svg2xml.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.cache.AbstractCache;
import org.xmlcml.graphics.svg.cache.ComponentCache;
import org.xmlcml.graphics.svg.cache.TextCache;

/** creates textChunks 
 * uses TextCache as raw input and systematically builds PhraseList and TextChunks
 * NOT FINISHED
 * 
 * @author pm286
 *
 */
@Deprecated // moved to svg package // perhaps never used 
public class TextChunkCacheOld extends AbstractCache {
	static final Logger LOG = Logger.getLogger(TextChunkCacheOld.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGText> rawTextList;
	private List<TextChunkOld> textChunkList;
//	private List<PhraseListList> textChunkListOld;
	private TextCache siblingTextCache;

	private TextChunkCacheOld() {
	}
	
	public TextChunkCacheOld(ComponentCache containingComponentCache) {
		super(containingComponentCache);
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreateTextChunkList();
	}
	
	public List<SVGText> getOrCreateRawTextList() {
		if (textChunkList == null) {
			 rawTextList = siblingTextCache == null ? null : siblingTextCache.getTextList();
			if (textChunkList == null) {
				textChunkList = new ArrayList<TextChunkOld>();
			}
		}
		return rawTextList;
	}

//	public List<SVGText> getOrCreateRawTextListOld() {
//		if (textChunkListOld == null) {
//			 rawTextList = siblingTextCache == null ? null : siblingTextCache.getTextList();
//			if (textChunkListOld == null) {
//				textChunkListOld = new ArrayList<PhraseListList>();
//			}
//		}
//		return rawTextList;
//	}

//	public List<PhraseListList> getOrCreateTextChunkListOld() {
//		if (textChunkListOld == null) {
//			getOrCreateRawTextListOld();
//			throw new RuntimeException("TextChunks NYI");
//		}
//		return textChunkListOld;
//	}

	public List<TextChunkOld> getOrCreateTextChunkList() {
		if (textChunkList == null) {
			getOrCreateRawTextList();
			throw new RuntimeException("TextChunks NYI");
		}
		return textChunkList;
	}



	@Override
	public String toString() {
		getOrCreateTextChunkList();
		String s = ""
			+ "rawText size: "+getOrCreateRawTextList().size()
			+ "textChunks: "+textChunkList.size()+"; "
//			+ "textChunksOld: "+textChunkListOld.size()+"; "
			;
		return s;

	}

	@Override
	public void clearAll() {
		superClearAll();
		textChunkList = null;
//		textChunkListOld = null;
	}

}
