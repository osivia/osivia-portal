package org.osivia.portal.core.web;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.DocumentsMetadata;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;

/**
 * Web URL service implementation test.
 *
 * @author Cédric Krommenhoek
 * @see WebUrlService
 */
public class WebUrlServiceTest {

    /** Base path test value. */
    private static final String BASE_PATH = "/domain/site";


    /** Class under class. */
    private WebUrlService service;

    /** CMS service mock. */
    private ICMSService cmsService;
    /** CMS context. */
    private CMSServiceCtx cmsContext;


    /**
     * Constructor.
     */
    public WebUrlServiceTest() {
        super();
    }


    /**
     * Class initialization.
     */
    @BeforeClass
    public static void init() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.TRACE);
    }


    /**
     * Test set-up.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // Documents metadata
        DocumentsMetadata metadata = EasyMock.createMock(DocumentsMetadata.class);
        EasyMock.expect(metadata.getWebPath(EasyMock.anyObject(String.class))).andReturn("/domain/web-path").anyTimes();
        EasyMock.expect(metadata.getWebId(EasyMock.anyObject(String.class))).andReturn("webId").anyTimes();
        metadata.update(EasyMock.anyObject(DocumentsMetadata.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(metadata.getTimestamp()).andReturn(System.currentTimeMillis() - 1).anyTimes();
        EasyMock.replay(metadata);

        // Controller context
        ControllerContext controllerContext = EasyMock.createNiceMock(ControllerContext.class);
        IAnswer<Object> attributeAnswer = new AttributeAnswer();
        EasyMock.expect(controllerContext.getAttribute(Scope.REQUEST_SCOPE, WebUrlService.REQUEST_ATTRIBUTE)).andAnswer(attributeAnswer);
        controllerContext.setAttribute(EasyMock.anyObject(Scope.class), EasyMock.anyObject(String.class), EasyMock.anyObject());
        EasyMock.expectLastCall().andAnswer(attributeAnswer).times(0, 1);
        EasyMock.replay(controllerContext);

        // CMS context
        this.cmsContext = new CMSServiceCtx();
        this.cmsContext.setControllerContext(controllerContext);

        // CMS service
        this.cmsService = EasyMock.createStrictMock(ICMSService.class);
        EasyMock.expect(this.cmsService.getDocumentsMetadata(this.cmsContext, BASE_PATH, null)).andReturn(metadata).times(1);
        Capture<Long> timestampCapture = new TimestampCapture();
        EasyMock.expect(
                this.cmsService.getDocumentsMetadata(EasyMock.anyObject(CMSServiceCtx.class), EasyMock.anyObject(String.class),
                        EasyMock.captureLong(timestampCapture))).andStubReturn(metadata);
        EasyMock.replay(this.cmsService);

        // Cache service
        ICacheService cacheService = EasyMock.createMock(ICacheService.class);
        EasyMock.expect(cacheService.checkIfPortalParametersReloaded(EasyMock.anyLong())).andReturn(true).anyTimes();
        EasyMock.replay(cacheService);

        // CMS service locator
        ICMSServiceLocator cmsServiceLocator = EasyMock.createMock(ICMSServiceLocator.class);
        EasyMock.expect(cmsServiceLocator.getCMSService()).andStubReturn(this.cmsService);
        EasyMock.replay(cmsServiceLocator);

        // Service
        this.service = new WebUrlService();
        this.service.setCmsServiceLocator(cmsServiceLocator);
        this.service.setCacheService(cacheService);
    }


    /**
     * Test getWebPath function.
     *
     * @throws PortalException
     */
    @Test
    public final void test() throws PortalException {
        ExecutorService executor = Executors.newFixedThreadPool(6);

        List<Exception> exceptions = new ArrayList<Exception>();

        // Multiple calls
        for (int i = 0; i < 20; i++) {
            Runnable serviceRunnable = new ServiceRunnable(exceptions);
            executor.execute(serviceRunnable);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }

        if (!exceptions.isEmpty()) {
            Exception exception = exceptions.iterator().next();
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }

        EasyMock.verify(this.cmsService);
    }



    /**
     * Attribute anwser.
     *
     * @author Cédric Krommenhoek
     * @see IAnswer
     */
    private class AttributeAnswer implements IAnswer<Object> {

        /** Attribute value. */
        private Object value;


        /**
         * Constructor.
         */
        public AttributeAnswer() {
            super();
        }


        /**
         * {@inheritDoc}
         */
        public Object answer() throws Throwable {
            if (this.value == null) {
                Object[] args = EasyMock.getCurrentArguments();
                if (args.length == 3) {
                    this.value = args[2];
                }
            }
            return this.value;
        }

    }


    /**
     * Timestamp capture.
     *
     * @author Cédric Krommenhoek
     * @see Capture
     */
    private class TimestampCapture extends Capture<Long> {

        /** Default serial version ID. */
        private static final long serialVersionUID = 1L;


        /** Timestamp. */
        private long timestamp;


        /**
         * Constructor.
         */
        public TimestampCapture() {
            super();
            this.timestamp = System.currentTimeMillis();
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public Long getValue() {
            Long value = super.getValue();
            Assert.assertTrue(value.longValue() > this.timestamp);
            return value;
        }

    }


    /**
     * Service runnable.
     *
     * @author Cédric Krommenhoek
     * @see Runnable
     */
    private class ServiceRunnable implements Runnable {

        /** Exceptions. */
        private final List<Exception> exceptions;


        /**
         * Constructor.
         */
        public ServiceRunnable(List<Exception> exceptions) {
            super();
            this.exceptions = exceptions;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            try {
                WebUrlServiceTest.this.service.getWebPath(WebUrlServiceTest.this.cmsContext, BASE_PATH, UUID.randomUUID().toString());
                Thread.sleep(200);
                WebUrlServiceTest.this.service.getWebId(WebUrlServiceTest.this.cmsContext, BASE_PATH, UUID.randomUUID().toString());
            } catch (Exception e) {
                this.exceptions.add(e);
            }
        }

    }

}
