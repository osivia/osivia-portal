/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.cache.services;

import java.util.Hashtable;
import java.util.Map;



/**
 * Permet d'associer un objet de synchronisation pour chaque instance de cache
 * 
 * @author jsteux
 *
 */
public class CacheSynchronizer {

    private static Map<String, CacheSynchronizer> synchronizers = new Hashtable<String, CacheSynchronizer>(500);


	public static CacheSynchronizer getSynchronizer(String key) {

		CacheSynchronizer sync = synchronizers.get(key);

		if (sync == null) {
			sync = new CacheSynchronizer();
			synchronizers.put(key, sync);
		}

		return sync;
	}

}
