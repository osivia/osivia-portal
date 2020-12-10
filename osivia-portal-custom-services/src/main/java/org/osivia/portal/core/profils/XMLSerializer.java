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
package org.osivia.portal.core.profils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class XMLSerializer {

    protected static final Log logger = LogFactory.getLog(XMLSerializer.class);

    public String encodeAll(List<ProfilBean> listToEncode) {


        XStream xstream = new XStream(new DomDriver());


        xstream.alias("profil", ProfilBean.class);
        xstream.alias("profils", Profils.class);

        xstream.addImplicitCollection(Profils.class, "implicitProfils", "profil", ProfilBean.class);


        // Convertion du contenu de l'objet article en XML
        String xmlText = xstream.toXML(new Profils(listToEncode));


        return xmlText;

    }

    public List<ProfilBean> decodeAll(String input) {

        XStream xstream = new XStream(new DomDriver());


        xstream.alias("profil", ProfilBean.class);
        xstream.alias("profils", Profils.class);

        xstream.addImplicitCollection(Profils.class, "implicitProfils", "profil", ProfilBean.class);

        if (input == null || input.length() == 0)
            return new ArrayList<ProfilBean>();

        InputStream in = new ByteArrayInputStream(input.getBytes());


        // objet article
        Profils profils = (Profils) xstream.fromXML(in);
        return profils.getImplicitProfils();


    }

    public static class Profils {

        List<ProfilBean> implicitProfils;

        /**
         * Default constructor.
         */
        public Profils() {
            super();
        }

        public Profils(List<ProfilBean> profils) {
            super();
            this.implicitProfils = profils;
        }

        public List<ProfilBean> getImplicitProfils() {
            return implicitProfils;
        }

        public void setImplicitProfils(List<ProfilBean> implicitProfils) {
            this.implicitProfils = implicitProfils;
        }
    }

}
