package org.xmlcml.svg2xml.box;

import org.xmlcml.graphics.svg.cache.AbstractCache;

@Deprecated // moved to svg package
public class ContentBoxCacheOld /*extends AbstractCache */{

//`	static final Logger LOG = Logger.getLogger(ContentBoxCacheOld.class);
//	static {
//		LOG.setLevel(Level.DEBUG);
//	}
//
//	private RectCache rectCache;
//	private TextChunkCacheOld textChunkCache;
//	private List<SVGContentBoxOld> contentBoxList;
//	private ContentBoxGridOld contentBoxGrid;
//
//	public ContentBoxCacheOld() {
//	}
//	
//	public ContentBoxCacheOld(ComponentCache containingComponentCache) {
//		super(containingComponentCache);
//		this.rectCache = containingComponentCache.getOrCreateRectCache();
//		// FIXME
//		throw new RuntimeException("TextChunkCache NYI");
////		this.textChunkCache = containingComponentCache.getOrCreateTextChunkCache();
//	}
//
//	public List<SVGContentBoxOld> getOrCreateContentBoxList() {
//		if (contentBoxList == null) {
//			contentBoxList = new ArrayList<SVGContentBoxOld>();
//		}
//		return contentBoxList;
//	}
//
//	public List<? extends SVGElement> getOrCreateElementList() {
//		return getOrCreateContentBoxList();
//	}
//
//	/** nxm operation - slow can be be optimised by using sorted y coords.
//	 * 
//	 * @param rectCache
//	 * @param phraseListList
//	 * @return
//	 */
//	public static ContentBoxCacheOld createCache(RectCache rectCache, PhraseListListOld phraseListList) {
//		ContentBoxCacheOld contentBoxCache = null;
//		if (rectCache != null && phraseListList != null) {
//			contentBoxCache = new ContentBoxCacheOld(rectCache.getOwnerComponentCache());
//			SVGElement.setBoundingBoxCached(rectCache.getOrCreateRectList(), true);
//			phraseListList.setBoundingBoxCached(true);
//			LOG.debug("ContentBoxCache");
//			contentBoxCache.createContentBoxList(rectCache.getOrCreateRectList(), phraseListList);
//		}
//		return contentBoxCache;
//	}
//
//	private List<SVGContentBoxOld> createContentBoxList(List<SVGRect> rectList, GraphicsElement phraseListList) {
//		Real2Range ownerBBox = getOwnerComponentCache().getBoundingBox();
//		LOG.trace("own "+ownerBBox);
//		contentBoxList = new ArrayList<SVGContentBoxOld>();
//		// does not detach used phrases so a possibility of duplicates
//		for (int irect = 0; irect < rectList.size(); irect++) {
//			SVGRect rect = rectList.get(irect);
//			Real2Range rectBox = rect.getBoundingBox();
//			if (rectBox.isEqualTo(ownerBBox, AbstractCache.MARGIN)) {
//				LOG.info("Omitted box surrounding ownerCache area");
//			} else {
//				LOG.trace("RECTBOX "+irect+"; "+rectBox);
//				SVGContentBoxOld contentBox = new SVGContentBoxOld(rect);
//				contentBox.addContainedElements(phraseListList);
//				if (contentBox.size() > 0) {
//					LOG.trace("CB "+contentBox.toString());
//					contentBoxList.add(contentBox);
//				}
//			}
//		}
//		return contentBoxList;
//	}
//
//	public ContentBoxGridOld getOrCreateContentBoxGrid() {
//		if (contentBoxGrid == null) {
//			List<SVGContentBoxOld> contentBoxList = getOrCreateContentBoxList();
//			List<SVGRect> rectList = new ArrayList<SVGRect>();
//			for (SVGContentBoxOld contentBox : contentBoxList) {
//				rectList.add(contentBox.getRect());
//			}
//			contentBoxGrid = new ContentBoxGridOld();
//			contentBoxGrid.add(rectList);
//		}
//		return contentBoxGrid;
//	}
//
//	public String toString() {
//		String s = ""
//				+ "rect: "+rectCache+"\n"
//				+ "text: "+textChunkCache+"\n"
//				+ "contentBoxList: "+contentBoxList.size()+"; "+contentBoxList+"\n"
//				+ "contentBoxGrid: "+contentBoxGrid+"\n";
//		return s;
//	}
//
//	@Override
//	public void clearAll() {
//		superClearAll();
//		contentBoxList = null;
//		contentBoxGrid = null;
//	}

}
