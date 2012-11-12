package org.xmlcml.svgplus.core;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.svgplus.control.page.PageAnalyzer;

public abstract class AbstractAnalyzer {
	private final static Logger LOG = Logger.getLogger(AbstractAnalyzer.class);

	protected Map<String, Object> valueMap;
	
	public Object getValue(String name) {
		String[] names = getNameParts(name);
		Object value = null;
		if (PageAnalyzer.NAME_PREFIX.equals(names[0]) && this instanceof PageAnalyzer) {
			value = ensureValueMap().get(names[1]);
		} else if (DocumentAnalyzer.NAME_PREFIX.equals(names[0])) {
			value = getDocumentAnalyzer().ensureValueMap().get(names[1]);
		} else {
			throw new RuntimeException("Bad regex name / or cannot access lookup "+name);
		}
		return value;
	}
	
	public List<String> getNames() {
		List<String> names = new ArrayList<String>();
		List<String> pageNames = Arrays.asList(ensureValueMap().keySet().toArray(new String[0]));
		names.addAll(pageNames);
		List<String> docNames = Arrays.asList(getDocumentAnalyzer().ensureValueMap().keySet().toArray(new String[0]));
		names.addAll(docNames);
		return names;
	}
	
	public void putValue(String name, Object value) {
		String[] names = getNameParts(name);
		if (PageAnalyzer.NAME_PREFIX.equals(names[0]) && this instanceof PageAnalyzer) {
			ensureValueMap().put(names[1], value);
		} else if (DocumentAnalyzer.NAME_PREFIX.equals(names[0])) {
			value = getDocumentAnalyzer().ensureValueMap().put(names[1], value);
		} else {
			throw new RuntimeException("Bad regex name / or cannot access lookup "+name);
		}
	}

	protected abstract String getNamePrefix();

	private String[] getNameParts(String name) {
		String[] names = name.split(CMLConstants.S_BACKSLASH+CMLConstants.S_PERIOD);
		if (names.length == 1) {
			names = new String[]{this.getNamePrefix(), names[0]};
		}
		if (names.length != 2) {
			throw new RuntimeException("Bad regex name (must be "+
		        PageAnalyzer.NAME_PREFIX+".foo or "+DocumentAnalyzer.NAME_PREFIX+".foo) "+name);
		}
		LOG.trace("get "+names[0]+" : "+names[1]);
		return names;
	}
	
	public Map<String, Object> ensureValueMap() {
		if (this.valueMap == null) {
			LOG.trace("created valueMap for "+this);
			valueMap = new HashMap<String, Object>();
		}
		return valueMap;
	}
	
	protected abstract DocumentAnalyzer getDocumentAnalyzer();
	
	public String debugString(String title) {
		StringBuilder sb = new StringBuilder();
		sb.append(title);
		if (valueMap != null) {
			sb.append("values: "+valueMap.size()+"\n");
			for (String key : valueMap.keySet()) {
				sb.append("  "+key+" = "+valueMap.get(key)+"\n");
			}
		}
		return sb.toString();
	}
}
