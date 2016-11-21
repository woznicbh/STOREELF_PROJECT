package com.storeelf.report.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import com.storeelf.report.web.Constants;
import com.storeelf.report.web.model.SQLModel;

public class StoreElfModelMapListener implements EntryListener<String, SQLModel>{
	private static final Logger logger = LoggerFactory.getLogger(StoreElfModelMapListener.class);

	@Override
	public void entryAdded(EntryEvent<String, SQLModel> paramEntryEvent) {
		logger.debug(paramEntryEvent.getMember().getSocketAddress().getHostString()+":: Added model:"+paramEntryEvent.getKey());
		Constants.STOREELF_SQLMODEL_MAP.put(paramEntryEvent.getKey(), paramEntryEvent.getValue());
	}

	@Override
	public void entryRemoved(EntryEvent<String, SQLModel> paramEntryEvent) {
		logger.debug(paramEntryEvent.getMember().getSocketAddress().getHostString()+":: Removed model:"+paramEntryEvent.getKey());
		Constants.STOREELF_SQLMODEL_MAP.remove(paramEntryEvent.getKey());
	}

	@Override
	public void entryUpdated(EntryEvent<String, SQLModel> paramEntryEvent) {
		try{
		logger.debug(paramEntryEvent.getMember().getSocketAddress().getHostString()+":: Updated model:"+paramEntryEvent.getKey());
		Constants.STOREELF_SQLMODEL_MAP.replace(paramEntryEvent.getKey(), paramEntryEvent.getValue());
		}catch(Exception e){
			logger.error("SQLMODEL_MAP entryUpdated() failed");
		}
	}

	@Override
	public void entryEvicted(EntryEvent<String, SQLModel> paramEntryEvent) {
		logger.debug(paramEntryEvent.getMember().getSocketAddress().getHostString()+":: Evicted model:"+paramEntryEvent.getKey());
		Constants.STOREELF_SQLMODEL_MAP.remove(paramEntryEvent.getKey());
	}

	@Override
	public void mapEvicted(MapEvent paramMapEvent) {
		logger.debug(paramMapEvent.getMember().getSocketAddress().getHostString()+":: Map evicted:"+paramMapEvent.getName());
		Constants.STOREELF_SQLMODEL_MAP.clear();
	}

	@Override
	public void mapCleared(MapEvent paramMapEvent) {
		logger.debug(paramMapEvent.getMember().getSocketAddress().getHostString()+":: Map cleared:"+paramMapEvent.getName());
		Constants.STOREELF_SQLMODEL_MAP.clear();		
	}
}
