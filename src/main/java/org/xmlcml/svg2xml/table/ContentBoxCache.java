package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.cache.AbstractCache;
import org.xmlcml.graphics.svg.cache.ComponentCache;
import org.xmlcml.graphics.svg.cache.RectCache;
import org.xmlcml.graphics.svg.cache.TextCache;
import org.xmlcml.svg2xml.text.PhraseListList;

public class ContentBoxCache extends AbstractCache {

	static final Logger LOG = Logger.getLogger(ContentBoxCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private AbstractCache rectCache;
	private TextCache textCache;
	private List<SVGContentBox> contentBoxList;
	private ContentBoxGrid contentBoxGrid;

	public ContentBoxCache() {
	}
	
	public ContentBoxCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
		this.rectCache = containingComponentCache.getOrCreateRectCache();
		this.textCache = containingComponentCache.getOrCreateTextCache();
	}

	public List<SVGContentBox> getOrCreateContentBoxList() {
		if (contentBoxList == null) {
			contentBoxList = new ArrayList<SVGContentBox>();
		}
		return contentBoxList;
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreateContentBoxList();
	}

	/** nxm operation - slow can be be optimised by using sorted y coords.
	 * 
	 * @param rectCache
	 * @param phraseListList
	 * @return
	 */
	public static ContentBoxCache createCache(RectCache rectCache, PhraseListList phraseListList) {
		ContentBoxCache contentBoxCache = null;
		if (rectCache != null && phraseListList != null) {
			contentBoxCache = new ContentBoxCache(rectCache.getOwnerComponentCache());
			SVGElement.setBoundingBoxCached(rectCache.getOrCreateRectList(), true);
			phraseListList.setBoundingBoxCached(true);
			LOG.debug("ContentBoxCache");
			contentBoxCache.createContentBoxList(rectCache.getOrCreateRectList(), phraseListList);
		}
		return contentBoxCache;
	}

	private List<SVGContentBox> createContentBoxList(List<SVGRect> rectList, PhraseListList phraseListList) {
		Real2Range ownerBBox = getOwnerComponentCache().getBoundingBox();
		LOG.trace("own "+ownerBBox);
		contentBoxList = new ArrayList<SVGContentBox>();
		// does not detach used phrases so a possibility of duplicates
		for (int irect = 0; irect < rectList.size(); irect++) {
			SVGRect rect = rectList.get(irect);
			Real2Range rectBox = rect.getBoundingBox();
			if (rectBox.isEqualTo(ownerBBox, AbstractCache.MARGIN)) {
				LOG.info("Omitted box surrounding ownerCache area");
			} else {
				LOG.trace("RECTBOX "+irect+"; "+rectBox);
				SVGContentBox contentBox = new SVGContentBox(rect);
				contentBox.addContainedElements(phraseListList);
				if (contentBox.size() > 0) {
					LOG.trace("CB "+contentBox.toString());
					contentBoxList.add(contentBox);
				}
			}
		}
		return contentBoxList;
	}

	public ContentBoxGrid getOrCreateContentBoxGrid() {
		if (contentBoxGrid == null) {
			List<SVGContentBox> contentBoxList = getOrCreateContentBoxList();
			List<SVGRect> rectList = new ArrayList<SVGRect>();
			for (SVGContentBox contentBox : contentBoxList) {
				rectList.add(contentBox.getRect());
			}
			contentBoxGrid = new ContentBoxGrid();
			contentBoxGrid.add(rectList);
		}
		return contentBoxGrid;
	}

	public String toString() {
		String s = ""
				+ "rect: "+rectCache+"\n"
				+ "text: "+textCache+"\n"
				+ "contentBoxList: "+contentBoxList.size()+"; "+contentBoxList+"\n"
				+ "contentBoxGrid: "+contentBoxGrid+"\n";
		return s;
	}

	@Override
	public void clearAll() {
		superClearAll();
		contentBoxList = null;
		contentBoxGrid = null;
	}

}
