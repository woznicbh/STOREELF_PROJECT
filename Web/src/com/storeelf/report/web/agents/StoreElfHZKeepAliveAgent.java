package com.storeelf.report.web.agents;

import org.apache.log4j.Logger;
import org.apache.shiro.session.mgt.SimpleSession;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.storeelf.report.web.Constants;

public class StoreElfHZKeepAliveAgent extends Thread {
	static final Logger						logger							= Logger.getLogger(StoreElfHZKeepAliveAgent.class);
	
	
	public StoreElfHZKeepAliveAgent() {
		Thread.currentThread().setName("STOREELF_HZ_KEEP_ALIVE");
	}

	@Override
	public void run() {
		try {

			IMap<Object, Object> STOREELF_SESSIONS = Hazelcast
					.getHazelcastInstanceByName(Constants.STOREELF_HAZELCAST_INSTANCE_NAME).getMap("STOREELF_SESSIONS");

			if (STOREELF_SESSIONS != null) {
				STOREELF_SESSIONS.put("0", new SimpleSession());
			}
		} catch (Exception e) {
			return;
		}
	}
}
