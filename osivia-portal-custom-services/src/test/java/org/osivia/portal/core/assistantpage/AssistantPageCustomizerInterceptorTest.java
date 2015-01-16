/**
 *
 */
package org.osivia.portal.core.assistantpage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPath.Format;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.portlet.info.PortletInfoInfo;
import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.ServerRequest;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.portalobjects.DynamicPersistentPage;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.portalobjects.TemplatePage;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * AssistantPageCustomizerInterceptor tests.
 *
 * @author Cédric Krommenhoek
 * @see AssistantPageCustomizerInterceptor
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Locator.class)
public class AssistantPageCustomizerInterceptorTest {

    private static final String ID_PREFIX = "IdPrefix";
    private static final String ID_SIMPLE = "Identifier";
    private static final String ID_SPECIAL_CHARS = "éèàù@%$";
    private static final String CMS_BASE_PATH = "/cms/";
    private static final String CURRENT_PAGE_ID = "/portal/page/";
    private static final String SUB_PAGE_ID = "/portal/page/subpage/";


    /** Formatter, implemented by AssistantPageCustomizerInterceptor. */
    private IFormatter formatter;

    /** Controller context. */
    private ControllerContext context;

    /** Portal. */
    private Portal portalMock;


    /**
     * Set up.
     */
    @Before
    public void setUp() {
        this.portalMock = EasyMock.createMock("Portal", Portal.class);

        // Formatter
        PortalAuthorizationManagerFactory factoryMock = EasyMock.createNiceMock(PortalAuthorizationManagerFactory.class);
        PortalAuthorizationManager managerMock = EasyMock.createNiceMock(PortalAuthorizationManager.class);
        InstanceContainer instanceContainerMock = EasyMock.createNiceMock(InstanceContainer.class);
        IProfilManager profileManagerMock = EasyMock.createNiceMock(IProfilManager.class);
        IInternationalizationService internationalizationServiceMock = EasyMock.createNiceMock(IInternationalizationService.class);
        IBundleFactory bundleFactoryMock = EasyMock.createNiceMock(IBundleFactory.class);
        PortalObjectContainer portalObjectContainerMock = EasyMock.createNiceMock(PortalObjectContainer.class);

        Bundle bundle = new Bundle(internationalizationServiceMock, this.getClass().getClassLoader(), Locale.getDefault());

        EasyMock.expect(factoryMock.getManager()).andStubReturn(managerMock);
        EasyMock.expect(managerMock.checkPermission(EasyMock.anyObject(PortalObjectPermission.class))).andReturn(true).anyTimes();
        EasyMock.expect(instanceContainerMock.getDefinitions()).andReturn(this.generateInstanceDefinitions()).anyTimes();
        EasyMock.expect(profileManagerMock.getListeProfils()).andReturn(new ArrayList<ProfilBean>()).anyTimes();
        EasyMock.expect(internationalizationServiceMock.getString(EasyMock.anyObject(String.class), EasyMock.anyObject(Locale.class)))
                .andReturn("LOCALIZED_STRING").anyTimes();
        EasyMock.expect(internationalizationServiceMock.getBundleFactory(EasyMock.anyObject(ClassLoader.class))).andStubReturn(bundleFactoryMock);
        EasyMock.expect(bundleFactoryMock.getBundle(EasyMock.anyObject(Locale.class))).andReturn(bundle).anyTimes();
        EasyMock.expect(portalObjectContainerMock.getObject(EasyMock.anyObject(PortalObjectId.class))).andStubReturn(this.portalMock);

        AssistantPageCustomizerInterceptor assistant = new AssistantPageCustomizerInterceptor();
        assistant.setPortalAuthorizationManagerFactory(factoryMock);
        assistant.setInstanceContainer(instanceContainerMock);
        assistant.setProfileManager(profileManagerMock);
        assistant.setInternationalizationService(internationalizationServiceMock);
        assistant.setPortalObjectContainer(portalObjectContainerMock);
        this.formatter = assistant;

        EasyMock.replay(factoryMock);
        EasyMock.replay(managerMock);
        EasyMock.replay(instanceContainerMock);
        EasyMock.replay(profileManagerMock);
        EasyMock.replay(internationalizationServiceMock);
        EasyMock.replay(bundleFactoryMock);
        EasyMock.replay(portalObjectContainerMock);


        // Context
        this.context = EasyMock.createNiceMock("ControllerContext", ControllerContext.class);
        ServerInvocation serverInvocationMock = EasyMock.createNiceMock("ServerInvocation", ServerInvocation.class);
        ServerRequest requestMock = EasyMock.createNiceMock("ServerRequest", ServerRequest.class);
        ServerInvocationContext serverContextMock = EasyMock.createNiceMock("ServerContext", ServerInvocationContext.class);
        HttpServletRequest httpRequestMock = EasyMock.createNiceMock("HttpRequest", HttpServletRequest.class);
        NavigationalStateContext nsContextMock = EasyMock.createNiceMock("NavigationalStateContext", NavigationalStateContext.class);
        PageNavigationalState currentPageStateMock = EasyMock.createNiceMock("CurrentPageNavigationalState", PageNavigationalState.class);
        PageNavigationalState subPageStateMock = EasyMock.createNiceMock("SubPageNavigationalState", PageNavigationalState.class);

        EasyMock.expect(this.context.getServerInvocation()).andStubReturn(serverInvocationMock);
        EasyMock.expect(
                this.context.renderURL(EasyMock.anyObject(ControllerCommand.class), EasyMock.anyObject(URLContext.class), EasyMock.anyObject(URLFormat.class)))
                .andReturn("/url/").anyTimes();
        EasyMock.expect(this.context.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE)).andStubReturn(nsContextMock);
        EasyMock.expect(serverInvocationMock.getRequest()).andStubReturn(requestMock);
        EasyMock.expect(serverInvocationMock.getServerContext()).andStubReturn(serverContextMock);
        EasyMock.expect(requestMock.getLocale()).andReturn(Locale.FRENCH).anyTimes();
        EasyMock.expect(requestMock.getLocales()).andReturn(new Locale[]{Locale.FRENCH}).anyTimes();
        EasyMock.expect(serverContextMock.getURLContext()).andReturn(URLContext.newInstance(false, false)).anyTimes();
        EasyMock.expect(serverContextMock.getClientRequest()).andStubReturn(httpRequestMock);
        EasyMock.expect(httpRequestMock.getLocale()).andReturn(Locale.FRENCH).anyTimes();
        EasyMock.expect(nsContextMock.getPageNavigationalState(CURRENT_PAGE_ID)).andStubReturn(currentPageStateMock);
        EasyMock.expect(nsContextMock.getPageNavigationalState(SUB_PAGE_ID)).andStubReturn(subPageStateMock);
        EasyMock.expect(currentPageStateMock.getParameter(EasyMock.anyObject(QName.class))).andReturn(new String[]{CMS_BASE_PATH}).anyTimes();
        EasyMock.expect(subPageStateMock.getParameter(EasyMock.anyObject(QName.class))).andReturn(new String[]{CMS_BASE_PATH + "subpage"}).anyTimes();

        EasyMock.replay(this.context);
        EasyMock.replay(serverInvocationMock);
        EasyMock.replay(requestMock);
        EasyMock.replay(serverContextMock);
        EasyMock.replay(httpRequestMock);
        EasyMock.replay(nsContextMock);
        EasyMock.replay(currentPageStateMock);


        // Locator
        PowerMock.mockStatic(Locator.class);
        IDynamicObjectContainer dynamicObjectContainerMock = EasyMock.createNiceMock(IDynamicObjectContainer.class);

        EasyMock.expect(Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer")).andStubReturn(
                dynamicObjectContainerMock);

        PowerMock.replayAll();
        EasyMock.replay(dynamicObjectContainerMock);
    }


    /**
     * Test method for {@link AssistantPageCustomizerInterceptor#formatHTMLTreeModels(Page, ControllerContext, String)}.
     *
     * @throws IOException
     */
    @Test
    public final void testFormatHTMLTreeModels() throws IOException {
        Page currentPageMock = this.generatePortalArborescence();

        String htmlData;

        // Test 1 : null current page
        htmlData = this.formatter.formatHTMLTreeModels(null, this.context, ID_PREFIX);
        assertNull(htmlData);

        // Test 2 : null context
        htmlData = this.formatter.formatHTMLTreeModels(currentPageMock, null, ID_PREFIX);
        assertNull(htmlData);

        // Test 3 : nominal case
        htmlData = this.formatter.formatHTMLTreeModels(currentPageMock, this.context, ID_PREFIX);
        assertNotNull(htmlData);
        assertEquals(1, StringUtils.countMatches(htmlData, "<ul"));
        assertEquals(1, StringUtils.countMatches(htmlData, "<li"));
        assertEquals(1, StringUtils.countMatches(htmlData, "<a"));
        assertEquals(1, StringUtils.countMatches(htmlData, "id=\"" + ID_PREFIX));
    }


    /**
     * Test case for {@link AssistantPageCustomizerInterceptor#formatHTMLTreePageParent(Page, ControllerContext, String)}.
     *
     * @throws IOException
     */
    @Test
    public final void testFormatHTMLTreePageParent() throws IOException {
        Page currentPageMock = this.generatePortalArborescence();

        String htmlData;

        // Test 1 : null current page
        htmlData = this.formatter.formatHTMLTreePageParent(null, this.context, ID_PREFIX);
        assertNull(htmlData);

        // Test 2 : null context
        htmlData = this.formatter.formatHTMLTreePageParent(currentPageMock, null, ID_PREFIX);
        assertNull(htmlData);

        // Test 3 : nominal case
        htmlData = this.formatter.formatHTMLTreePageParent(currentPageMock, this.context, ID_PREFIX);
        assertNotNull(htmlData);
        assertEquals(2, StringUtils.countMatches(htmlData, "<ul"));
        assertEquals(2, StringUtils.countMatches(htmlData, "<li"));
        assertEquals(2, StringUtils.countMatches(htmlData, "<a"));
        assertEquals(2, StringUtils.countMatches(htmlData, "id=\"" + ID_PREFIX));
    }


    /**
     * Test case for {@link AssistantPageCustomizerInterceptor#formatHTMLTreeTemplateParent(Page, ControllerContext, String)}.
     *
     * @throws IOException
     */
    @Test
    public final void testFormatHTMLTreeTemplateParent() throws IOException {
        Page currentPageMock = this.generatePortalArborescence();

        String htmlData;

        // Test 1 : null current page
        htmlData = this.formatter.formatHTMLTreeTemplateParent(null, this.context, ID_PREFIX);
        assertNull(htmlData);

        // Test 2 : null context
        htmlData = this.formatter.formatHTMLTreeTemplateParent(currentPageMock, null, ID_PREFIX);
        assertNull(htmlData);

        // Test 3 : nominal case
        htmlData = this.formatter.formatHTMLTreeTemplateParent(currentPageMock, this.context, ID_PREFIX);
        assertNotNull(htmlData);
        assertEquals(StringUtils.EMPTY, htmlData);
    }


    /**
     * Test case for {@link AssistantPageCustomizerInterceptor#formatHTMLTreePortalObjectsMove(Page, ControllerContext, String)}.
     *
     * @throws IOException
     */
    @Test
    public final void testFormatHTMLTreePortalObjectsMove() throws IOException {
        Page currentPageMock = this.generatePortalArborescence();

        String htmlData;

        // Test 1 : null current page
        htmlData = this.formatter.formatHTMLTreePortalObjectsMove(null, this.context, ID_PREFIX);
        assertNull(htmlData);

        // Test 2 : null context
        htmlData = this.formatter.formatHTMLTreePortalObjectsMove(currentPageMock, null, ID_PREFIX);
        assertNull(htmlData);

        // Test 3 : nominal case
        htmlData = this.formatter.formatHTMLTreePortalObjectsMove(currentPageMock, this.context, ID_PREFIX);
        assertNotNull(htmlData);
        assertEquals(2, StringUtils.countMatches(htmlData, "<ul"));
        assertEquals(5, StringUtils.countMatches(htmlData, "<li"));
        assertEquals(5, StringUtils.countMatches(htmlData, "<a"));
        assertEquals(5, StringUtils.countMatches(htmlData, "id=\"" + ID_PREFIX));
    }


    /**
     * Test case for {@link AssistantPageCustomizerInterceptor#formatHTMLTreePortalObjectsAlphaOrder(Page, ControllerContext, String)}.
     *
     * @throws IOException
     */
    @Test
    public final void testFormatHTMLTreePortalObjectsAlphaOrder() throws IOException {
        Page currentPageMock = this.generatePortalArborescence();

        String htmlData;

        // Test 1 : null current page
        htmlData = this.formatter.formatHTMLTreePortalObjectsAlphaOrder(null, this.context, ID_PREFIX);
        assertNull(htmlData);

        // Test 2 : null context
        htmlData = this.formatter.formatHTMLTreePortalObjectsAlphaOrder(currentPageMock, null, ID_PREFIX);
        assertNull(htmlData);

        // Test 3 : nominal case
        htmlData = this.formatter.formatHTMLTreePortalObjectsAlphaOrder(currentPageMock, this.context, ID_PREFIX);
        assertNotNull(htmlData);
        assertEquals(2, StringUtils.countMatches(htmlData, "<ul"));
        assertEquals(3, StringUtils.countMatches(htmlData, "<li"));
        assertEquals(3, StringUtils.countMatches(htmlData, "<a"));
        assertEquals(3, StringUtils.countMatches(htmlData, "id=\"" + ID_PREFIX));
    }


    /**
     * Test method for {@link AssistantPageCustomizerInterceptor#formatHtmlSafeEncodingId(PortalObjectId)}.
     */
    @Test
    public final void testFormatHtmlSafeEncodingId() {
        PortalObjectId portalObjectIdMock = EasyMock.createMock("PortalObjectIdMock", PortalObjectId.class);
        String safeEncodingId;

        try {
            // Test 1 : simple ID
            EasyMock.expect(portalObjectIdMock.toString(EasyMock.anyObject(Format.class))).andReturn(ID_SIMPLE);
            EasyMock.replay(portalObjectIdMock);
            safeEncodingId = this.formatter.formatHtmlSafeEncodingId(portalObjectIdMock);
            assertEquals(ID_SIMPLE, safeEncodingId);
            EasyMock.verify(portalObjectIdMock);

            // Test 2 : ID with special chars
            EasyMock.reset(portalObjectIdMock);
            EasyMock.expect(portalObjectIdMock.toString(EasyMock.anyObject(Format.class))).andReturn(ID_SPECIAL_CHARS);
            EasyMock.replay(portalObjectIdMock);
            safeEncodingId = this.formatter.formatHtmlSafeEncodingId(portalObjectIdMock);
            assertNotEquals(ID_SIMPLE, safeEncodingId);
            assertEquals(URLEncoder.encode(ID_SPECIAL_CHARS, CharEncoding.UTF_8), safeEncodingId);
            EasyMock.verify(portalObjectIdMock);

            // Test 3 : null ID
            safeEncodingId = this.formatter.formatHtmlSafeEncodingId(null);
            assertNull(safeEncodingId);

            // Test 4 : empty ID
            EasyMock.reset(portalObjectIdMock);
            EasyMock.expect(portalObjectIdMock.toString(EasyMock.anyObject(Format.class))).andReturn(StringUtils.EMPTY);
            EasyMock.replay(portalObjectIdMock);
            safeEncodingId = this.formatter.formatHtmlSafeEncodingId(portalObjectIdMock);
            assertEquals(StringUtils.EMPTY, safeEncodingId);
            EasyMock.verify(portalObjectIdMock);

            // Optional test : format verification
            EasyMock.reset(portalObjectIdMock);
            Capture<Format> capturedFormat = new Capture<Format>();
            EasyMock.expect(portalObjectIdMock.toString(EasyMock.capture(capturedFormat))).andReturn(ID_SIMPLE);
            EasyMock.replay(portalObjectIdMock);
            safeEncodingId = this.formatter.formatHtmlSafeEncodingId(portalObjectIdMock);
            Format format = capturedFormat.getValue();
            assertEquals(PortalObjectPath.SAFEST_FORMAT.toString(), format.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    /**
     * Test method for {@link AssistantPageCustomizerInterceptor#formatHtmlPortletsList(ControllerContext)}.
     */
    @Test
    public final void testFormatHtmlPortletsList() {
        String htmlData;
        try {
            // Test 1 : null context
            htmlData = this.formatter.formatHtmlPortletsList(null);
            assertNull(htmlData);

            // Test 2 : 4 instance definitions generated
            htmlData = this.formatter.formatHtmlPortletsList(this.context);
            assertNotNull(htmlData);
            assertEquals(4, StringUtils.countMatches(htmlData, "<img"));
            assertEquals(4, StringUtils.countMatches(htmlData, "<button"));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    /**
     * Utility method used to generate a portal arborescence.
     *
     * @return current page in the generated arborescence
     */
    private Page generatePortalArborescence() {
        // Portal objects mocks
        Page currentPageMock = EasyMock.createNiceMock("CurrentPage", TemplatePage.class);
        PortalObjectId currentPageIdMock = EasyMock.createNiceMock("CurrentPageId", PortalObjectId.class);
        PortalObjectId portalIdMock = EasyMock.createNiceMock("PortalId", PortalObjectId.class);
        Page siblingPageMock = EasyMock.createNiceMock("SiblingPage", DynamicPersistentPage.class);
        PortalObjectId siblingPageIdMock = EasyMock.createNiceMock("SiblingPageId", PortalObjectId.class);
        Page subPageMock = EasyMock.createNiceMock("SubPage", TemplatePage.class);
        PortalObjectId subPageIdMock = EasyMock.createNiceMock("SubPageId", PortalObjectId.class);

        // Current page children
        Collection<PortalObject> currentPageChildren = new ArrayList<PortalObject>(1);
        currentPageChildren.add(subPageMock);
        // Current page display
        Map<Locale, String> currentPageDisplayMap = new HashMap<Locale, String>();
        currentPageDisplayMap.put(Locale.FRENCH, "[01] - Nom de la page");
        LocalizedString currentPageDisplay = new LocalizedString(currentPageDisplayMap, Locale.ENGLISH);
        // Portal children
        Collection<PortalObject> portalChildren = new ArrayList<PortalObject>(1);
        portalChildren.add(currentPageMock);
        portalChildren.add(siblingPageMock);
        // Portal display
        Map<Locale, String> portalDisplayMap = new HashMap<Locale, String>();
        portalDisplayMap.put(Locale.FRENCH, "Portail");
        LocalizedString portalDisplay = new LocalizedString(portalDisplayMap, Locale.ENGLISH);
        // Sibling page display
        Map<Locale, String> siblingPageDisplayMap = new HashMap<Locale, String>();
        siblingPageDisplayMap.put(Locale.FRENCH, "[02] - Nom de la page de même niveau");
        LocalizedString siblingPageDisplay = new LocalizedString(siblingPageDisplayMap, Locale.ENGLISH);
        // Sub page display
        Map<Locale, String> subPageDisplayMap = new HashMap<Locale, String>();
        subPageDisplayMap.put(Locale.FRENCH, "Nom de la sous page");
        LocalizedString subPageDisplay = new LocalizedString(subPageDisplayMap, Locale.ENGLISH);

        // Current page mock operations
        EasyMock.expect(currentPageMock.getPortal()).andStubReturn(this.portalMock);
        EasyMock.expect(currentPageMock.getParent()).andStubReturn(this.portalMock);
        EasyMock.expect(currentPageMock.getChildren(EasyMock.anyInt())).andReturn(currentPageChildren).anyTimes();
        EasyMock.expect(currentPageMock.getId()).andStubReturn(currentPageIdMock);
        EasyMock.expect(currentPageMock.getName()).andReturn("current-page").anyTimes();
        EasyMock.expect(currentPageMock.getDisplayName()).andReturn(currentPageDisplay).anyTimes();
        EasyMock.expect(currentPageMock.getDeclaredProperty(EasyMock.anyObject(String.class))).andReturn("2").anyTimes();
        EasyMock.expect(currentPageMock.getProperty("osivia.cms.basePath")).andReturn(CMS_BASE_PATH).anyTimes();
        // Current page ID mock operations
        EasyMock.expect(currentPageIdMock.toString(EasyMock.anyObject(Format.class))).andReturn(CURRENT_PAGE_ID).anyTimes();
        // Portal mock operations
        EasyMock.expect(this.portalMock.getParent()).andReturn(null).anyTimes();
        EasyMock.expect(this.portalMock.getChildren(EasyMock.anyInt())).andReturn(portalChildren).anyTimes();
        EasyMock.expect(this.portalMock.getId()).andStubReturn(portalIdMock);
        EasyMock.expect(this.portalMock.getDisplayName()).andReturn(portalDisplay).anyTimes();
        EasyMock.expect(this.portalMock.getDeclaredProperty("osivia.liste_styles")).andReturn("PortalStyle1,PortalStyle2").anyTimes();
        // Portal ID mock operations
        EasyMock.expect(portalIdMock.toString(EasyMock.anyObject(Format.class))).andReturn("/portal/").anyTimes();
        // Sibling page mock operations
        EasyMock.expect(siblingPageMock.getParent()).andStubReturn(this.portalMock);
        EasyMock.expect(siblingPageMock.getChildren(EasyMock.anyInt())).andReturn(new ArrayList<PortalObject>()).anyTimes();
        EasyMock.expect(siblingPageMock.getId()).andStubReturn(siblingPageIdMock);
        EasyMock.expect(siblingPageMock.getName()).andReturn("sibling-page").anyTimes();
        EasyMock.expect(siblingPageMock.getDisplayName()).andReturn(siblingPageDisplay).anyTimes();
        EasyMock.expect(siblingPageMock.getDeclaredProperty(EasyMock.anyObject(String.class))).andReturn("1").anyTimes();
        EasyMock.expect(siblingPageMock.getProperty("osivia.cms.basePath")).andReturn(null).anyTimes();
        // Sibling page ID mock operations
        EasyMock.expect(siblingPageIdMock.toString(EasyMock.anyObject(Format.class))).andReturn("/portal/sibling-page/").anyTimes();
        // Sub page mock operations
        EasyMock.expect(subPageMock.getParent()).andStubReturn(currentPageMock);
        EasyMock.expect(subPageMock.getChildren(EasyMock.anyInt())).andReturn(null).anyTimes();
        EasyMock.expect(subPageMock.getId()).andStubReturn(subPageIdMock);
        EasyMock.expect(subPageMock.getDisplayName()).andReturn(subPageDisplay).anyTimes();
        EasyMock.expect(subPageMock.getDeclaredProperty(EasyMock.anyObject(String.class))).andReturn("1").anyTimes();
        EasyMock.expect(subPageMock.getProperty("osivia.cms.basePath")).andReturn(CMS_BASE_PATH).anyTimes();
        // Sub page ID mock operations
        EasyMock.expect(subPageIdMock.toString(EasyMock.anyObject(Format.class))).andReturn(SUB_PAGE_ID).anyTimes();

        // Replay
        EasyMock.replay(currentPageMock);
        EasyMock.replay(currentPageIdMock);
        EasyMock.replay(this.portalMock);
        EasyMock.replay(portalIdMock);
        EasyMock.replay(siblingPageMock);
        EasyMock.replay(siblingPageIdMock);
        EasyMock.replay(subPageMock);
        EasyMock.replay(subPageIdMock);

        return currentPageMock;
    }


    /**
     * Utility method used to generage instance definitions
     *
     * @return a collection who contains 4 instance definitions
     */
    private Collection<InstanceDefinition> generateInstanceDefinitions() {
        Collection<InstanceDefinition> definitions = new ArrayList<InstanceDefinition>();

        for (int i = 1; i <= 4; i++) {
            InstanceDefinition definitionMock = EasyMock.createNiceMock(InstanceDefinition.class);
            Portlet portletMock = EasyMock.createNiceMock(Portlet.class);
            PortletInfo portletInfoMock = EasyMock.createNiceMock(PortletInfo.class);
            PortletInfoInfo portletInfoInfoMock = EasyMock.createNiceMock(PortletInfoInfo.class);

            LocalizedString displayNames = new LocalizedString("Display name " + i);

            EasyMock.expect(definitionMock.getDisplayName()).andReturn(displayNames).anyTimes();
            EasyMock.expect(definitionMock.getId()).andReturn("ID" + i).anyTimes();
            try {
                EasyMock.expect(definitionMock.getPortlet()).andStubReturn(portletMock);
            } catch (PortletInvokerException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
            EasyMock.expect(portletMock.getInfo()).andStubReturn(portletInfoMock);
            EasyMock.expect(portletInfoMock.getAttachment(PortletInfoInfo.class)).andReturn(portletInfoInfoMock);

            EasyMock.replay(definitionMock);
            EasyMock.replay(portletMock);
            EasyMock.replay(portletInfoMock);
            EasyMock.replay(portletInfoInfoMock);

            definitions.add(definitionMock);
        }

        return definitions;
    }

}
