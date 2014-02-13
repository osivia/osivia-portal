package org.osivia.portal.core.auth;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Context;
import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.UserProfileModule;


/*
 * Valve d'administration
 * 
 * permet de bypasser CAS en mode intranet
 * 
 * 
 * <?xml version="1.0"?>
 * <Context>
 * 
 * <Valve className="org.osivia.portal.core.auth.SSOIntraAuthenticationValve" />
 * 
 * </Context>
 */
public class SSOIntraAuthenticationValve extends ValveBase {

    /** . */
    private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(SSOIntraAuthenticationValve.class);


    private String authType = "FORM";


    /**
     * 
     *
     */
    public boolean authenticate(String login, String password) {
        try {
            InitialContext initialContext = new InitialContext();

            UserModule userModule = null;
            userModule = (UserModule) initialContext.lookup("java:/portal/UserModule");

            User user = userModule.findUserByUserName(login);
            if (user != null) {
                return user.validatePassword(password);
            }


        } catch (Exception e) {
            log.error(this, e);
        }

        return false;
    }


    public byte[] displayLoginForm(String errorMsg) {

        StringBuffer sb = new StringBuffer();
        sb.append("<body style=\"font-family: Verdana,Arial,Helvetica,Sans-Serif,sans-serif;\"><html>");
        if (errorMsg != null) {
            sb.append("<span style=\"color: #FF0000;\">" + errorMsg + "</span>");
        }
        sb.append("<form method=\"post\" action=\"/portail/intra-admin/check\" name=\"loginform\" id=\"loginForm\" target=\"_parent\" style=\"margin:0;padding:0\">");
        sb.append("      <div class=\"form-field\">");
        sb.append("          <label for=\"j_username\">Identifiant");
        sb.append("          </label>");
        sb.append("          <input type=\"text\" style=\"width:155px;\" name=\"j_username\" id=\"j_username\" value=\"\" />");
        sb.append("      </div>");
        sb.append("   <div class=\"form-field\">");
        sb.append("          <label for=\"j_password\">Mot de passe");
        sb.append("         </label>");
        sb.append("          <input type=\"password\" style=\"width:155px;\" name=\"j_password\" id=\"j_password\" value=\"\" />");
        sb.append("      </div>");
        sb.append("      <br class=\"clear\"/>");

        sb.append("      <div class=\"button-container\">");
        sb.append("          <input type=\"submit\" name=\"login\" id=\"login-submit\"");
        sb.append("                 value=\"Se connecter\" class=\"login-button\"/>");
        sb.append("      </div>");
        sb.append("  </form>");
        sb.append("</html></body>");

        return String.valueOf(sb).getBytes();

    }

    public SSOIntraAuthenticationValve() {
        super();

    }


    /**
    * 
    */
    public void invoke(Request request, Response response) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;


        String host = request.getHeader("host");

        String uri = request.getDecodedRequestURI();

        // if (StringUtils.contains(host, "8080") && StringUtils.indexOf(uri, "/portail/intra_admin") == 0) {

        if (StringUtils.indexOf(uri, "/portail/intra-admin") == 0) {

            // System.err.println("host" + host);

            // System.err.println("uri" + request.getDecodedRequestURI());

            HttpSession session = httpRequest.getSession();

            if (session.getAttribute("intranet_user") == null) {


                // System.err.println("no user defined");

                // Disable cas
                session.setAttribute("edu.yale.its.tp.cas.client.filter.user", "SKIP_CAS");


                String userName = request.getParameter("j_username");
                String password = request.getParameter("j_password");

                if (userName != null && password != null) {
                    request.setAttribute("ssoEnabled", "true");

                    // System.err.println("authenticate");

                    if (authenticate(userName, password)) {

                        // System.err.println("success");

                        request.setAttribute("ssoSuccess", new Boolean(true));
                        Principal principal = ((Context) this.container).getRealm().authenticate(userName, (String) null);
                        if (principal != null) {
                            this.register(request, response, principal, this.authType, userName, (String) null);
                            session.setAttribute("intranet_user", userName);

                            String redirect = "http://" + host + "/portail/auth/portal/admin/osivia-admin";
                            // System.err.println("redirect" + redirect);

                            response.sendRedirect(redirect);
                            return;


                        }
                    } else {
                        response.getOutputStream().write(displayLoginForm("Login/mot de passe incorrect"));
                        response.getOutputStream().flush();

                        return;
                    }
                } else {

                    // System.err.println("custom login");


                    response.getOutputStream().write(displayLoginForm(null));
                    response.getOutputStream().flush();

                    return;


                }


            }

            String redirect = "http://" + host + "/portail/auth/portal/admin/osivia-admin";
            // System.err.println("redirect" + redirect);

            response.sendRedirect(redirect);
            return;


        }

        // continue processing the request
        this.getNext().invoke(request, response);


        HttpSession session = httpRequest.getSession();

        if (session.getAttribute("intranet_user") != null && response.getStatus() == 302) {
 
            // Redirect to original host in admin mode
            String location = response.getHeader("Location");
            
//            System.err.println("redirection" + location);
//            
            if (StringUtils.isNotEmpty(location)) {

                Pattern expOrginial = Pattern.compile("http://(([^/:]*)(:[0-9]*)?)?/portail/(.*)");
                Matcher mResrTest = expOrginial.matcher(location);

                if (mResrTest.matches()) {
                    if (mResrTest.groupCount() == 4) {
                        for (int i = 0; i <= mResrTest.groupCount(); i++) {
                            location = "http://" + host + "/portail/" + mResrTest.group(4);
                            response.setHeader("Location", location);
                            
//                            System.err.println("new redirection" + location);
                        }
                    }
                }

                
                
                
            }
        }

        /*
         * 
         * String h[] = response.getHeaderNames();
         * 
         * for( int i=0; i< h.length; i++){
         * System.err.println("header " + h[i] + " " + response.getHeader(h[i]));
         * }
         * 
         * String location = response.getHeader("Location");
         * if( StringUtils.isNotEmpty(location)) {
         * //response.setHeader("Location", location.replace("toutatice.vm", "test.vm"));
         * System.err.println("location " + location);
         * // response.setHeader("Location", "http://www.google.fr");
         * 
         * location = location.replace("toutatice.vm", "test.vm");
         * 
         * System.err.println("New location " + location);
         * 
         * response.setHeader("Location", location);
         * }
         */

    }

    /**
     * Register an authenticated Principal and authentication type in our
     * request, in the current session (if there is one), and with our
     * SingleSignOn valve, if there is one. Set the appropriate cookie to be
     * returned.
     * 
     * @param request
     *            The servlet request we are processing
     * @param response
     *            The servlet response we are generating
     * @param principal
     *            The authenticated Principal to be registered
     * @param authType
     *            The authentication type to be registered
     * @param username
     *            Username used to authenticate (if any)
     * @param password
     *            Password used to authenticate (if any)
     */
    private void register(Request request, Response response, Principal principal, String authType, String username, String password) {
        // Cache the authentication information in our request
        request.setAuthType(authType);
        request.setUserPrincipal(principal);

        Session session = request.getSessionInternal(false);
        // Cache the authentication information in our session, if any
        if (session != null) {
            session.setAuthType(authType);
            session.setPrincipal(principal);
            if (username != null) {
                session.setNote(Constants.SESS_USERNAME_NOTE, username);
            } else {
                session.removeNote(Constants.SESS_USERNAME_NOTE);
            }
            if (password != null) {
                session.setNote(Constants.SESS_PASSWORD_NOTE, password);
            } else {
                session.removeNote(Constants.SESS_PASSWORD_NOTE);
            }
        }
    }


}
