package org.osivia.portal.core.assistantpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jboss.portal.common.util.ListMap;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeTools;

public class MoveWindowCommand extends AssistantCommand {

    /** Up move action name. */
    public static final String UP = "up";
    /** Down move action name. */
    public static final String DOWN = "down";
    /** Previous region move action name. */
    public static final String PREVIOUS_REGION = "previousRegion";
    /** Next region move action name. */
    public static final String NEXT_REGION = "nextRegion";


    private String moveAction;

    private String windowId;

    
    public MoveWindowCommand(String windowId, String moveAction) throws IllegalArgumentException {
        this.windowId = windowId;
        this.moveAction = moveAction;
    }


    public ControllerResponse executeAssistantCommand() throws Exception {
        int toPos;
        String toRegion;
        Window target;

        // Récupération window
        PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
        target = (Window) getControllerContext().getController().getPortalObjectContainer().getObject(poid);
        String regionId = target.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
        int wOrder = Integer.parseInt(target.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER));

        PortalObject page = target.getParent();

        // Initialisation destination par défaut
        toPos = wOrder;
        toRegion = regionId;

        if (UP.equals(moveAction) || DOWN.equals(moveAction)) {
            // Mémorisation des fenêtres de la région courante, pour gérer le déplacement
            SortedMap<Integer, Window> regionWindows = new TreeMap<Integer, Window>();
            for (PortalObject po : (Collection<PortalObject>) page.getChildren(Page.WINDOW_MASK)) {

                Window win = (Window) po;
                String region = win.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
                if (region != null && region.equals(regionId)) {

                    int order = 0;
                    String windowOrder = win.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER);
                    if (windowOrder != null) {
                        order = Integer.parseInt(win.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER));
                        regionWindows.put(order, win);
                    }
                }
            }

            List<Integer> orders = new ArrayList<Integer>(regionWindows.keySet());

            // recherche de la fenêtre courante
            int indiceOrdre = -1, i = 0;

            for (Integer iOrdre : orders) {
                if (iOrdre.equals(wOrder))
                    indiceOrdre = i;
                i++;
            }

            if (UP.equals(moveAction)) {
                if (indiceOrdre > 0) {
                    toPos = indiceOrdre - 1;
                }
            }

            if (DOWN.equals(moveAction)) {
                if (indiceOrdre < orders.size() - 1) {
                    toPos = indiceOrdre + 1;
                }
            }


            String debug = "order=";
            for (Integer order : orders) {
                debug += " " + order;
            }

            debug += "toPos=" + toPos;

            logger.debug(debug);
        }

        if (PREVIOUS_REGION.equals(moveAction) || NEXT_REGION.equals(moveAction)) {
            PageService ps = getControllerContext().getController().getPageService();
            LayoutService ls = ps.getLayoutService();

            String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
            PortalLayout pageLayout = ls.getLayout(layoutId, true);

            List<?> regionNames = pageLayout.getLayoutInfo().getRegionNames();
            String windowRegion = target.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
            int indiceRegionCourante = -1;
            int i = 0;

            for (Object regionName : regionNames) {
                if (regionName.equals(windowRegion))
                    indiceRegionCourante = i;
                i++;
            }

            if (PREVIOUS_REGION.equals(moveAction)) {
                if (indiceRegionCourante > 0) {
                    toRegion = (String) regionNames.get(indiceRegionCourante - 1);
                    toPos = 0;
                }
            }

            if (NEXT_REGION.equals(moveAction)) {
                if (indiceRegionCourante < regionNames.size() - 1) {
                    toRegion = (String) regionNames.get(indiceRegionCourante + 1);
                    toPos = 0;
                }
            }
        }

        String fromRegion = target.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);

        // First relayout all windows correctly except the target window
        ListMap<String, Window> blah = new ListMap<String, Window>(tmp);
        for (Iterator<?> i = page.getChildren(PortalObject.WINDOW_MASK).iterator(); i.hasNext();) {
            Window window = (Window) i.next();
            if (!window.equals(target)) {
                String region = window.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
                if (region != null) {
                    blah.put(region, window);
                }
            }
        }

        //
        for (Iterator<String> i = blah.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();

            //
            boolean processFrom = key.equals(fromRegion);
            boolean processTo = key.equals(toRegion);

            //
            if (!processFrom && !processTo) {
                int order = 0;
                for (Iterator<Window> j = blah.iterator(key); j.hasNext();) {
                    Window window = (Window) j.next();
                    window.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER, Integer.toString(order++));
                }
            } else {
                if (processFrom) {
                    int order = 0;
                    for (Iterator<Window> j = blah.iterator(key); j.hasNext();) {
                        Window window = (Window) j.next();

                        //
                        if (window == target) {
                            order--;
                        } else {
                            window.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER, Integer.toString(order++));
                        }
                    }
                }
                if (processTo) {
                    int order = 0;
                    for (Iterator<Window> j = blah.iterator(key); j.hasNext();) {
                        Window window = (Window) j.next();

                        //
                        if (order == toPos) {
                            order++;
                        }

                        //
                        window.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER, Integer.toString(order++));
                    }
                }
            }
        }

        //
        target.setDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION, toRegion);
        target.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER, Integer.toString(toPos));

        //
        return new UpdatePageResponse(page.getId());
    }

    
    private static final Comparator<Window> tmp = new Comparator<Window>() {

        public int compare(Window window1, Window window2) {
            String order1 = window1.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER);
            String order2 = window2.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER);
            return ThemeTools.compareWindowOrder(order1, window1.getName(), order2, window2.getName());
        }
        
    };
    

    public String getMoveAction() {
        return moveAction;
    }

    public void setMoveAction(String moveAction) {
        this.moveAction = moveAction;
    }

    public String getWindowId() {
        return windowId;
    }

    public void setWindowId(String windowId) {
        this.windowId = windowId;
    }
    
}
