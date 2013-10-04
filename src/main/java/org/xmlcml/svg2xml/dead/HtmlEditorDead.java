package org.xmlcml.svg2xml.dead;


public class HtmlEditorDead {

//	private final static Logger LOG = Logger.getLogger(HtmlEditorOld.class);
//	
//	private static final String TEXT = "TEXT";
//
//	private List<HtmlAnalyzerOld> htmlAnalyzerListSortedByChunkId;
//	private PDFAnalyzer pdfAnalyzer;
//	private Map<ChunkId, HtmlAnalyzerOld> htmlAnalyzerByIdMap;
//	private List<HtmlAnalyzerOld> figureHtmlAnalyzerList;
//	private List<HtmlAnalyzerOld> tableHtmlAnalyzerList;
//	private List<HtmlAnalyzerOld> mergedHtmlAnalyzerList;
//	private HtmlAnalyzerOld textDivAnalyzer;
//
//	private PDFAnalyzerIO pdfIo;
//	
//	public HtmlEditorOld(PDFAnalyzer pdfAnalyzer) {
//		this.pdfAnalyzer = pdfAnalyzer;
//		this.pdfIo = pdfAnalyzer.getPDFIO();
//	}
//
//    public void accept(HtmlVisitor visitor) {
//        visitor.visit(this);
//    }
//    
//	public /*List<HtmlAnalyzer>*/ void categorizeHtml() {
//		LOG.debug("Merging HTML");
//		HtmlDiv textDiv = new HtmlDiv();
//		createTextDivAnalyzer(textDiv);
//		HtmlAnalyzerOld lastAnalyzer = null;
//		figureHtmlAnalyzerList = new ArrayList<HtmlAnalyzerOld>();
//		tableHtmlAnalyzerList = new ArrayList<HtmlAnalyzerOld>();
//		mergedHtmlAnalyzerList = new ArrayList<HtmlAnalyzerOld>();
//		for (HtmlAnalyzerOld htmlAnalyzer : htmlAnalyzerListSortedByChunkId) {
//			String id = htmlAnalyzer.getId();
//			String classAttribute = htmlAnalyzer.getClassAttribute();
//			String classAttribute0 = (classAttribute == null) ? null : classAttribute.split("\\s+")[0];
//			LOG.trace("Class "+classAttribute+" "+classAttribute0+" "+htmlAnalyzer.getAnalyzer());
//			if (classAttribute == null) { 
//				mergedHtmlAnalyzerList.add(htmlAnalyzer);
//				LOG.trace("merging "+id);
//				merge(lastAnalyzer, htmlAnalyzer, textDiv);
//				lastAnalyzer = htmlAnalyzer;
//			} else if (HtmlAnalyzerOld.OMIT.equals(classAttribute)) {
//				// already designated as OMIT
//				LOG.trace("OMITTED "+id);
//			} else if (FigureIndexer.TITLE.equals(classAttribute0)) {
//				htmlAnalyzer.setChunkType(classAttribute0);
//				figureHtmlAnalyzerList.add(htmlAnalyzer);
//				LOG.trace(classAttribute+" = "+id);
//			} else if (TableIndexer.TITLE.equals(classAttribute0)) {
//				htmlAnalyzer.setId(id);
//				htmlAnalyzer.setChunkType(classAttribute0);
//				tableHtmlAnalyzerList.add(htmlAnalyzer);
//				LOG.trace(classAttribute+" = "+id);
//			} else {
//				LOG.trace("untreated CLASS "+classAttribute);
//			}
//			
//		}
////		return htmlAnalyzerList;
//	}
//
//	private void createTextDivAnalyzer(HtmlDiv textDiv) {
//		textDivAnalyzer = new HtmlAnalyzerOld(textDiv, this);
//		textDivAnalyzer.setClassAttribute(TEXT);
//		textDivAnalyzer.setChunkType(TEXT);
//		textDivAnalyzer.setSerial(1);
//		textDivAnalyzer.setId("t.1.0");
//	}
//
//	private void merge(HtmlAnalyzerOld lastAnalyzer, HtmlAnalyzerOld htmlAnalyzer, HtmlDiv topDiv) {
//		TextStructurer lastTextContainer = (lastAnalyzer == null) ? 
//				null : lastAnalyzer.getTextStructurer();
//		TextStructurer textStructurer = htmlAnalyzer.getTextStructurer();
//		boolean merged = false;
//		if (lastTextContainer != null && textStructurer != null) {
//			if (lastTextContainer.endsWithRaggedLine() && textStructurer.startsWithRaggedLine()) {
//				merged = htmlAnalyzer.mergeLinesWithPrevious(lastAnalyzer, topDiv);
//			}
//		} 
//		if (!merged) {
//			htmlAnalyzer.addIdSeparator(topDiv);
//			Element copyElement = null;
//			try {
//				copyElement = HtmlElement.create((Element)htmlAnalyzer.createHtmlElement());
//			} catch (Exception e) {
//				LOG.debug("cannot create HTML: "+e);
//				// might be SVG
//				copyElement = (Element) htmlAnalyzer.createHtmlElement().copy();
//			}
//			topDiv.appendChild(copyElement);
//		}
////		htmlAnalyzer.removeSVGNodes();
//	}
//
//	public void removeDuplicates() {
//		getHtmlAnalyzerListSortedByChunkId();
//		for (HtmlAnalyzerOld htmlAnalyzer : htmlAnalyzerListSortedByChunkId) {
//			ChunkId id = new ChunkId(htmlAnalyzer.getId());
//			if (pdfAnalyzer.getIndex().getUsedIdSet().contains(id)) {
//				String classAttribute = htmlAnalyzer.getClassAttribute();
//				LOG.trace(id+" "+classAttribute);
//				if (classAttribute == null) {
//					LOG.trace("skip duplicate: "+id+" "+classAttribute);
//					htmlAnalyzer.setClassAttribute(HtmlAnalyzerOld.OMIT);
//				}
//			}
//		}
//	}
//
//	public void outputHtmlElements() {
//		LOG.debug("figures HTML");
//		for (HtmlAnalyzerOld htmlAnalyzer : figureHtmlAnalyzerList) {
//			htmlAnalyzer.outputElementAsHtml(pdfIo.getExistingOutputDocumentDir());
//		}
//		LOG.debug("tables HTML");
//		for (HtmlAnalyzerOld htmlAnalyzer : tableHtmlAnalyzerList) {
//			htmlAnalyzer.outputElementAsHtml(pdfIo.getExistingOutputDocumentDir());
//		}
//		LOG.debug("merged HTML");
//		for (HtmlAnalyzerOld htmlAnalyzer : mergedHtmlAnalyzerList) {
//			htmlAnalyzer.outputElementAsHtml(pdfIo.getExistingOutputDocumentDir());
//		}
//		LOG.debug("merged TEXT");
//		textDivAnalyzer.outputElementAsHtml(pdfIo.getExistingOutputDocumentDir());
//		
//	}
//
//	public void mergeCaptions() {
////		for (HtmlAnalyzer htmlAnalyzer : htmlAnalyzerListSortedByChunkId) {
////			String chunkType = htmlAnalyzer.addTypeSerialAttributes();
////			if (FigureSemanticAnalyzer.TITLE.equals(chunkType)) {
////				LOG.trace("FIG FIX");
////				if (htmlAnalyzer.containsDivImage()) {
////					LOG.trace("***********IMG************");
////				} else {
////					HtmlAnalyzer previousAnalyzer = htmlAnalyzer.getPreviousHtmlAnalyzer(htmlAnalyzerByIdMap);
////					htmlAnalyzer.addImageDivTo(previousAnalyzer);
////				}
////			}
////		}
//	}
//
//	/** create list of entities
//	 * 
//	 * @param htmlFiles
//	 * @param xpath
//	 * @param htmlPattern
//	 * @return
//	 */
//	public HtmlUl searchHtml(List<File> htmlFiles, String xpath, Pattern htmlPattern) {
//		Set<String> entitySet = new HashSet<String>();
//		HtmlUl ul = null;
//		for (File file : htmlFiles) {
//			Element html = null;
//			try {
//				html = new Builder().build(file).getRootElement();
//			} catch (Exception e) {
//				LOG.error("Failed on html File: "+file);
//			}
//			if (html != null) {
//				ul = new HtmlUl();
//				searchHtml(xpath, htmlPattern, ul, entitySet, html);
//			}
//		}
//		return ul;
//	}
//
//	private void searchHtml(String xpath, Pattern htmlPattern, HtmlUl ul,
//			Set<String> entitySet, Element html) {
//		Nodes nodes = html.query(xpath);
//		for (int i = 0; i < nodes.size(); i++) {
//			String value = nodes.get(i).getValue();
//			if (htmlPattern.matcher(value).matches()) {	
//				if (!entitySet.contains(value)) {
//					LOG.trace(value);
//					HtmlLi li = new HtmlLi();
//					ul.appendChild(li);
//					li.setValue(value);
//					entitySet.add(value);
//				}
//			}
//		}
//	}
//
//	public List<HtmlAnalyzerOld> getHtmlAnalyzerListSortedByChunkId() {
//		if (htmlAnalyzerListSortedByChunkId == null) {
//			List<ChunkId> chunkIdList = Arrays.asList(htmlAnalyzerByIdMap.keySet().toArray(new ChunkId[0]));
//			Collections.sort(chunkIdList);
//			htmlAnalyzerListSortedByChunkId = new ArrayList<HtmlAnalyzerOld>();
//			for (ChunkId id : chunkIdList) {
//				HtmlAnalyzerOld htmlAnalyzer = htmlAnalyzerByIdMap.get(id);
//				htmlAnalyzer.setId(id.toString());
//				htmlAnalyzerListSortedByChunkId.add(htmlAnalyzer);
//			}
//		}
//		return htmlAnalyzerListSortedByChunkId;
//	}
//
//	Map<ChunkId, HtmlAnalyzerOld> getHtmlAnalyzerByIdMap() {
//		ensureHtmlAnalyzerByIdMap();
//		return htmlAnalyzerByIdMap;
//	}
//
////	public void setHtmlElementByIdMap(Map<ChunkId, HtmlElement> htmlElementByIdMap) {
////		this.htmlElementByIdMap = htmlElementByIdMap;
////	}
//
//	public void createLinkedElementList() {
//		getHtmlAnalyzerListSortedByChunkId();
//		HtmlAnalyzerOld lastAnalyzer = null;;
//		for (HtmlAnalyzerOld htmlAnalyzer : htmlAnalyzerListSortedByChunkId) {
//			htmlAnalyzer.addLinks(lastAnalyzer);
//			lastAnalyzer = htmlAnalyzer;
//		}
//	}
//
//	String getValueFromHtml(ChunkId id) {
//		HtmlAnalyzerOld htmlAnalyzer = getHtmlAnalyzerByIdMap().get(id);
//		return htmlAnalyzer.getValue();
//	}
//
//	protected HtmlAnalyzerOld getHtmlAnalyzer(ChunkId id) {
//		ensureHtmlAnalyzerByIdMap();
//		return (htmlAnalyzerByIdMap == null) ? null : htmlAnalyzerByIdMap.get(id);
//	}
//
//	protected void ensureHtmlAnalyzerByIdMap() {
//		if (htmlAnalyzerByIdMap == null) {
//			htmlAnalyzerByIdMap = new HashMap<ChunkId, HtmlAnalyzerOld>();
//		}
//	}
//
//	public SVGG labelChunk() {
//		// might iterate through pages
//		throw new RuntimeException("NYI");
//	}
//
//	void indexHtmlBySvgId(HtmlAnalyzerOld htmlAnalyzer, ChunkId chunkId) {
//		ensureHtmlAnalyzerByIdMap();
//		htmlAnalyzerByIdMap.put(chunkId, htmlAnalyzer);
//	}
//
//	/** label HtmlElement
//	 * 
//	 * @param id
//	 * @param title
//	 * @param serial
//	 */
//	void labelChunk(ChunkId id, String title, Integer serial) {
//		getHtmlAnalyzer(id);
//		HtmlAnalyzerOld htmlAnalyzer = getHtmlAnalyzer(id);
//		if (htmlAnalyzer != null) {
//			htmlAnalyzer.addClassAttributeIfMissing(title, serial);
//		}
//	}
//
//	public HtmlUl searchHtml(String italicXpathS, Pattern pattern) {
//		throw new RuntimeException("NYI");
//	}
//	
//	public void addHtmlElement(HtmlElement htmlElement, ChunkId chunkId) {
//		HtmlAnalyzerOld htmlAnalyzer = new HtmlAnalyzerOld(htmlElement, this);
//		getHtmlAnalyzerByIdMap().put(chunkId, htmlAnalyzer);
//	}
//
//	public void analyzeFigures() {
//		for (HtmlAnalyzerOld figureHtmlAnalyzer : figureHtmlAnalyzerList) {
//			FigureAnalyzer figureAnalyzer = createFigureAnalyzer(figureHtmlAnalyzer);
//			figureAnalyzer.analyze();
//		}
//	}
//
//	private FigureAnalyzer createFigureAnalyzer(HtmlAnalyzerOld figureHtmlAnalyzer) {
//		FigureAnalyzer figureAnalyzer = null;
//		PageChunkAnalyzer analyzer = figureHtmlAnalyzer.getAnalyzer();
//		if (analyzer instanceof MixedAnalyzer) {
//			TextAnalyzer textAnalyzer = ((MixedAnalyzer)analyzer).getTextAnalyzer();
//			ImageAnalyzer imageAnalyzer = ((MixedAnalyzer)analyzer).getImageAnalyzer();
//			ShapeAnalyzer shapeAnalyzer = ((MixedAnalyzer)analyzer).getShapeAnalyzer();
//			figureAnalyzer = new FigureAnalyzer(textAnalyzer, shapeAnalyzer, imageAnalyzer, null);
//		} else if (analyzer instanceof TextAnalyzer) {
//			figureAnalyzer = new FigureAnalyzer((TextAnalyzer)analyzer, (ShapeAnalyzer)null, (ImageAnalyzer)null, null);
//			LOG.error("Figure has no path/image");
//		}
//		return figureAnalyzer;
//	}
//
//	public void analyzeTables() {
//		for (HtmlAnalyzerOld tableHtmlAnalyzer : tableHtmlAnalyzerList) {
//			TableAnalyzer tableAnalyzer = createTableAnalyzer(tableHtmlAnalyzer);
////			tableAnalyzer.analyze();
//			HtmlElement htmlElement = tableAnalyzer.createTable();
//			LOG.debug("Table "+htmlElement.toXML());
//			// transfer any existing id and class attribute
//			HtmlElement oldHtmlElement = tableHtmlAnalyzer.createHtmlElement();
//			if (oldHtmlElement != null) {
//				XMLUtil.copyAttributes(oldHtmlElement, htmlElement);
//			}
//			tableHtmlAnalyzer.setHtmlElement(htmlElement);
//		}
//	}
//
//	private TableAnalyzer createTableAnalyzer(HtmlAnalyzerOld tableHtmlAnalyzer) {
//		TableAnalyzer tableAnalyzer = null;
//		PageChunkAnalyzer analyzer = tableHtmlAnalyzer.getAnalyzer();
//		
//		if (analyzer instanceof MixedAnalyzer) {
//			MixedAnalyzer mixedAnalyzer = ((MixedAnalyzer)analyzer);
//			LOG.trace("M "+mixedAnalyzer);
//			TextAnalyzer textAnalyzer = mixedAnalyzer.getTextAnalyzer();
//			if (textAnalyzer == null) {
//				LOG.error("Table has no text so cannot process");
//				return null;
//			}
//			ImageAnalyzer imageAnalyzer = mixedAnalyzer.getImageAnalyzer();
//			if (imageAnalyzer != null) {
//				LOG.error("Cannot currently analyze images in Tables");
//				return null;
//			}
//			ShapeAnalyzer ShapeAnalyzer = mixedAnalyzer.getShapeAnalyzer();
//			tableAnalyzer = new TableAnalyzer(textAnalyzer, ShapeAnalyzer);
//		} else if (analyzer instanceof TextAnalyzer) {
//			TextAnalyzer textAnalyzer = (TextAnalyzer)analyzer;
//			tableAnalyzer = new TableAnalyzer(textAnalyzer, null);
//		}
//		return tableAnalyzer;
//	}
//
//
//
}
