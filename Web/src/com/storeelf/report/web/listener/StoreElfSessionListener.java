package com.storeelf.report.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;

public class StoreElfSessionListener implements EntryListener<Object, Object>{
	private static final Logger logger = LoggerFactory.getLogger(StoreElfSessionListener.class);

	@Override
	public void entryAdded(EntryEvent<Object, Object> paramEntryEvent) {
		logger.debug(paramEntryEvent.getMember().getSocketAddress().getHostString()+":: Added session:"+paramEntryEvent.getKey());		
	}

	@Override
	public void entryRemoved(EntryEvent<Object, Object> paramEntryEvent) {
		logger.debug(paramEntryEvent.getMember().getSocketAddress().getHostString()+":: Removed session:"+paramEntryEvent.getKey());
	}

	@Override
	public void entryUpdated(EntryEvent<Object, Object> paramEntryEvent) {
		logger.debug(paramEntryEvent.getMember().getSocketAddress().getHostString()+":: Updated session:"+paramEntryEvent.getKey());
	}

	@Override
	public void entryEvicted(EntryEvent<Object, Object> paramEntryEvent) {
		logger.debug(paramEntryEvent.getMember().getSocketAddress().getHostString()+":: Evicted session:"+paramEntryEvent.getKey());
	}

	@Override
	public void mapEvicted(MapEvent paramMapEvent) {
		logger.debug(paramMapEvent.getMember().getSocketAddress().getHostString()+":: Map evicted:"+paramMapEvent.getName());
	}

	@Override
	public void mapCleared(MapEvent paramMapEvent) {
		logger.debug(paramMapEvent.getMember().getSocketAddress().getHostString()+":: Map cleared:"+paramMapEvent.getName());
		
	}
}
