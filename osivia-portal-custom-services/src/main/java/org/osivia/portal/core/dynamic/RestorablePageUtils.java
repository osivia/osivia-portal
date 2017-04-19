package org.osivia.portal.core.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;


public class RestorablePageUtils {

    private static String PREFIX = "_dyn_";

    private static final String TEMPLATE_ID = "templateId:";
    private static final String CMS_PATH = "cms:";

    private static final String EMPTY_VALUE = "__E__";
    private static final String NULL_VALUE = "__N__";
    
    public static boolean isRestorable(String completeName) {
        if (completeName.startsWith(PREFIX))
            return true;
        return false;
    }

    public static void restore(ControllerContext controllerContext, PortalObjectId portalId, String completeName) {

        String templateId = null;
        String cmsPath = null;
        
        ControllerCommand restoreCmd = null;

        String names[] = PortalObjectPath.LEGACY_BASE64_FORMAT.parse(completeName.substring(5));
        
        String businessName = decodePath( names[0]);
        Map<String, String> props = decodeMap(decodePath(names[2]));
        Map<String, String> params = decodeMap(decodePath(names[3]));       
        
        
        Map displayNames = null;
        Map<String, String> i18Names = decodeMap(decodePath(names[4]));     
        if( i18Names != null){
            displayNames = new HashMap();
            for( String i18Key : i18Names.keySet()){
                displayNames.put(new Locale(i18Key), i18Names.get(i18Key));
            }
        }
        
        String pageType = decodePath(names[1]);
               
        if (pageType.startsWith(TEMPLATE_ID)) {
            templateId = names[1].substring(TEMPLATE_ID.length());

            restoreCmd = new StartDynamicPageCommand(portalId.toString(PortalObjectPath.SAFEST_FORMAT), businessName, displayNames, templateId,
                    props, params);
        }
        
        if (pageType.startsWith(CMS_PATH)) {
            cmsPath = names[1].substring(CMS_PATH.length());
            restoreCmd = new CmsCommand(null, cmsPath, params, null, null, null, null, null, null, null, null);
        }
        


        try {
            if( restoreCmd != null)
                controllerContext.execute(restoreCmd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createRestorableName(ControllerContext controllerContext, String businessName, String templateId, String cmsPath, Map displayNames,
            Map<String, String> props, Map<String, String> params, Map<String, String> cmsPublicParams) {

        
        String completePageName = "";


        Map<String, String> pageParams = params;
        Map<String, String> pageProps = props;   
        Map pageDisplayName = displayNames; 
        
        
        if( cmsPath != null)   {
            // To restore cms params
            pageParams = cmsPublicParams;
            pageDisplayName = null;
            pageProps= null;
        }
        
        
        
        String names[] = new String[5];
        names[0] = encodePath(businessName);


        String pageType = null;
        if (templateId != null)
            pageType = TEMPLATE_ID + templateId;
        if (cmsPath != null)
            pageType = CMS_PATH + cmsPath;
         

        names[1] = encodePath(pageType);
        
        names[2] = encodePath(encodeMap(pageProps));
        
        names[3] = encodePath(encodeMap(pageParams));
        Map<String, String> i18Names = null;
        if (pageDisplayName != null) {
            i18Names = new HashMap<String, String>();
            for (Object key : pageDisplayName.keySet()) {
                i18Names.put(key.toString(), pageDisplayName.get(key).toString());
            }
        }
        names[4] = encodePath(encodeMap(i18Names));
     

        completePageName = PREFIX + PortalObjectPath.LEGACY_BASE64_FORMAT.toString(names, 0, names.length);

        return completePageName;

    }

    private static String encodePath(String path) {
        if (path == null)
            return NULL_VALUE;
        else if( path.length() == 0)
            return EMPTY_VALUE;
        else
            return path;
    }

    private static String decodePath(String path) {
        if (StringUtils.equals(path, NULL_VALUE))
            return null;
        if (StringUtils.equals(path, EMPTY_VALUE))
            return "";
        else
            return path;
    }


    private static String encodeMap(Map<String, String> inputMap) {
        if( inputMap == null)
            return null;

        Map<String, String> map = inputMap;
        if (map == null)
            map = new HashMap<String, String>();


        Map<String, List<String>> hProps = new HashMap<String, List<String>>();
        for (String hKey : map.keySet()) {
            java.util.List<String> lProps = new ArrayList<String>();
            lProps.add(map.get(hKey));
            hProps.put(hKey, lProps);
        }
        return PageParametersEncoder.encodeProperties(hProps);
    }


    private static Map<String, String> decodeMap(String input) {
        if( input == null)
            return null;

        Map<String, List<String>> map = PageParametersEncoder.decodeProperties(input);

        Map<String, String> hProps = new HashMap<String, String>();
        for (String hKey : map.keySet()) {
            hProps.put(hKey, map.get(hKey).get(0));
        }

        return hProps;
    }

    
    public static String getPageLogName(PortalObjectId pageId) {

        String templateId = null;
        String cmsPath = null;

        String pageName = null;

        if (pageId.getPath().getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {
            pageName = pageId.getPath().getParent().getLastComponentName();
        } else {

            pageName = pageId.getPath().getLastComponentName();
        }

        if (isRestorable(pageName)) {

            String names[] = PortalObjectPath.LEGACY_BASE64_FORMAT.parse(pageName.substring(5));

            String pageType = decodePath(names[1]);

            if (pageType.startsWith(TEMPLATE_ID)) {
                templateId = names[1].substring(TEMPLATE_ID.length());

                PortalObjectId potemplateid = PortalObjectId.parse(templateId, PortalObjectPath.SAFEST_FORMAT);
                String potemplatepath = potemplateid.toString(PortalObjectPath.CANONICAL_FORMAT);

                return "/portal" + potemplatepath;
            }

            if (pageType.startsWith(CMS_PATH)) {
                cmsPath = names[1].substring(CMS_PATH.length());
                return "/portal/cms" + cmsPath;
            }
        }

        String portalPageName = null;
        if (pageId.getPath().getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {
            portalPageName = pageId.getPath().getParent().toString();
        } else {

            portalPageName = pageId.getPath().toString();
        }
        
        return "/portal" + portalPageName;

    }
    

}
