/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/

package org.osivia.portal.core.renderers;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.jboss.portal.theme.render.renderer.DecorationRenderer;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;
import org.osivia.portal.core.page.PageProperties;

/**
 * Implementation of a decoration renderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 12912 $, $LastChangedDate: 2009-02-28 11:37:59 -0500 (Sat, 28 Feb 2009) $
 * @see org.jboss.portal.theme.render.renderer.DecorationRenderer
 */
public class DivDecorationRenderer extends AbstractObjectRenderer implements DecorationRenderer {

    /**
     * Default constructor.
     */
    public DivDecorationRenderer() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void render(RendererContext rendererContext, DecorationRendererContext drc) throws RenderException {
        PrintWriter markup = rendererContext.getWriter();

        PageProperties properties = PageProperties.getProperties();
        // Current window identifier
        String currentWindowId = properties.getCurrentWindowId();

        // Render title
        this.renderTitle(properties, markup, drc);

        markup.print("<div class='btn-toolbar pull-right hidden-xs portlet-mode-container'>");
        markup.print("<div class='btn-group btn-group-xs'>");

        if ("1".equals(properties.getWindowProperty(currentWindowId, "osivia.displayDecorators"))) {
            renderTriggerableActions(rendererContext, drc, ActionRendererContext.MODES_KEY);
            renderTriggerableActions(rendererContext, drc, ActionRendererContext.WINDOWSTATES_KEY);
        }

        String closeUrl = properties.getWindowProperty(currentWindowId, "osivia.closeUrl");
        if (closeUrl != null) {
            markup.print("<a href='");
            markup.print(closeUrl);
            markup.print("' class='btn btn-default' data-toggle='tooltip' data-placement='bottom' title='close'><span class='glyphicons halflings remove'></span></a>");
        }

        markup.print("</div>");
        markup.print("</div>");
    }


    /**
     * Render portlet title.
     *
     * @param properties page properties
     * @param markup markup
     * @param drc decoration renderer context
     */
    private void renderTitle(PageProperties properties, PrintWriter markup, DecorationRendererContext drc) {
        // Current window identifier
        String currentWindowId = properties.getCurrentWindowId();
        // Title value
        String title = properties.getWindowProperty(currentWindowId, "osivia.title");
        if (title == null) {
            title = drc.getTitle();
        }
        // Mobile collapse indicator
        boolean mobileCollapse = BooleanUtils.toBoolean(properties.getWindowProperty(currentWindowId, "osivia.mobileCollapse"));
        // Bootstrap panel style indicator
        boolean bootstrapPanelStyle = BooleanUtils.toBoolean(properties.getWindowProperty(currentWindowId, "osivia.bootstrapPanelStyle"));

        markup.print("<div class=\"portlet-titlebar-decoration\"></div>");

        if (bootstrapPanelStyle) {
            markup.print("<h3 class='panel-title pull-left portlet-titlebar-title'>");
        } else {
            markup.print("<h3 class='pull-left portlet-titlebar-title'>");
        }

        if (mobileCollapse) {
            markup.print("<a href='#' class='no-ajax-link' data-toggle='collapse' data-target='#body_" + currentWindowId + "'>");
            markup.print(title);
            markup.print("</a>");
        } else {
            markup.print(title);
        }

        markup.print("</h3>");
    }



    /**
     * Render triggerable actions.
     *
     * @param ctx renderer context
     * @param drc decoration renderer context
     * @param selector selector
     */
    private static void renderTriggerableActions(RendererContext ctx, DecorationRendererContext drc, String selector) {
        Collection<?> modesOrStates = drc.getTriggerableActions(selector);
        if (modesOrStates == null) {
            return;
        }

        //
        if (modesOrStates instanceof List) {
            List<?> list = (List<?>) modesOrStates;
            Collections.sort(list, new ModeAndStateComparator());
            modesOrStates = list;
        }

        //
        for (Object name : modesOrStates) {
            ActionRendererContext action = (ActionRendererContext) name;
            String actionName = action.getName();
            if (action.isEnabled() && !"admin".equals(actionName) && !"minimized".equals(actionName)) {
                // Glyphicon
                String glyphicon = "asterisk";
                if ("maximized".equals(actionName)) {
                    glyphicon = "halflings resize-full";
                } else if ("normal".equals(actionName)) {
                    glyphicon = "halflings resize-small";
                }

                PrintWriter markup = ctx.getWriter();
                markup.print("<a href='");
                markup.print(action.getURL());
                markup.print("' class='btn btn-default' title='");
                markup.print(actionName);
                markup.print("' data-toggle='tooltip' data-placement='bottom'><span class='glyphicons ");
                markup.print(glyphicon);
                markup.print("'></span></a>");
            }
        }
    }


    /**
     * Mode and state comparator.
     *
     * @see Comparator
     */
    private static class ModeAndStateComparator implements Comparator<Object> {

        /** . */
        private final Map<Object, Integer> modeOrState2Index = new HashMap<Object, Integer>();

        /** . */
        private int lastModeIndex = 1;

        /** . */
        private int lastStateIndex = 101;


        /**
         * Constructor.
         */
        public ModeAndStateComparator() {
            this.modeOrState2Index.put(Mode.EDIT.toString(), new Integer(97));
            this.modeOrState2Index.put(Mode.HELP.toString(), new Integer(98));
            this.modeOrState2Index.put(Mode.VIEW.toString(), new Integer(99));
            this.modeOrState2Index.put(Mode.ADMIN.toString(), new Integer(100));
            this.modeOrState2Index.put(WindowState.MINIMIZED.toString(), new Integer(198));
            this.modeOrState2Index.put(WindowState.NORMAL.toString(), new Integer(199));
            this.modeOrState2Index.put(WindowState.MAXIMIZED.toString(), new Integer(200));
        }


        /**
         * {@inheritDoc}
         */
        public int compare(Object o1, Object o2) {
            ActionRendererContext action1 = (ActionRendererContext) o1;
            ActionRendererContext action2 = (ActionRendererContext) o2;

            //
            Object origin1 = action1.getName();
            Object origin2 = action2.getName();

            //
            int index1 = this.getIndexFor(origin1);
            int index2 = this.getIndexFor(origin2);

            //
            return index1 - index2;
        }


        /**
         * Get index for.
         *
         * @param origin origin
         * @return index
         */
        private int getIndexFor(Object origin) {
            Integer index = this.modeOrState2Index.get(origin);
            if (index == null) {
                index = (origin instanceof Mode) ? new Integer(this.lastModeIndex++) : new Integer(this.lastStateIndex++);
                this.modeOrState2Index.put(origin, index);
            }
            return index.intValue();
        }

    }

}
