package org.osivia.portal.api.contribution;

import org.osivia.portal.api.context.PortalControllerContext;


public interface IContributionService {
    
    static final String MBEAN_NAME = "osivia:service=ContributionService";
    
    public static final String PUBLISH = "publish";
    public static final String UNPUBLISH = "unpublish";
    
    /**
     * Window edition state types enumeration.
     *
     * @author JS Steux
     */
    public class EditionState {

        
        public static String CONTRIBUTION_MODE_ONLINE = "o";
        public static String CONTRIBUTION_MODE_EDITION = "e";

        

        private final String contributionMode;
        private final String docPath;

      
        
        public String getContributionMode() {
            return contributionMode;
        }
       
        public String getDocPath() {
            return docPath;
        }

        public EditionState(String contributionMode,String docPath) {
            this.contributionMode = contributionMode;
            this.docPath = docPath;
        }
        
        public String getStringValue()  {
            return contributionMode + docPath;
        }
        
        public static EditionState fromString( String s)  {
            String contributionMode = s.substring(0,1);
            String docPath = s.substring(1);
            return new EditionState(contributionMode, docPath);
        }

       
        
   }
    
    
    /**
     * Change current window state
     * 
     * @param portalControllerContext
     * @param state
     * @return
     */
    public String getChangeEditionStateUrl(PortalControllerContext portalControllerContext,  EditionState state)  ;
    
    /**
     * return a publishing url for current doc
     * 
     * @param portalControllerContext
     * @param docPath
     * @return
     */
    
    public String getPublishContributionUrl(PortalControllerContext portalControllerContext, String docPath)  ;
    
    /**
     * get current window state
     * 
     * @param portalControllerContext
     * @return
     */
    public EditionState getEditionState(PortalControllerContext portalControllerContext);
    
    /**
     * remove current window state
     * 
     * @param portalControllerContext
     * @return
     */
    public void removeWindowEditionState(PortalControllerContext portalControllerContext) ;

}
