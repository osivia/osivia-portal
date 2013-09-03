package org.osivia.portal.core.assistantpage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jboss.portal.common.util.ListMap;
import org.jboss.portal.core.controller.ControllerResponse;
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

/**
 * Move window command.
 *
 * @see AssistantCommand
 */
public class MoveWindowCommand extends AssistantCommand {

    /** Up move action name. */
    public static final String UP = "up";
    /** Down move action name. */
    public static final String DOWN = "down";
    /** Previous region move action name. */
    public static final String PREVIOUS_REGION = "previousRegion";
    /** Next region move action name. */
    public static final String NEXT_REGION = "nextRegion";

    /** Move action. */
    private final String moveAction;
    /** Window identifier. */
    private final String windowId;


    /** Window comparator. */
    private static final Comparator<Window> windowComparator = new Comparator<Window>() {

        /**
         * {@inheritDoc}
         */
        public int compare(Window window1, Window window2) {
            String order1 = window1.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER);
            String order2 = window2.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER);
            return ThemeTools.compareWindowOrder(order1, window1.getName(), order2, window2.getName());
        }

    };


    /**
     * Constructor.
     *
     * @param windowId window identifier
     * @param moveAction move action
     * @throws IllegalArgumentException
     */
    public MoveWindowCommand(String windowId, String moveAction) {
        this.windowId = windowId;
        this.moveAction = moveAction;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse executeAssistantCommand() throws Exception {
        int toPos;
        String toRegion;
        Window target;

        // Get window
        PortalObjectId poid = PortalObjectId.parse(this.windowId, PortalObjectPath.SAFEST_FORMAT);
        target = (Window) this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);
        String regionId = target.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
        int wOrder = Integer.parseInt(target.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER));

        PortalObject page = target.getParent();

        // Init default destination
        toPos = wOrder;
        toRegion = regionId;

        if (UP.equals(this.moveAction) || DOWN.equals(this.moveAction)) {
            toPos = this.changePosition(toPos, regionId, wOrder, page);
        }

        if (PREVIOUS_REGION.equals(this.moveAction) || NEXT_REGION.equals(this.moveAction)) {
            PageService ps = this.getControllerContext().getController().getPageService();
            LayoutService ls = ps.getLayoutService();

            String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
            PortalLayout pageLayout = ls.getLayout(layoutId, true);

            List<?> regionNames = pageLayout.getLayoutInfo().getRegionNames();
            String windowRegion = target.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
            int currentRegionIndex = -1;
            int i = 0;

            for (Object regionName : regionNames) {
                if (regionName.equals(windowRegion)) {
                    currentRegionIndex = i;
                }
                i++;
            }

            if (PREVIOUS_REGION.equals(this.moveAction)) {
                if (currentRegionIndex > 0) {
                    toRegion = (String) regionNames.get(currentRegionIndex - 1);
                    toPos = 0;
                }
            }

            if (NEXT_REGION.equals(this.moveAction)) {
                if (currentRegionIndex < (regionNames.size() - 1)) {
                    toRegion = (String) regionNames.get(currentRegionIndex + 1);
                    toPos = 0;
                }
            }
        }

        String fromRegion = target.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);

        // First relayout all windows correctly except the target window
        ListMap<String, Window> blah = new ListMap<String, Window>(windowComparator);
        for (Object name : page.getChildren(PortalObject.WINDOW_MASK)) {
            Window window = (Window) name;
            if (!window.equals(target)) {
                String region = window.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
                if (region != null) {
                    blah.put(region, window);
                }
            }
        }

        //
        for (String string : blah.keySet()) {
            String key = string;

            //
            boolean processFrom = key.equals(fromRegion);
            boolean processTo = key.equals(toRegion);

            //
            if (!processFrom && !processTo) {
                int order = 0;
                for (Iterator<Window> j = blah.iterator(key); j.hasNext();) {
                    Window window = j.next();
                    window.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER, Integer.toString(order++));
                }
            } else {
                if (processFrom) {
                    int order = 0;
                    for (Iterator<Window> j = blah.iterator(key); j.hasNext();) {
                        Window window = j.next();

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
                        Window window = j.next();

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


    /**
     * Utility method used to change position into region.
     *
     * @param toPos current position
     * @param regionId region identifier
     * @param wOrder window order
     * @param page current page
     * @return new position
     */
    private int changePosition(int toPos, String regionId, int wOrder, PortalObject page) {
        int newPos = toPos;

        // Save current region windows, for move handling
        SortedMap<Integer, Window> regionWindows = new TreeMap<Integer, Window>();
        for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK)) {
            Window win = (Window) po;
            String region = win.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);
            if ((region != null) && region.equals(regionId)) {

                int order = 0;
                String windowOrder = win.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER);
                if (windowOrder != null) {
                    order = Integer.parseInt(win.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER));
                    regionWindows.put(order, win);
                }
            }
        }

        List<Integer> orders = new ArrayList<Integer>(regionWindows.keySet());

        // Search current window
        int orderIndex = -1, i = 0;
        for (Integer order : orders) {
            if (order.equals(wOrder)) {
                orderIndex = i;
            }
            i++;
        }

        if (UP.equals(this.moveAction)) {
            if (orderIndex > 0) {
                newPos = orderIndex - 1;
            }
        }

        if (DOWN.equals(this.moveAction)) {
            if (orderIndex < (orders.size() - 1)) {
                newPos = orderIndex + 1;
            }
        }


        String debug = "order=";
        for (Integer order : orders) {
            debug += " " + order;
        }

        debug += "toPos=" + newPos;

        this.getLogger().debug(debug);
        return newPos;
    }


    /**
     * Getter for moveAction.
     *
     * @return the moveAction
     */
    public String getMoveAction() {
        return this.moveAction;
    }

    /**
     * Getter for windowId.
     *
     * @return the windowId
     */
    public String getWindowId() {
        return this.windowId;
    }

}
