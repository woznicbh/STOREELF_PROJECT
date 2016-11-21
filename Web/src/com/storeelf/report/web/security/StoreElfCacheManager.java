package com.storeelf.report.web.security;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import com.google.common.cache.CacheBuilder;
import com.storeelf.report.web.Constants;

/**
 * @author tkmagh4
 *
 * StoreElf users, roles and permissions Cache manager
 * @param <V>
 *
 */
public class StoreElfCacheManager<K,V> extends AbstractCacheManager implements org.apache.shiro.cache.CacheManager {
	private LinkedList<String>					activeCaches= new LinkedList<String>();
	private Map<String, com.google.common.cache.Cache<Object,Object>>	backingMaps	= new HashMap<String,  com.google.common.cache.Cache<Object,Object>>();
	private Map<String, StoreElfCache>			cacheMap	= new HashMap<String, StoreElfCache>();
	static final Logger							logger		= Logger.getLogger(StoreElfCacheManager.class);
	
	public static Logger getLogger() {
		return logger;
	}
	
	public void setActiveCaches(LinkedList<String> activeCaches) {
		this.activeCaches = activeCaches;
	}	
	
	public Map<String,  com.google.common.cache.Cache<Object,Object>> getBackingMaps() {
		return backingMaps;
	}
	
	public void setBackingMaps(Map<String,  com.google.common.cache.Cache<Object,Object>> backingMaps) {
		this.backingMaps = backingMaps;
	}
	
	public Map<String, StoreElfCache> getCacheMap() {
		return cacheMap;
	}
	
	public void setCacheMap(Map<String, StoreElfCache> cacheMap) {
		this.cacheMap = cacheMap;
	}

	@Override
	public StoreElfCache getCache(String pCacheName) throws CacheException {
		//TODO check for existing caches
		/*
		 * TODO use storeelf specific cache for production
		 */

		logger.debug("requesting Cache '"+pCacheName+"'");
		
		//if backing map does not exisst create one
		if(!backingMaps.containsKey(pCacheName)){
			logger.debug("Cache '"+pCacheName+"' not found, creating new one");
			
			//create ldap realm cache if it doesn't exist
			if(StringUtils.equals(pCacheName, "ldapRealm.authorizationCache")){
				backingMaps.put(pCacheName,  CacheBuilder.newBuilder()
					    .maximumSize(Constants.STOREELF_MAX_CONCURRENT_SESSIONS_CACHED)
					    .expireAfterWrite(10, TimeUnit.MINUTES)
					    .build());
			}else{
				backingMaps.put(pCacheName,  CacheBuilder.newBuilder()
					    .maximumSize(Constants.STOREELF_MAX_CONCURRENT_SESSIONS_CACHED)
					    .expireAfterWrite(10, TimeUnit.MINUTES)
					    .build());
			}
			activeCaches.add(pCacheName);
		}

		if(!cacheMap.containsKey(pCacheName)){
			cacheMap.put(pCacheName, new StoreElfCache(pCacheName, backingMaps.get(pCacheName)));
		}

		return cacheMap.get(pCacheName);
		//return cache;
	}
	
	public boolean containsBackingCache(String backingCacheName){
		return backingMaps.containsKey(backingCacheName);				
	}
	
	public boolean containsCache(String pCacheName){
		return cacheMap.containsKey(pCacheName);				
	}

	//get list of active cache currently used
	public LinkedList<String> getActiveCaches(){
		return this.activeCaches;
	}

	@Override
	protected Cache createCache(String name) throws CacheException {
		// TODO Auto-generated method stub
		return new StoreElfCache(name, CacheBuilder.newBuilder()
			    .maximumSize(Constants.STOREELF_MAX_CONCURRENT_SESSIONS_CACHED)
			    .expireAfterWrite(10, TimeUnit.MINUTES)
			    .build());
	}
	
	public void clearPermissionsCache(){
		try{
			if(containsCache("ldapRealm.resolvedPermissionsCache")){
				cacheMap.remove("ldapRealm.resolvedPermissionsCache");
				activeCaches.remove("ldapRealm.resolvedPermissionsCache");
				backingMaps.remove("ldapRealm.resolvedPermissionsCache");
			}
		}catch(Exception e){
			logger.error("encountered an issue clearing the HZ cache map");
		}
	}
}
