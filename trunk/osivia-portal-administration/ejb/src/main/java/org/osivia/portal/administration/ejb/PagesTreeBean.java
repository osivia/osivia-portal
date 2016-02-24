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
package org.osivia.portal.administration.ejb;

import java.util.Locale;
import java.util.Observable;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.core.portalobjects.PortalObjectOrderComparator;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

/**
 * Pages tree bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractAdministrationBean
 */
@Name("pagesTreeBean")
@Scope(ScopeType.PAGE)
public class PagesTreeBean extends AbstractAdministrationBean {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Page tree data root identifier and name. */
    private static final String PAGE_TREE_DATA_ROOT = "portal";

    /** Page. */
    private String page;
    /** Pages tree. */
    private TreeNode<PageTreeData> pagesTree;


    /**
     * Default constructor.
     */
    public PagesTreeBean() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Create
    @Override
    public void init() {
        super.init();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Observable observable, Object arg) {
        this.pagesTree = null;
    }


    /**
     * Get pages tree.
     *
     * @return pages tree
     */
    public TreeNode<PageTreeData> getPagesTree() {
        if (this.pagesTree == null) {
            this.pagesTree = new TreeNodeImpl<PageTreeData>();
            this.pagesTree.setData(new PageTreeData(PAGE_TREE_DATA_ROOT, PAGE_TREE_DATA_ROOT));

            // As we are in servlet, cache must explicitly initialized
            this.getDynamicObjectContainer().startPersistentIteration();

            Portal portal = this.getPortal();

            if (portal != null) {
                SortedSet<Page> sortedPages = new TreeSet<Page>(PortalObjectOrderComparator.getInstance());
                for (PortalObject portalObject : portal.getChildren()) {
                    if (portalObject instanceof Page) {
                        Page page = (Page) portalObject;
                        sortedPages.add(page);
                    }
                }
                for (Page page : sortedPages) {
                    this.addPageNode(this.pagesTree, page);
                }
            }

            this.getDynamicObjectContainer().stopPersistentIteration();
        }
        return this.pagesTree;
    }

    /**
     * Utility method used to recursively add page node.
     *
     * @param parentNode parent node
     * @param page page to add
     */
    private void addPageNode(TreeNode<PageTreeData> parentNode, Page page) {
        TreeNode<PageTreeData> pageNode = new TreeNodeImpl<PageTreeData>();

        String pageId = page.getId().toString(PortalObjectPath.SAFEST_FORMAT);

        String displayName = page.getDisplayName().getString(Locale.FRENCH, true);
        if (StringUtils.isBlank(displayName)) {
            displayName = page.getName();
        }

        PageTreeData data = new PageTreeData(pageId, displayName);
        pageNode.setData(data);

        parentNode.addChild(pageId, pageNode);

        SortedSet<Page> sortedSubPages = new TreeSet<Page>(PortalObjectOrderComparator.getInstance());
        for (PortalObject portalObject : page.getChildren()) {
            if (portalObject instanceof Page) {
                Page subPage = (Page) portalObject;
                sortedSubPages.add(subPage);
            }
        }
        for (Page subPage : sortedSubPages) {
            this.addPageNode(pageNode, subPage);
        }
    }


    /**
     * Getter for page.
     *
     * @return the page
     */
    public String getPage() {
        return this.page;
    }

    /**
     * Setter for page.
     *
     * @param page the page to set
     */
    public void setPage(String page) {
        this.page = page;
    }

}
