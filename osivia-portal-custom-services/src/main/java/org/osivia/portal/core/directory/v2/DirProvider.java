/*
 * (C) Copyright 2016 OSIVIA (http://www.osivia.com)
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
 */
package org.osivia.portal.core.directory.v2;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.directory.v2.IDirDelegate;
import org.osivia.portal.api.directory.v2.IDirProvider;
import org.osivia.portal.api.directory.v2.IDirService;

/**
 * The directory provided handles all calls to the directory service behind dynamic proxies
 * The realization of the call is defered to a delegate which is deployed in a separated war/service
 * @author Loïc Billon
 * @since 4.4
 */
public class DirProvider implements IDirProvider {
	
	/** the handler */
	private DirHandler handler = new DirHandler();
	
	/** the delegate */
	private IDirDelegate delegate;

	/** Map of requested services identified by their types, and dynamic proxies in front of implementation */
	private Map<Class<? extends IDirService>, IDirService> proxies = new ConcurrentHashMap<Class<? extends IDirService>, IDirService>();

	/* (non-Javadoc)
	 * @see org.osivia.portal.api.directory.v2.IDirServiceProvider#getDirService(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <D extends IDirService> D getDirService(Class<D> clazz) {
		
		
		if(proxies.containsKey(clazz)) {
			return (D) proxies.get(clazz);
		}
		else {
			IDirService newProxyInstance = (IDirService) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, handler);
			proxies.put(clazz, newProxyInstance);
			
			return (D) newProxyInstance;
		}
		
	}
	
	
	public void registerDelegate(IDirDelegate delegate) {
		this.delegate = delegate;
		handler.setDelegate(delegate);
	}
	

	public void unregisterDelegate(IDirDelegate delegate) {
		if(handler.getDelegate().equals(delegate)) {
			handler.setDelegate(null);
			
			this.delegate = null;
		}
	}
	
	
	public void clearCaches() {
		if(delegate != null) {
			delegate.clearCaches();
		}
	}


    public Object getDirectoryTxManagerDelegate() {
        if (delegate != null) {
            return delegate.getDirectoryTxManagerDelegate();
        } else
            return null;
    }

}
