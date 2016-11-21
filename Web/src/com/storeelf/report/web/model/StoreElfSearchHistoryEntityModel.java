package com.storeelf.report.web.model;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.storeelf.report.web.Constants;

public class StoreElfSearchHistoryEntityModel {
	private ConcurrentSkipListMap<String, HashMap<String, String>>	searchMap	= new ConcurrentSkipListMap<String, HashMap<String, String>>();
	private HashMap<String, String> 					searchParameterValueMap = new HashMap<String, String>();

	public StoreElfSearchHistoryEntityModel(){
	}

	public StoreElfSearchHistoryEntityModel add(String param, String Value){
		getSearchParameterValueMap().put(param, Value);
		return this;
	}

	public ConcurrentSkipListMap<String, HashMap<String, String>> getSearchMap() {
		return searchMap;
	}

	public HashMap<String, String> getSearchParameterValueMap() {
		return searchParameterValueMap;
	}

	public void setSearchParameterValueMap(HashMap<String, String> searchParameterValueMap) {
		this.searchParameterValueMap = searchParameterValueMap;
	}

	//save search results
	private void saveRecord(){
		if(getSearchMap().size() > Constants.STOREELF_MAX_SEARCH_HISTORY)		getSearchMap().remove(getSearchMap().firstKey());
		getSearchMap().put((new Date()).toString(), getSearchParameterValueMap());
		searchParameterValueMap = new HashMap<String, String>();
	}

	public StoreElfSearchHistoryEntityModel save(){
		saveRecord();
		return this;
	}
}
