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
public class PhraseCache extends AbstractCache {
	static final Logger LOG = Logger.getLogger(PhraseCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGText> rawTextList;
	private List<TextChunk> textChunkList;
	private List<PhraseListList> textChunkListOld;
	private TextCache siblingTextCache;

	private PhraseCache() {
		
	}
	
	public PhraseCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreateTextChunkListOld();
	}
	
	public List<SVGText> getOrCreateRawTextList() {
		if (textChunkList == null) {
			 rawTextList = siblingTextCache == null ? null : siblingTextCache.getTextList();
			if (textChunkList == null) {
				textChunkList = new ArrayList<TextChunk>();
			}
		}
		return rawTextList;
	}

	public List<SVGText> getOrCreateRawTextListOld() {
		if (textChunkListOld == null) {
			 rawTextList = siblingTextCache == null ? null : siblingTextCache.getTextList();
			if (textChunkListOld == null) {
				textChunkListOld = new ArrayList<PhraseListList>();
			}
		}
		return rawTextList;
	}

	public List<PhraseListList> getOrCreateTextChunkListOld() {
		if (textChunkListOld == null) {
			getOrCreateRawTextListOld();
			throw new RuntimeException("TextChunks NYI");
		}
		return textChunkListOld;
	}

	public List<TextChunk> getOrCreateTextChunkList() {
		if (textChunkList == null) {
			getOrCreateRawTextListOld();
			throw new RuntimeException("TextChunks NYI");
		}
		return textChunkList;
	}



	@Override
	public String toString() {
		getOrCreateTextChunkListOld();
		String s = ""
			+ "rawText size: "+getOrCreateRawTextListOld().size()
			+ "textChunks: "+textChunkList.size()+"; "
			+ "textChunksOld: "+textChunkListOld.size()+"; ";
		return s;

	}

	@Override
	public void clearAll() {
		superClearAll();
		textChunkList = null;
		textChunkListOld = null;
	}

}
