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
 */
package org.osivia.portal.api.directory.entity;

import java.util.ArrayList;
import java.util.List;

import org.osivia.portal.api.urls.Link;

/**
 * Person from LDAP directory
 */
public class DirectoryPerson {


    // ------------------------------------------------------------------------------------------------------------------------
    // ATTRIBUTS DE L'OBJET PERSONNE
    // ------------------------------------------------------------------------------------------------------------------------

    /**
     * Identifiant de la personne (en général 1ère lettre du prénom - nom)
     */
    private String uid;

    /**
     * Nom de la personne tel qu'il doit être affiché
     */
    private String displayName;

    /**
     * Prénom de la personne
     */
    private String givenName;

    /**
     * Nom de famille de la personne
     */
    private String sn;

    /**
     * Nom et prénom de la personne
     */
    private String cn;

    /**
     * Surnom de la personne
     */
    private String alias;
    /**
     * Date de naissance de la personne
     */
    private String birthDate;

    /**
     * Sexe de la personne
     */
    private String gender;


    /**
     * Liste des DN des profils affectés à la personne
     */
    private List<String> listeProfiles = new ArrayList<String>();


    /**
     * Liste des DN des rôles applicatifs affectés à la personne
     */
    // private ArrayList<String> listeRoles = new ArrayList<String>();

    /**
     * Titre de la personne (ELE, INS, ADM,...)
     */
    private String title;

    /**
     * Adresse email de la personne
     */
    private String email;


    /** Avatar */
    private Link avatar = new Link("", false);


    /** LDAP native item. */
    private Object nativeItem;

    // ------------------------------------------------------------------------------------------------------------------------
    // GETTERS ET SETTERS
    // ------------------------------------------------------------------------------------------------------------------------


    /**
     * Getter de l'identifiant de la personne
     * 
     * @return
     */
    public String getUid() {
        return uid;
    }

    /**
     * Setter de l'identifiant de la personne
     * 
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Getter du DN (distinguished Name) de la personne
     * Ramène le DN complet de l'objet (avec le base DN du type dc=ent-bretagne,dc=fr)
     * 
     * @return
     */
    // public String getDn() {
    // return personDao.buildFullDn(this.getUid());
    // }

    /**
     * Getter du display name (sous la forme "Prénom Nom")
     * 
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Setter du displayName (sous la forme "Prénom Nom")
     * 
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Getter du GivenName (Prénom)
     * 
     * @return
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Setter du GivenName (Prénom)
     * 
     * @param givenName
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * Getter du Sn (Nom de famille)
     * 
     * @return
     */
    public String getSn() {
        return sn;
    }

    /**
     * Setter du Sn (Nom de famille)
     * 
     * @param sn
     */
    public void setSn(String sn) {
        this.sn = sn;
    }

    /**
     * Getter du Cn (sous la forme "Nom Prenom")
     * 
     * @return
     */
    public String getCn() {
        return cn;
    }

    /**
     * Setter du Cn (sous la forme "Nom Prenom")
     * 
     * @param cn
     */
    public void setCn(String cn) {
        this.cn = cn;
    }

    /**
     * Getter de l'alias de la personne (pseudo)
     * 
     * @return
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Setter de l'alias de la personne (pseudo)
     * 
     * @return
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }


    /**
     * @return the birthDate
     */
    public String getBirthDate() {
        return birthDate;
    }


    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }


    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }


    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }


    /**
     * @return the listeProfiles
     */
    public List<String> getListeProfiles() {
        return listeProfiles;
    }


    /**
     * @param listeProfiles the listeProfiles to set
     */
    public void setListeProfiles(List<String> listeProfiles) {
        this.listeProfiles = listeProfiles;
    }

    /**
     * Récupération du TITLE de la personne
     * 
     * @return ELE pour élève, ENS pour enseignant, EDU, DOC, DIR,...
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter du TITLE de la personne
     * 
     * @param title ELE pour élève, ENS pour enseignant, EDU, DOC, DIR,...
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Récupération de l'adresse email de la personne
     * 
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter de l'adresse email de la personne
     * 
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * @return the avatar
     */
    public Link getAvatar() {
        return avatar;
    }


    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(Link avatar) {
        this.avatar = avatar;
    }


    /**
     * @return the nativeItem
     */
    public Object getNativeItem() {
        return nativeItem;
    }


    /**
     * @param nativeItem the nativeItem to set
     */
    public void setNativeItem(Object nativeItem) {
        this.nativeItem = nativeItem;
    }


}
