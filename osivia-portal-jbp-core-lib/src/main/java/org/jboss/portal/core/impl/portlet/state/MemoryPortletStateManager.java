package org.jboss.portal.core.impl.portlet.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.portlet.state.InvalidStateIdException;
import org.jboss.portal.portlet.state.NoSuchStateException;
import org.jboss.portal.portlet.state.PropertyMap;
import org.jboss.portal.portlet.state.SimplePropertyMap;
import org.jboss.portal.portlet.state.producer.PortletStatePersistenceManager;

/**
 * Stockage en mémoire des préférences des portlets
 */
public class MemoryPortletStateManager extends AbstractJBossService implements PortletStatePersistenceManager {

	private Map<String, PersistentPortletState> states;
	private long key;

	protected void startService() throws Exception {
		states = Collections.synchronizedMap(new HashMap());
		key = 0;
	}

	private synchronized long createKey()	
	{
		key++;
		return key;
	}
	@Override
	public PersistentPortletState loadState(String stateId)
			throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException {

		if (stateId == null) {
			throw new IllegalArgumentException("id cannot be null");
		}

		PersistentPortletState context = states.get(stateId);
		if (context == null)
			throw new NoSuchStateException(stateId);
		return context;
	}

	@Override
	public String createState(String portletId, PropertyMap propertyMap) throws IllegalArgumentException {
		PersistentPortletState context = new PersistentPortletState(portletId, propertyMap);
		context.setKey(createKey());
		states.put(context.getId(), context);
		return context.getId();
	}

	@Override
	public String cloneState(String stateId)
			throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException {

		if (stateId == null) {
			throw new IllegalArgumentException("id cannot be null");
		}

		//
		PersistentPortletState parentContext = loadState(stateId);

		// Create the persistent state
		PersistentPortletState context = new PersistentPortletState(parentContext.getPortletId(),
				new SimplePropertyMap(parentContext.getState().getProperties()));
		context.setKey(createKey());
		states.put(context.getId(), context);

		// Make the association
		context.setParent(parentContext);
		parentContext.getChildren().add(context);

		//
		return context.getId();
	}

	@Override
	public String cloneState(String stateId, PropertyMap propertyMap)
			throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException {
		  if (stateId == null)
	      {
	         throw new IllegalArgumentException("id cannot be null");
	      }

		  if (stateId == null)
	      {
	         throw new IllegalArgumentException("id cannot be null");
	      }
	      if (propertyMap == null)
	      {
	         throw new IllegalArgumentException("value map cannot be null");
	      }


	      PersistentPortletState parentContext = loadState( stateId);

	      // Create the persistent state
	      PersistentPortletState context = new PersistentPortletState(parentContext.getPortletId(), propertyMap);
	      context.setKey(createKey());
		  states.put(context.getId(), context);

	      // Make the association
	      context.setParent(parentContext);
	      parentContext.getChildren().add(context);

	      //
	      return context.getId();

	}

	@Override
	public void updateState(String stateId, PropertyMap propertyMap)
			throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException {

		PersistentPortletState context = loadState(stateId);

		//
		context.entries.clear();
		for (Iterator i = propertyMap.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			List<String> value = propertyMap.getProperty(key);
			PersistentPortletStateEntry entry = new PersistentPortletStateEntry(key, value);
			context.entries.put(key, entry);
		}

	}

	@Override
	public void destroyState(String stateId)
			throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException {
		 
		 if (stateId == null)
	      {
	         throw new IllegalArgumentException("No null state id accepted");
	      }

	      //

	      PersistentPortletState context = loadState( stateId);
	      
	      for(PersistentPortletState children: states.values()) {
	    	  if( children.getParent() == context)	{
	    		  children.setParent(null);
	    	  }
	    	  if( children.getChildren().contains(context))	{
	    		  children.getChildren().remove(context);
	    	  }
	      }
	      

	      states.remove(context.getId());

	}

}
