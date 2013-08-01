package org.osivia.portal.administration.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.jboss.portal.core.model.portal.Portal;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.administration.util.AdministrationConstants;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;

/**
 * Profiles bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractAdministrationBean
 */
@Name("profilesBean")
@Scope(ScopeType.PAGE)
public class ProfilesBean extends AbstractAdministrationBean {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Profile manager. */
    private IProfilManager profileManager;
    /** Profile. */
    private ProfileData profile;
    /** Selected profiles. */
    private Set<ProfileData> selectedProfiles;
    /** Profiles list. */
    private List<ProfileData> profiles;
    /** Edition indicator. */
    private boolean edition;


    /**
     * Default constructor.
     */
    public ProfilesBean() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Create
    @Override
    public void init() {
        super.init();
        this.profileManager = (IProfilManager) this.getPortletContext().getAttribute(AdministrationConstants.PROFILE_MANAGER_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Observable observable, Object arg) {
        this.profile = null;
        this.selectedProfiles = null;
        this.refreshProfiles();
    }


    /**
     * Add profile action.
     */
    public void addProfile() {
        this.profile = new ProfileData();
        this.edition = false;
        this.setPopupTitle("Ajouter profil");
    }


    /**
     * Add profile popup submit action.
     */
    public void addProfileSubmit() {
        if (this.profiles.contains(this.profile)) {
            this.setMessages("Le profil existe déjà.");
        } else {
            this.profiles.add(this.profile);
        }
    }


    /**
     * Edit profile action.
     */
    public void editProfile() {
        if (this.selectedProfiles.isEmpty()) {
            this.addProfile();
        } else {
            this.profile = this.selectedProfiles.iterator().next();
            this.edition = true;
            this.setPopupTitle("Modifier profil " + this.profile.getName());
        }
    }


    /**
     * Edit profile popup submit action.
     */
    public void editProfileSubmit() {
        for (ProfileData data : this.profiles) {
            if (this.profile.equals(data)) {
                this.profiles.remove(data);
                break;
            }
        }
        this.profiles.add(this.profile);
    }


    /**
     * Delete profile action.
     */
    public void deleteProfile() {
        for (ProfileData selectedProfile : this.selectedProfiles) {
            this.profiles.remove(selectedProfile);
        }
    }


    /**
     * Refresh profiles action.
     */
    public void refreshProfiles() {
        Portal portal = this.getPortal();
        List<ProfilBean> portalProfiles = this.profileManager.getListeProfils(portal);
        this.profiles = new ArrayList<ProfileData>(portalProfiles.size());
        for (ProfilBean portalProfile : portalProfiles) {
            ProfileData data = new ProfileData(portalProfile);
            this.profiles.add(data);
        }
    }


    /**
     * Save profiles action.
     */
    public void save() {
        Portal portal = this.getPortal();
        List<ProfilBean> portalProfiles = new ArrayList<ProfilBean>(this.profiles.size());
        for (ProfileData data : this.profiles) {
            ProfilBean portalProfile = data.toProfileBean();
            portalProfiles.add(portalProfile);
        }
        this.profileManager.setListeProfils(portal, portalProfiles);
        this.setMessages("Les profils ont été mis à jour.");
    }


    /**
     * Getter for profile.
     *
     * @return the profile
     */
    public ProfileData getProfile() {
        return this.profile;
    }

    /**
     * Setter for profile.
     *
     * @param profile the profile to set
     */
    public void setProfile(ProfileData profile) {
        this.profile = profile;
    }

    /**
     * Getter for selectedProfiles.
     *
     * @return the selectedProfiles
     */
    public Set<ProfileData> getSelectedProfiles() {
        return this.selectedProfiles;
    }

    /**
     * Setter for selectedProfiles.
     *
     * @param selectedProfiles the selectedProfiles to set
     */
    public void setSelectedProfiles(Set<ProfileData> selectedProfiles) {
        this.selectedProfiles = selectedProfiles;
    }

    /**
     * Getter for profiles.
     *
     * @return the profiles
     */
    public List<ProfileData> getProfiles() {
        return this.profiles;
    }

    /**
     * Setter for profiles.
     *
     * @param profiles the profiles to set
     */
    public void setProfiles(List<ProfileData> profiles) {
        this.profiles = profiles;
    }

    /**
     * Getter for edition.
     *
     * @return the edition
     */
    public boolean isEdition() {
        return this.edition;
    }

    /**
     * Setter for edition.
     *
     * @param edition the edition to set
     */
    public void setEdition(boolean edition) {
        this.edition = edition;
    }

}
