package org.osivia.portal.core.page.files;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.ContextImpl;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.content.spi.ContentProvider;
import org.jboss.portal.core.model.content.spi.ContentProviderRegistry;
import org.jboss.portal.core.model.content.spi.handler.ContentHandler;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.osivia.portal.api.locator.Locator;



public class ImportPortalObjectContainer extends AbstractPortalObjectContainer implements PortalObjectContainer {

	private final Log logger = LogFactory.getLog(FilesPortalObjectContainer.class);
	private ContentProviderRegistry contentProviderRegistry = Locator.findMBean(ContentProviderRegistry.class, "portal:service=ContentProviderRegistry");
	

	ImportPortalObjectContainer.ContainerContext containerContext = new ImportPortalObjectContainer.ContainerContext();
	
	Map<PortalObjectId, PortalObject> nodes = new ConcurrentHashMap<>();
	
	@Override
	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {
		
		PortalObject res = nodes.get(id);
		return res;
	}

	@Override
	public <T extends PortalObject> T getObject(PortalObjectId id, Class<T> expectedType)
			throws IllegalArgumentException {
		return null;
	}

	@Override
	public Context getContext() {
		return null;
	}

	@Override
	public Context getContext(String namespace) {
		return null;
	}

	@Override
	public Context createContext(String namespace) throws DuplicatePortalObjectException {
		
	     // Create root context if it does not exist
	     ObjectNode root = new MemoryObjectNode(containerContext, new PortalObjectId(namespace, PortalObjectPath.ROOT_PATH), namespace);
	      
		 ContextImpl ctx = new ContextImpl();
         root.setObject(ctx);
         ctx.setObjectNode(root);
         
         nodes.put(ctx.getId(), ctx);
         
         return ctx;
	}




	@Override
	public ContextImpl createRoot(String namespace) throws DuplicatePortalObjectException {

	     ObjectNode root = new MemoryObjectNode(containerContext, new PortalObjectId(namespace, PortalObjectPath.ROOT_PATH), namespace);
	      
		 ContextImpl ctx = new ContextImpl();
         root.setObject(ctx);
         ctx.setObjectNode(root);
        
         nodes.put(ctx.getId(), ctx);
        
         return ctx;
	}

	@Override
	protected ObjectNode getObjectNode(PortalObjectId id) {
		PortalObjectImpl res = (PortalObjectImpl) nodes.get(id);
		if( res != null)
			return res.getObjectNode();
		else 
			return null;
	}
	

	
	 public class ContainerContext extends AbstractPortalObjectContainer.ContainerContext
	   {
	      /**
	       */
	      public PortalObjectContainer getContainer()
	      {
	         return ImportPortalObjectContainer.this;
	      }

	      public ContentType getDefaultContentType()
	      {
	         return ContentType.PORTLET;
	      }

	      /**
	       */
	      public void destroyChild(ObjectNode node)
	      {
	      }

	      /**
	       * @throws DuplicatePortalObjectException 
	       */
	      public void createChild(ObjectNode node) throws DuplicatePortalObjectException
	      {
	    	  nodes.put(node.getObject().getId(), node.getObject());
	      }

	      /**
	       */
	      public void updated(ObjectNode node)
	      {
	      }

	      /**
	       */
	      public ContentHandler getContentHandler(ContentType contentType)
	      {
	         ContentProvider contentProvider = contentProviderRegistry.getContentProvider(contentType);

	         //
	         if (contentProvider != null)
	         {
	            return contentProvider.getHandler();
	         }
	         else
	         {
	            return null;
	         }
	      }
	   }
	
	

}
