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
import java.util.Locale;

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
import org.jboss.portal.core.model.portal.Portal;
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
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
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
