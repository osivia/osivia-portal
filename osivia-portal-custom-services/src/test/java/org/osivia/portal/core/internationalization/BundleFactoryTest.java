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
package org.osivia.portal.core.internationalization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;


public class BundleFactoryTest {

    /** Bundle factory. */
    private IBundleFactory bundleFactory;

    /** Internationalization service. */
    private IInternationalizationService internationalizationServiceMock;

    @Before
    public void setUp() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();

        this.internationalizationServiceMock = EasyMock.createMock("InternationalizationService", IInternationalizationService.class);
        EasyMock.expect(this.internationalizationServiceMock.getString("EXAMPLE", Locale.ITALIAN, classLoader)).andReturn("esempio");
        EasyMock.replay(this.internationalizationServiceMock);

        this.bundleFactory = new BundleFactory(this.internationalizationServiceMock, classLoader);
    }

    @Test
    public final void testGetBundle() {
        Bundle bundle = this.bundleFactory.getBundle(Locale.ITALIAN);
        assertNotNull(bundle);

        String test = bundle.getString("EXAMPLE");
        assertEquals("esempio", test);
    }

}
