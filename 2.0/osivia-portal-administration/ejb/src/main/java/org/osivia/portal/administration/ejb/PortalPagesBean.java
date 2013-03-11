package org.osivia.portal.administration.ejb;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.transaction.UserTransaction;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


@Name("treeBean")
@Scope(ScopeType.PAGE)
public class PortalPagesBean   {

	@In("fileUploadBean")
	private FileUploadBean mainBean;

	private TreeNode<String> pages;
	
	private String page = "none";

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	
	public void initPages()	{
		pages = null;
	}
	
	
	public static final String TAB_ORDER = "order";
	
	//TODO : factoriser le comparator avec PageUtils et mettre dans custom-lib-utils

	public static final Comparator orderComparator = new Comparator() {
		
		public int compare(Object o1, Object o2) {
			Page page1 = (Page) o1;
			Page page2 = (Page) o2;
			
			
			String orderString1 = page1.getDeclaredProperty(TAB_ORDER);
			String orderString2 = page2.getDeclaredProperty(TAB_ORDER);
			
			int order1 = 0;
			int order2 = 0;

			//
			try {
				order1 = Integer.parseInt(orderString1);

				//
				try {
					order2 = Integer.parseInt(orderString2);
				} catch (NumberFormatException e) {
					// We have window2>window1
					order2 = DynamicPageBean.DYNAMIC_PAGES_FIRST_ORDER - 1;
				}
			} catch (NumberFormatException e1) {
				try {
					

					// We have order2=0 and order1=1 and thus window1>window2
					order1 = DynamicPageBean.DYNAMIC_PAGES_FIRST_ORDER - 1;
					
					order2 = Integer.parseInt(orderString2);
				} catch (NumberFormatException e2) {
					// Orders have the same value of zero that will lead to the
					// comparison of their ids
					order2 = DynamicPageBean.DYNAMIC_PAGES_FIRST_ORDER - 1;
				}
			}

			// If order are the same we compare the id
			if (order1 == order2) {
				return page1.getId().compareTo(page2.getId());
			} else {
				return order1 - order2;
			}

		}
	};


	public TreeNode getPages() throws Exception {
		if (pages == null) {
			
			try	{
			
			
			if( mainBean.getPortail() != null && mainBean.getPortail().length() > 0)
				pages = getPortalPages();
			else	{
				// Portail vide 
				
				TreeNode portalNode = new TreeNodeImpl<PageTreeData>();
				portalNode.setData(new PageTreeData("portal","portal"));
				
				pages = portalNode;

				}
			} catch( Exception e)	{
				
				// Cas a identifier ... (probablement plus utile)

				
				TreeNode portalNode = new TreeNodeImpl<PageTreeData>();
				portalNode.setData(new PageTreeData("portal","portal"));
				
				pages = portalNode;

				
			}
				

		}

		return pages;
	}

	public TreeNode getPortalPages() throws Exception {

		TreeNode portalNode = new TreeNodeImpl<PageTreeData>();
		portalNode.setData(new PageTreeData("portal","portal"));
		
		/*TreeNode<String> portalNode = new PageBeanNode(page.getName(), "path/"+ page.getName());*/	
		

		// As we are in servlet, cache must explicitly initialized
		IDynamicObjectContainer dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class,
				"osivia:service=DynamicPortalObjectContainer");

		try {
			dynamicObjectContainer.startPersistentIteration();

			PortalObjectContainer portalObjectContainer = Locator.findMBean(PortalObjectContainer.class,
					"portal:container=PortalObject");

			Portal portal = (Portal) portalObjectContainer.getObject(PortalObjectId.parse("/" + mainBean.getPortail(),
					PortalObjectPath.CANONICAL_FORMAT));
			
			SortedSet<Page> sortedPages = new TreeSet<Page>(orderComparator);
			for (PortalObject portalObject : portal.getChildren()) {
				if (portalObject instanceof Page)
					sortedPages.add((Page)portalObject);
			}

			

			for (Page page : sortedPages) {
					addPage(portalNode, (Page) page);

			}
		}


		finally {
			

			dynamicObjectContainer.stopPersistentIteration();

		}

		return portalNode;
	}

	private TreeNode addPage(TreeNode<String> parentNode, Page page) throws Exception {

		// Ajout du noeud

		TreeNode pageNode = new TreeNodeImpl<PageTreeData>();
		//pageNode.setData(page.getName());
		
		String displayName = page.getDisplayName().getString(Locale.FRENCH, true);
		if (displayName == null)
			displayName = page.getName();

		
		pageNode.setData(new PageTreeData(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), displayName));
		
		/*TreeNode<String> pageNode = new PageBeanNode(page.getName(), "path/"+ page.getName());*/		
		
		parentNode.addChild(page.getName(), pageNode);

		// Création des sous-pages
		
		SortedSet<Page> sortedPages = new TreeSet<Page>(orderComparator);
		for (PortalObject child : page.getChildren()) {
			if (child instanceof Page) {
				sortedPages.add( (Page) child);
			}
		}
		
		for (Page child : sortedPages) {
			addPage(pageNode, (Page) child);
		}
	
		
		return pageNode;

	}
	
	
}
