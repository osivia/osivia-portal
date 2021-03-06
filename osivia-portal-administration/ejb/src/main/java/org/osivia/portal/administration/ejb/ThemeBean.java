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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;

import javax.faces.model.SelectItem;

import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.administration.util.AdministrationConstants;

/**
 * Theme bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractAdministrationBean
 */
@Name("themeBean")
@Scope(ScopeType.PAGE)
public class ThemeBean extends AbstractAdministrationBean {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Default theme name. */
    private static final String DEFAULT_THEME_NAME = "default";

    /** Theme service. */
    private ThemeService themeService;
    /** Portal selected theme. */
    private String theme;
    /** Portal themes list. */
    private List<SelectItem> themes;


    /**
     * Default constructor.
     */
    public ThemeBean() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Create
    @Override
    public void init() {
        super.init();
        this.themeService = (ThemeService) this.getPortletContext().getAttribute(AdministrationConstants.THEME_SERVICE_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable observable, Object arg) {
        Portal portal = this.getPortal();
        this.theme = portal.getDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME);

        // Portal themes list
        Collection<PortalTheme> portalThemes = this.themeService.getThemes();
        this.themes = new ArrayList<SelectItem>(portalThemes.size() + 1);
        for (PortalTheme portalTheme : portalThemes) {
            String name = portalTheme.getThemeInfo().getName();
            this.themes.add(new SelectItem(name));
        }
        this.themes.add(new SelectItem(DEFAULT_THEME_NAME));
    }


    /**
     * Update theme action.
     */
    public void updateTheme() {
        Portal portal = this.getPortal();
        portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, this.theme);
        this.setMessages("Thème enregistré avec succès.");
    }


    /**
     * Getter for theme.
     *
     * @return the theme
     */
    public String getTheme() {
        return this.theme;
    }

    /**
     * Setter for theme.
     *
     * @param theme the theme to set
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * Getter for themes.
     *
     * @return the themes
     */
    public List<SelectItem> getThemes() {
        return this.themes;
    }

    /**
     * Setter for themes.
     *
     * @param themes the themes to set
     */
    public void setThemes(List<SelectItem> themes) {
        this.themes = themes;
    }

}
