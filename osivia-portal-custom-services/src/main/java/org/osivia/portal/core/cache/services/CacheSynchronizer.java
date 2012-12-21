package org.osivia.portal.core.cache.services;

import java.util.HashMap;
import java.util.Map;



/**
 * Permet d'associer un objet de synchronisation pour chaque instance de cache
 * 
 * @author jsteux
 *
 */
public class CacheSynchronizer {

	private static Map<String, CacheSynchronizer> synchronizers = new HashMap<String, CacheSynchronizer>(500);

	public static CacheSynchronizer getSynchronizer(String key) {

		CacheSynchronizer sync = synchronizers.get(key);

		if (sync == null) {
			sync = new CacheSynchronizer();
			synchronizers.put(key, sync);
		}

		return sync;
	}

}
