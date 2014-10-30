package org.osivia.portal.core.customization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Project customization configuration implementation test class.
 *
 * @author Cédric Krommenhoek
 * @see ProjectCustomizationConfiguration
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Locator.class)
public class ProjectCustomizationConfigurationTest {

    /** Project customization configuration. */
    private ProjectCustomizationConfiguration configuration;

    /** CMS service. */
    private ICMSService cmsServiceMock;
    /** Page navigational state mock. */
    private PageNavigationalState pageNavigationalStateMock;
    /** Portal controller context mock. */
    private PortalControllerContext portalControllerContextMock;
    /** Portal page mock. */
    private Page pageMock;


    @Before
    public void setUp() {
        String path = "/domain/page/path";

        // CMS service
        this.cmsServiceMock = EasyMock.createMock("CMSService", ICMSService.class);
        EasyMock.replay(this.cmsServiceMock);

        // CMS service locator
        ICMSServiceLocator cmsServiceLocatorMock = EasyMock.createNiceMock(ICMSServiceLocator.class);
        EasyMock.expect(cmsServiceLocatorMock.getCMSService()).andStubReturn(this.cmsServiceMock);
        EasyMock.replay(cmsServiceLocatorMock);

        // Locator
        PowerMock.mockStatic(Locator.class);
        EasyMock.expect(Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator")).andStubReturn(cmsServiceLocatorMock);

        // Page navigation state
        this.pageNavigationalStateMock = EasyMock.createMock("PageNavigationalState", PageNavigationalState.class);
        EasyMock.expect(this.pageNavigationalStateMock.getParameter(EasyMock.anyObject(QName.class))).andReturn(new String[]{path}).anyTimes();
        EasyMock.replay(this.pageNavigationalStateMock);

        // Navigational state context
        NavigationalStateContext navigationalStateContextMock = EasyMock.createNiceMock(NavigationalStateContext.class);
        EasyMock.expect(navigationalStateContextMock.getPageNavigationalState(EasyMock.anyObject(String.class))).andStubReturn(this.pageNavigationalStateMock);
        EasyMock.replay(navigationalStateContextMock);

        // Controller context
        ControllerContext controllerContextMock = EasyMock.createNiceMock(ControllerContext.class);
        EasyMock.expect(controllerContextMock.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE)).andStubReturn(navigationalStateContextMock);
        EasyMock.replay(controllerContextMock);

        // Portal controller context
        this.portalControllerContextMock = EasyMock.createMock("PortalControllerContext", PortalControllerContext.class);
        EasyMock.expect(this.portalControllerContextMock.getControllerCtx()).andStubReturn(controllerContextMock);
        EasyMock.replay(this.portalControllerContextMock);

        // Portal object identifier
        PortalObjectId portalObjectId = PortalObjectId.parse(StringUtils.EMPTY, path, PortalObjectPath.CANONICAL_FORMAT);

        // Portal page
        this.pageMock = EasyMock.createMock("Page", Page.class);
        EasyMock.expect(this.pageMock.getId()).andReturn(portalObjectId).anyTimes();
        EasyMock.expect(this.pageMock.getProperty("osivia.cms.basePath")).andReturn(path).anyTimes();
        EasyMock.replay(this.pageMock);


        PowerMock.replayAll();


        // Object under test
        this.configuration = new ProjectCustomizationConfiguration(this.portalControllerContextMock, this.pageMock);
    }


    @Test
    public final void testEqualsCMSPath() {
        // CMS path
        String cmsPath = "/domain/cms/path";


        // Test 1 : equals
        String[] sPath1 = new String[]{cmsPath};
        EasyMock.reset(this.pageNavigationalStateMock);
        EasyMock.expect(this.pageNavigationalStateMock.getParameter(EasyMock.anyObject(QName.class))).andReturn(sPath1);
        EasyMock.replay(this.pageNavigationalStateMock);

        boolean result1 = this.configuration.equalsCMSPath(cmsPath);
        assertTrue(result1);


        // Test 2 : not equals
        String[] sPath2 = new String[]{"/domain/cms/other/path"};
        EasyMock.reset(this.pageNavigationalStateMock);
        EasyMock.expect(this.pageNavigationalStateMock.getParameter(EasyMock.anyObject(QName.class))).andReturn(sPath2);
        EasyMock.replay(this.pageNavigationalStateMock);

        boolean result2 = this.configuration.equalsCMSPath(cmsPath);
        assertFalse(result2);


        // Test 3 : no CMS path
        String[] sPath3 = null;
        EasyMock.reset(this.pageNavigationalStateMock);
        EasyMock.expect(this.pageNavigationalStateMock.getParameter(EasyMock.anyObject(QName.class))).andReturn(sPath3);
        EasyMock.replay(this.pageNavigationalStateMock);

        boolean result3 = this.configuration.equalsCMSPath(cmsPath);
        assertFalse(result3);
    }


    @Test
    public final void testEqualsWebId() throws CMSException {
        String domainId = "domain";
        String webId = "page-to-redirect";

        // CMS item
        CMSItem cmsItemMock = EasyMock.createMock("CMSItem", CMSItem.class);
        EasyMock.replay(cmsItemMock);

        EasyMock.reset(this.cmsServiceMock);
        EasyMock.expect(
                this.cmsServiceMock.getPortalNavigationItem(EasyMock.anyObject(CMSServiceCtx.class), EasyMock.anyObject(String.class),
                        EasyMock.anyObject(String.class))).andStubReturn(cmsItemMock);
        EasyMock.replay(this.cmsServiceMock);


        // Test 1 : equals
        EasyMock.reset(cmsItemMock);
        EasyMock.expect(cmsItemMock.getWebId()).andReturn(webId).anyTimes();
        EasyMock.expect(cmsItemMock.getDomainId()).andReturn(domainId).anyTimes();
        EasyMock.replay(cmsItemMock);

        boolean result1 = this.configuration.equalsWebId(webId);
        assertTrue(result1);
        boolean result1bis = this.configuration.equalsWebId(domainId, webId);
        assertTrue(result1bis);


        // Test 2 : not equals
        EasyMock.reset(cmsItemMock);
        EasyMock.expect(cmsItemMock.getWebId()).andReturn("other-page").anyTimes();
        EasyMock.expect(cmsItemMock.getDomainId()).andReturn("other-domain").anyTimes();
        EasyMock.replay(cmsItemMock);

        boolean result2 = this.configuration.equalsWebId(webId);
        assertFalse(result2);
        boolean result2bis = this.configuration.equalsWebId(domainId, webId);
        assertFalse(result2bis);


        // Test 3 : empty domain
        EasyMock.reset(cmsItemMock);
        EasyMock.expect(cmsItemMock.getWebId()).andReturn(webId).anyTimes();
        EasyMock.expect(cmsItemMock.getDomainId()).andReturn(null).anyTimes();
        EasyMock.replay(cmsItemMock);

        boolean result3 = this.configuration.equalsWebId(webId);
        assertTrue(result3);
        boolean result3bis = this.configuration.equalsWebId(domainId, webId);
        assertFalse(result3bis);


        // Test 4 : wrong web identifier
        EasyMock.reset(cmsItemMock);
        EasyMock.expect(cmsItemMock.getWebId()).andReturn("other-page").anyTimes();
        EasyMock.expect(cmsItemMock.getDomainId()).andReturn(domainId).anyTimes();
        EasyMock.replay(cmsItemMock);

        boolean result4 = this.configuration.equalsWebId(webId);
        assertFalse(result4);
        boolean result4bis = this.configuration.equalsWebId(domainId, webId);
        assertFalse(result4bis);


        // Test 5 : wrong domain
        EasyMock.reset(cmsItemMock);
        EasyMock.expect(cmsItemMock.getWebId()).andReturn(webId).anyTimes();
        EasyMock.expect(cmsItemMock.getDomainId()).andReturn("other-domain").anyTimes();
        EasyMock.replay(cmsItemMock);

        boolean result5 = this.configuration.equalsWebId(webId);
        assertTrue(result5);
        boolean result5bis = this.configuration.equalsWebId(domainId, webId);
        assertFalse(result5bis);
    }


    @Test
    public final void testSetRedirectionURL() {
        String redirectionURL = "/domain/redirection/url";

        // Call
        this.configuration.setRedirectionURL(redirectionURL);

        assertEquals(redirectionURL, this.configuration.getRedirectionURL());
    }

}
