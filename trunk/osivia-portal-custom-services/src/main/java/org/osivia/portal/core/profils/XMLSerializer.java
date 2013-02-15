package org.osivia.portal.core.profils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.core.profils.ProfilBean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;




public class XMLSerializer {
	
	protected static final Log logger = LogFactory.getLog(XMLSerializer.class);
	
	public static class Profils {
		
		List<ProfilBean> implicitProfils ;

		public List<ProfilBean> getImplicitProfils() {
			return implicitProfils;
		}

		public Profils(List<ProfilBean> profils) {
			super();
			this.implicitProfils = profils;
		}
	 }

	
	
	public String encodeAll(List<ProfilBean> listToEncode) {	
		
		
		XStream xstream = new XStream(new DomDriver());
		
		
		xstream.alias("profil", ProfilBean.class);
	    xstream.alias("profils", Profils.class);
	    
	    xstream.addImplicitCollection(Profils.class, "implicitProfils", "profil", ProfilBean.class);
	   
		

		// Convertion du contenu de l'objet article en XML
		String xmlText = xstream.toXML(new Profils( listToEncode));

		
		return xmlText;

	}

	public List<ProfilBean> decodeAll(String input) {
	
		XStream xstream = new XStream(new DomDriver());
		
		
		xstream.alias("profil", ProfilBean.class);
	    xstream.alias("profils", Profils.class);
	    
	    xstream.addImplicitCollection(Profils.class, "implicitProfils", "profil", ProfilBean.class);
		
		if( input == null || input.length() == 0)
			return new ArrayList<ProfilBean>();		
		
		InputStream in = new ByteArrayInputStream(input.getBytes()); 
		

		
        // objet article
		Profils profils = (Profils) xstream.fromXML(in);
		return profils.getImplicitProfils();


	}
	
}
