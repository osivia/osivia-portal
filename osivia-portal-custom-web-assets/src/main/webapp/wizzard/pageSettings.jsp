<%@page import="org.osivia.portal.core.formatters.IFormatter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashSet"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<%@page import="java.util.Collection"%>
<%@page import="org.jboss.portal.theme.PortalLayout"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="org.jboss.portal.identity.Role" %>
<%@page import="org.jboss.portal.core.model.portal.Window"%>
<%@page import="org.jboss.portal.theme.PortalTheme"%>




<%
	String pageName = (String)request.getAttribute("osivia.setting.page.NAME");
	String pageCmsBasePath = (String)request.getAttribute("osivia.setting.page.CMS_BASE_PATH");
	String pageCmsScopeSelect = (String)request.getAttribute("osivia.setting.page.CMS_SCOPE_SELECT");	
	String pageCmsNavigationScopeSelect = (String)request.getAttribute("osivia.setting.page.CMS_NAVIGATION_SCOPE_SELECT");	
	String pageCmsDisplayLiveVersionSelect = (String)request.getAttribute("osivia.setting.page.CMS_DISPLAY_LIVE_VERSION_SELECT");	
	
	
	String pageCMSNavigationMode = (String)request.getAttribute("osivia.setting.page.CMS_NAVIGATION_MODE");	
	String pageContextualizationSupport = (String)request.getAttribute("osivia.setting.page.PAGE_CONTEXTUALIZATION_SUPPORT_SELECT");	
	String outgoingRecontextualizationSupport = (String)request.getAttribute("osivia.setting.page.OUTGOING_RECONTEXTUALIZATION_SUPPORT_SELECT");	


	Collection<Page> sisters = (Collection<Page>) request.getAttribute("osivia.setting.page.order.sisters");
	Collection<Page> models = (Collection<Page>) request.getAttribute("osivia.setting.page.create.models");
	
	String url = (String)request.getAttribute("osivia.setting.URL");
	String currentLayout = (String)request.getAttribute("osivia.setting.layout.NAME");
	String defaultPage = (String)request.getAttribute("osivia.setting.page.DEFAULT_PAGE");
	Map<String, String> categories = (Map<String, String>) request.getAttribute("osivia.setting.page.CATEGORIES");
	String category = (String) request.getAttribute("osivia.setting.page.CATEGORY");
	
	String draftPage = (String)request.getAttribute("osivia.setting.page.DRAFT_PAGE");
	Collection<PortalLayout> listLayout = (Collection<PortalLayout>)request.getAttribute("osivia.setting.LAYOUT_LIST");
	List<Role> roleAvaible = (List<Role>)request.getAttribute("osivia.setting.ROLE_AVAIBLE");
	Map<String,Set<String>> actionsForRole = (Map<String, Set<String>>)request.getAttribute("osivia.setting.ACTIONS_FOR_ROLE");
	
	String deletePageUrl = (String) request.getAttribute("osivia.setting.DELETE_PAGE_URL");
	String commandUrl = (String) request.getAttribute("osivia.setting.COMMAND_URL");
	String pageId = (String) request.getAttribute("osivia.setting.PAGE_ID");
	
	List<Window> windows = (List<Window>) request.getAttribute("osivia.setting.windows");
	List<String> portalStyles  = (List<String>) request.getAttribute("osivia.setting.windows.PORTAL_STYLES");
	List<InstanceDefinition> portletDefinitions = (List<InstanceDefinition>) request.getAttribute("osivia.setting.portlets");
	
	String templateUrl = (String) request.getAttribute("osivia.setting.page.CMS_TEMPLATE_URL");
	
	IFormatter formatter = (IFormatter) request.getAttribute("osivia.setting.FORMATTER");
	
	
	

%>
	

	


<%@page import="org.jboss.portal.core.model.portal.PortalObjectPath"%>
<%@page import="org.jboss.portal.core.model.instance.InstanceDefinition"%>
<%@page import="org.jboss.portal.portlet.info.PortletInfo"%>
<%@page import="org.jboss.portal.core.portlet.info.PortletInfoInfo"%>
<%@page import="org.jboss.portal.core.portlet.info.PortletIconInfo"%>
<%@page import="org.jboss.portal.core.model.portal.Page"%>

<script type="text/javascript" src="/osivia-portal-custom-web-assets/js/modal-message.js"></script>


<%
	if(pageName != null && pageName.length() != 0){
%>		


	<script type="text/javascript">
		var messageModal = new DHTML_modalMessage();
		function displaySettings(){
			if($("modal-settings")){
				messageModal.setHtmlContent($("modal-settings").innerHTML);
				$("modal-settings").remove();
				messageModal.setShadowDivVisible(false);
			}
			messageModal.display();
		}
		function closeSettings(){
			messageModal.close();
		}

		function displaySettingSubBlock(subBlockId) {	
			if($(subBlockId).visible()){
				$(subBlockId).hide();
			}else{
				var linkSetting = $(subBlockId).ancestors()[0];
				var divs = linkSetting.select("div");
				for(var i=0;i<divs.length;i++){
					if(divs[i].identify() != subBlockId && divs[i].visible()){
						divs[i].hide();
					}
				}
				$(subBlockId).show();
			}
		}
	</script>
		

		<div id="modal-settings" style="display: none;">
			<div class="close-settings clickable-element" onclick="closeSettings();"><img src="<%= request.getContextPath() %>/images/blank.png"/><span class="title-settings">Configuration de la page</span></div>
			<div id="linkSettings" style="color: #000000;">
				<span class="clickable-element" onclick="displaySettingSubBlock('createPage');">Création</span>&nbsp;
				|&nbsp;<span class="clickable-element" onclick="displaySettingSubBlock('propsPage');">Propriétés</span>&nbsp;
				|&nbsp;<span class="clickable-element" onclick="displaySettingSubBlock('changeLayout');">Layout</span>&nbsp;
				|&nbsp;<span class="clickable-element" onclick="displaySettingSubBlock('changeOrder');">Ordre</span>&nbsp;				
				|&nbsp;<span class="clickable-element" onclick="displaySettingSubBlock('securePage');">Droits</span>&nbsp;
				|&nbsp;<span class="clickable-element" onclick="displaySettingSubBlock('deletePage');">Suppression</span>
				|&nbsp;<span class="clickable-element" onclick="displaySettingSubBlock('changeCMSProperties');">CMS</span>

				<hr/> 
				<div id="propsPage" style="display: none;">
				
					<form id="formRenamePage" method="get" action="<%= commandUrl %>">
					<input type="hidden" name="action" value="renamePage"/>
					<input type="hidden" name="pageId" value="<%= pageId %>"/>
						Nom de la page : <input type="text" name="displayName" value="<%= pageName %>"/> <input type="submit" value="Renommer"/>
					</form>
	
					<form id="formMakeDefault" method="get" action="<%= commandUrl %>">
					<input type="hidden" name="action" value="makeDefaultPage"/>
					<input type="hidden" name="pageId" value="<%= pageId %>"/>
					
<%
	if( "0".equals(defaultPage))	{					
%>					
										
						Faire de cette page la page d'accueil du portail <input type="submit" value="Appliquer"/>

					
<%
	} else	{					
%>		
						Cette page est la page d'accueil du portail.

<%
	} 				
%>	
					</form>
					

					
					<br/>
					<br/>
					
<%

String checkDraft = ""; 
if( "1".equals(draftPage))
	checkDraft = "checked";	


String checkCMSNavigationMode = ""; 
if( "1".equals(pageCMSNavigationMode))
	checkCMSNavigationMode = "checked";	

%>					
					
					<form id="formchangePageProperties" method="get" action="<%= commandUrl %>">
					
						<input type="hidden" name="action" value="changePageProperties"/>
						<input type="hidden" name="pageId" value="<%= pageId %>"/>
						
						<table>
						<th colspan="2"> Propriétés de la page</th>
						<tr>
								<td>
									Page brouillon (non exportée)
								</td>
								<td>
									<input type="checkbox" name="draftPage" value="1" <%=checkDraft%>/>
								</td>
							</tr>			
							
										
						
<% 
// v2.0.22 : catégories
if( categories != null) { %>						
						
					<tr>
								<td>
									Catégorie
								</td>
								<td>
						
						
						<select name="pageCategory">
<%
				for(String possibleCategory : categories.keySet()){
					
					String selected = "";
					
					if( possibleCategory.equals( category))	{
						selected = "selected=\"selected\"";
					}

%>
							<option  <%=selected %> value="<%= possibleCategory %>"><%= categories.get(possibleCategory) %></option>

<%						
				}
%>


					</select>
								</td>
							</tr>						
<%	} %>												
						
						
							<tr>
								<td colspan="2" align="center">
									<input type="submit" value="Changer les propriétés"/>
								</td>
							</tr>	
							</table>
						
					</form>
					
						
								
					
					
				</div>
				<div id="createPage" style="display: none;">
					<form id="fromCreatePage" method="get" action="<%= commandUrl %>">
						<input type="hidden" name="action" value="createPage"/>
						<input type="hidden" name="pageId" value="<%= pageId %>"/>
					
						<table border="0">
							<tr>
								<td>
									Cr&eacute;er une sous page
								</td>
								<td>
									<input type="radio" name="creationType" value="child"/>
								</td>
							</tr>
							<tr>
								<td>
									Cr&eacute;er une page de m&ecirc;me niveau
								</td>
								<td>							
									<input type="radio" name="creationType" value="sister"/>
								</td>
							</tr>
							<tr>
								<td>
									Nom de la nouvelle page :
								</td>
								<td>
									<input type="text" name="name" value=""/>
								</td>
							</tr>
							
							<tr>
							<td>
									Modèle de page :
								</td>
							
								<td>
									<select name="modeleId">
										<option value="0">Pas de modèle</option>									
<%
				for(Page model : models){
					String modelTitle = null;
					if( model.getDisplayName() != null)
						modelTitle = model.getDisplayName().getString(request.getLocale(), true);
					
					if( modelTitle == null)
						modelTitle = model.getName();

%>
										<option  value="<%= model.getId().toString(PortalObjectPath.SAFEST_FORMAT) %>"><%= modelTitle %></option>

<%						
				}
%>
									</select>
								</td>
							</tr>							
							<tr>
								<td colspan="2" align="center">
									<input type="submit" value="Cr&eacute;er la page"/>
								</td>
							</tr>
							
							
							
													
						</table>
					</form>
				</div>
				<div id="deletePage" style="display: none;">
					<form id="formDeletePage" method="post" action="<%= deletePageUrl %>">
						Etes-vous sur de vouloir supprimer cette page ?<br />
						<input type="submit" value="Oui"/> <input type="button" value="Non" onclick="displaySettingSubBlock('deletePage')"/>
					</form>
				</div>
				
				<div id="changeOrder" style="display: none;">
					<form id="formChangeOrder" method="get" action="<%= commandUrl %>">
					<input type="hidden" name="action" value="changePageOrder"/>
					<input type="hidden" name="pageId" value="<%= pageId %>"/>
					
	<table border="0">
							<tr>
								<td>
								
									<span>Sélectionner l'emplacement où insérer la page</span>
								</td>
							</tr>								
							<tr>
								<td>
									<select name="destinationId">
<%
				for(Page sister : sisters){
					String sisterTitle = null;
					if( sister.getDisplayName() != null)
						sisterTitle = sister.getDisplayName().getString(request.getLocale(), true);
					
					if( sisterTitle == null)
						sisterTitle = sister.getName();

%>
										<option  value="<%= sister.getId().toString(PortalObjectPath.SAFEST_FORMAT) %>"><%= sisterTitle %></option>

<%						
				}
%>
										<option value="0">[Fin de liste]</option>

									</select>
								</td>
							</tr>
							<tr>
								<td align="center">
									<input type="submit" value="Déplacer"/>
								</td>
							</tr>						
						</table>					
					</form>
				</div>
				
				
				
				
				<div id="changeLayout" style="display: none;">
					<form id="formChangeLayout" method="get" action="<%= commandUrl %>">
									
					<input type="hidden" name="action" value="changeLayout"/>
					<input type="hidden" name="pageId" value="<%= pageId %>"/>						
					
						<table border="0">
							<tr>
								<td>
									Layouts :
								</td>
								<td>
									<select name="newLayout">
<%
			if(listLayout != null && !listLayout.isEmpty()){
				if(currentLayout == null || currentLayout.length() == 0){
%>
										<option selected="selected" value="">Valeur par d&eacute;faut</option>
<%
				}else{
%>
										<option value="">Valeur par d&eacute;faut</option>
<%
				}
				for(PortalLayout portalLayout : listLayout){
					if(currentLayout != null && currentLayout.length() != 0 && portalLayout.getLayoutInfo().getName().equals(currentLayout)){
%>
										<option selected="selected" value="<%= portalLayout.getLayoutInfo().getName() %>"><%= portalLayout.getLayoutInfo().getName() %></option>
<%
					}else{
%>
										<option value="<%= portalLayout.getLayoutInfo().getName() %>"><%= portalLayout.getLayoutInfo().getName() %></option>
<%						
					}
				}
			}
%>
									</select>
								</td>
							</tr>
							<tr>
								<td colspan="2" align="center">
									<input type="submit" value="Changer le layout"/>
								</td>
							</tr>						
						</table>
					</form>
				</div>
	
				<div id="securePage" style="display: none;">
					<form id="formRenamePage" method="get" action="<%= commandUrl %>">
									
					<input type="hidden" name="action" value="securePage"/>
					<input type="hidden" name="pageId" value="<%= pageId %>"/>						
						<table border="0">
							<thead>
								<tr>
									<th>
										R&ocirc;les
									</th>
									<th>
										Accès à la page
									</th>
								</tr>
							</thead>
							<tbody>
<%
			for(Role role : roleAvaible){
				Set<String> actions = null;
				if(actionsForRole.containsKey(role.getName())){
					actions = actionsForRole.get(role.getName());
				}
%>
								<tr>
									<td>
										<%= role.getDisplayName() %>
									</td>
									<td>
										<% if(actions != null && !actions.isEmpty() && actions.contains("view")){ %>
										<input type="checkbox" name="view" value="<%= role.getName() %>" checked="checked"/>
										<% }else{ %>
										<input type="checkbox" name="view" value="<%= role.getName() %>"/>
										<% } %>
									</td>
									</tr>
<%
			}
%>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="6" align="center">
										<input type="submit" value="Modifier les droits"/>
									</td>
								</tr>
							</tfoot>
						</table>
					</form>
				</div>

				
				
				<div id="changeCMSProperties" style="display: none;">
					<form id="formCMSProperties" method="get" action="<%= commandUrl %>">
						<input type="hidden" name="action" value="changeCMSProperties"/>
						<input type="hidden" name="pageId" value="<%= pageId %>"/>
					
					

							<table border="0">
						

							<tr>
								<td width="300px">
									Scope de la page :
								</td>
								<td>
										<%= pageCmsScopeSelect %><br/>
								</td>
							</tr>	
							
							<tr>
								<td>
									Versions des contenus de la page :
								</td>
								<td>
										<%= pageCmsDisplayLiveVersionSelect %><br/>
								</td>
							</tr>	
							
							<tr>
								<td>
									Contextualiser les liens sortants dans le portail
								</td>
								<td>
										<%= outgoingRecontextualizationSupport %><br/>
								</td>
							</tr>									



							</table>

							




<script language="javascript"> 


/*function togglePublication() {
	var ele = document.getElementById("toggleDynamicCMS");
	var text = document.getElementById("displayDynamicCMS");
	if(ele.style.display == "block") {
    		ele.style.display = "none";
  	}
	else {
		ele.style.display = "block";
	}
}*/

function disableOrNotPreviousFormValues(cmsPathInput){
	var cmsForm = document.forms["formCMSProperties"];
	if(cmsPathInput.value != ''){
		cmsForm.elements["scope"].disabled = true;
		cmsForm.elements["displayLiveVersion"].disabled = true;
		cmsForm.elements["outgoingRecontextualizationSupport"].disabled = true;
	} else {
		cmsForm.elements["scope"].disabled = false;
		cmsForm.elements["displayLiveVersion"].disabled = false;
		cmsForm.elements["outgoingRecontextualizationSupport"].disabled = false;
	}
}

</script>
							
							
							<!-- <table>

							<tr><td><a id="displayDynamicCMS" href="javascript:togglePublication();">Publication dynamique</a></td></tr>

							<tr><td colspan="2"><div id="toggleDynamicCMS" style="display: none; border: 1px; border-style: solid">
							<tr><td colspan="2"><div id="toggleDynamicCMS" border: 1px; border-style: solid">
							
							<i>Dans le cas d'une publication dynamique, les paramètres ci-dessus (version, scope, contextualisation) seront ignorés et dynamiquement extraits de l'ECM</i><br/>-->
							
							<table>
													
							<tr>
								<td width="300px">
									Path de l'espace de publication : 
									<br/> 
								</td>
								<td>
									<input type="text" name="cmsBasePath" size="50" value="<%=pageCmsBasePath%>" 
																		onKeyup="javascript:disableOrNotPreviousFormValues(this);"
																		onBlur="javascript:disableOrNotPreviousFormValues(this);"/>
								</td>
							</tr>
							

						
							<tr>
								<td>
									Afficher directement les sous-rubriques dans la navigation :
								</td>
								<td>
										<input type="checkbox" name="cmsNavigationMode" value="1" <%=checkCMSNavigationMode%>/>
								</td>
							</tr>
							
							
				
							<!-- </table></div></td></tr>-->					
													
							
							<tr>
								<td colspan="2" align="center">
									<input type="submit" value="Valider" onMouseOver="javascript:disableOrNotPreviousFormValues(this.form['cmsBasePath']);"/>
								</td>
							</tr>
							
							
							
													
						</table>
					</form>
				</div>			
				
				
				
				
				
				
			</div>
		</div>
		
		
<%	} // Fin page %>
		
		
		
		

<% if( windows != null)	{	 %>

<!-- WINDOWS -->	


	<script type="text/javascript">
	
		var messageModalWindow = new DHTML_modalMessage();

		function displayWindowSettings( windowID){
			messageModalWindow.setHtmlContent($("modal-window-"+windowID+"-settings").innerHTML);
			messageModalWindow.display();
		}

		function closeWindowSettings(){
			messageModalWindow.close();
		}
		

		function displayWindowDelete( windowID){
			messageModalWindow.setHtmlContent($("modal-window-"+windowID+"-delete").innerHTML);
			messageModalWindow.display();
		}
		
		function closeWindowDelete(){
			messageModalWindow.close();
		}
		


		
		function toggleStyle( comp) {
			var ele = $(comp).up('div').down('.toggleStyle');
			
			
			if(ele.style.display == "block") {
	    		ele.style.display = "none";
	  		}
			else {
			ele.style.display = "block";
			}
	
		} 

	</script>	
	
		
		
		
		
<%



		for(Window window : windows){
			
			String windowId = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);
			
			
			
			/* Récupération des styles */
			
			String stylesProp = window.getDeclaredProperty("osivia.style");
			String[] styles = new String[0];
			if (stylesProp != null)
				styles = stylesProp.split(",");
			// Conversion en tableau
			List<String> windowStyles = new ArrayList<String>();
			for (int i = 0; i < styles.length; i++)
				windowStyles.add(styles[i]);
			
			
			
			
			String checkTitle = "checked"; 
			if( "1".equals(window.getDeclaredProperty("osivia.hideTitle")))
				checkTitle = "";	
			String title = ""; 
			if( window.getDeclaredProperty("osivia.title") != null)
				title = window.getDeclaredProperty("osivia.title");		
				
			String checkDecorators = "checked";
			if("1".equals(window.getDeclaredProperty("osivia.hideDecorators")))
				checkDecorators = "";
			

			String ajaxLink = "";
			if( "1".equals( window.getProperty("osivia.ajaxLink")))
				ajaxLink = "checked";

			String printPortlet = "";
			if( "1".equals( window.getProperty("osivia.printPortlet")))
				printPortlet = "checked";

			
			String idPerso = ""; 
			if( window.getDeclaredProperty("osivia.idPerso") != null)
				idPerso = window.getDeclaredProperty("osivia.idPerso");		
			
			String hideEmptyPortlet = "";
			if( "1".equals( window.getProperty("osivia.hideEmptyPortlet")))
				hideEmptyPortlet = "checked";
			
			String conditionalScope = window.getProperty("osivia.conditionalScope");
			

			String bshActivation = ""; 
			if( "1".equals(window.getDeclaredProperty("osivia.bshActivation")))
				bshActivation = "checked";	
			
			String bshScript =  window.getProperty("osivia.bshScript");
			if( bshScript == null)
				bshScript = "";
			
			
			String cacheID =  window.getProperty("osivia.cacheID");
			if( cacheID == null)
				cacheID = "";
	

			
			String checkSelectionDep =  "";
			if( "selection".equals(window.getDeclaredProperty("osivia.cacheEvents")))
				checkSelectionDep = "checked";	
			
%>


	<div id="modal-window-<%= windowId %>-settings" style="display: none;" 	>
	
<%
			String windowTitle = "";
		
			if( window.getDeclaredProperty("osivia.title") != null)
				windowTitle = window.getDeclaredProperty("osivia.title");		
			
			/* Ajout nom instance */

			String instanceName = null;
			for(InstanceDefinition instance : portletDefinitions){
				if( instance.getId().equals(window.getContent().getURI()))	{
		 		instanceName = instance.getDisplayName().getString(request.getLocale(), true);
				}
			}
			
			if( instanceName != null)
			
				windowTitle += "     ["+instanceName+"]";


%>	
	
			<div class="close-settings clickable-element" onclick="closeWindowSettings();"><span class="title-settings">Paramètres <%= windowTitle %></span><img src="<%= request.getContextPath() %>/images/blank.png"/></div>
	
	<div class="wizzard-window-content">

		<form id="formSetting<%= windowId %>" method="get" action="<%= commandUrl %>">
		
					<input type="hidden" name="action" value="changeWindowSettings"/>
					<input type="hidden" name="windowId" value="<%= windowId %>"/>
					
						<table border="0">
							<tr>
								<td colspan = "2">
	
<%
			Set<String> possibleStyles = new HashSet<String>( portalStyles);

			String displayStyles = "";
			for( String windowStyle : windowStyles)	{
				displayStyles += windowStyle + " ";
			}
			
			if( "".equals(displayStyles))
				displayStyles = "aucun";

			possibleStyles.addAll( portalStyles);
			possibleStyles.addAll( windowStyles);
%>			


						<span class="wizzard-label"> Styles </span>
						 <span class="wizzard-value">

							  <b><%= displayStyles %></b>	
							
							(<a href="#" onclick="toggleStyle(this);">Modifier</a>)</span><br/>
		
							<div class="toggleStyle" style="display: none; border: 1px; border-style: solid">							
									
							<table border="0">
							<tbody>
<%

			for(String possibleStyle : possibleStyles){
%>				
								<tr>
									<td width="200px">
										<%= possibleStyle %>
									</td>
									<td>
										<% if( windowStyles.contains(possibleStyle)){ %>
										<input type="checkbox" name="style" value="<%= possibleStyle %>" checked="checked"/>
										<% }else{ %>
										<input type="checkbox" name="style" value="<%= possibleStyle %>"/>
										<% } %>
									</td>
									</tr>
<%
			}
%>
							</tbody>
							</table>
						</div>

						
						
						
									
								</td>
							</tr>
							
							
		
					
							
							
							
							<tr>
								<td width="200px">
									Affichage barre de titre :
								</td>
								<td>
									<input type="checkbox" name="displayTitle" value="1" <%=checkTitle%>/>
								</td>
							</tr>
							<tr>
								<td>
									Titre : 
								</td>
								<td>
									<input name="title" value="<%=title%>" />
								</td>
							</tr>
							
							<tr>
								<td>
									Affichage des icônes :
								</td>
								<td>
									<input type="checkbox" name="displayDecorators" value="1" <%=checkDecorators %>/>
								</td>
							</tr>

							
							<tr>
								<td>
									Liens et formulaires en AJAX :
								</td>
								<td>
									<input type="checkbox" name="ajaxLink" value="1" <%=ajaxLink%>/>
								</td>
							</tr>
							
							<tr>
								<td>
									Impression
								</td>
								<td>
									<input type="checkbox" name="printPortlet" value="1" <%=printPortlet%>/>
								</td>
							</tr>


							<tr>
								<td>
									Masquer ce portlet si contenu vide
								</td>
								<td>
									<input type="checkbox" name="hideEmptyPortlet" value="1" <%=hideEmptyPortlet%>/>
								</td>
							</tr>

	
							<tr>
								<td>
									Affichage conditionné au profil :
								</td>
								<td>
									<%= formatter.formatPortletFilterScopeList("conditionalScope", conditionalScope) %>
								</td>
							</tr>
							
							
	
							<tr>
								<td>
									Id. personnalisation : 
								</td>
								<td>
									<input name="idPerso" value="<%=idPerso%>" />
								</td>
							</tr>


							<tr>
								<td>
									Id. cache user mutualisé : 
								</td>
								<td>
									<input name="cacheID" value="<%=cacheID%>" />
								</td>
							</tr>
							
							
							
							<tr>
								<td>
									Dépendance / service sélection : 
								</td>
								<td>
									<input type="checkbox" name="selectionDep" value="1" <%=checkSelectionDep%>/>
								</td>
							</tr>
							
							
		
					<tr>
						<td colspan="2">

<%


			String dynamicStyleLabel = "-";
			if( "checked".equals( bshActivation))	
				dynamicStyleLabel = "script shell";
			
%>			

						
						<div>
						
						<span class="wizzard-label"> Propriétés dynamiques </span>
						 <span class="wizzard-value"> <b><%= dynamicStyleLabel %> </b>(<a href="#" onclick="toggleStyle(this);">Modifier</a>)<br/></span>
						
		
							<div class="toggleStyle" style="display: none; border: 1px; border-style: solid; ">							
							
							<input type="checkbox" name="bshActivation" value="1" <%=bshActivation%>/> Activer le script beanshell 
							<br/>
							
							<textarea rows="10" cols="75" name="bshScript" ><%= bshScript %></textarea><br/><br/>
									
							
							<div>
							Exemple
							 <pre>
/*
implicits variables :
   - pageParamsEncoder : parameters encoder (decoded to List&lt;String&gt;)
   - windowsProperties : paramètres dynamique de la window (Map&lt;String,String&gt;)
        > osivia.dynamicCSSClasses : css class names separated by a space (eq : "css1 css2")
      
*/	
							 
import java.util.List;

List cssSelectorValues =  pageParamsEncoder.decode("selectors", "cssSelector");

if( cssSelectorValues != null)	{
   windowProperties.put("osivia.dynamicCSSClasses", cssSelectorValues.get(0));   
 }
   


							</pre>

							</div>
							
						</div>	
						</div>
						
						
						
									
								</td>
							</tr>
							
							
									
							
							<tr>
								<td colspan="2" align="center">
									<input type="submit" value="Changer les paramètres"/>
								</td>
							</tr>						
						</table>
						
					</form>
					
			</div>

	</div>
	
	
	


<div id="modal-window-<%= windowId %>-delete" style="display: none;">
		<div class="close-settings clickable-element" onclick="closeWindowDelete();"><img src="<%= request.getContextPath() %>/images/blank.png"/></div>
	
		<div class="wizzard-delete-content">

		<br/><br/>
		<form id="formDelete<%= windowId %>" method="get" action="<%= commandUrl %>">
		
					<input type="hidden" name="action" value="deleteWindow"/>
					<input type="hidden" name="windowId" value="<%= windowId %>"/>
					

					Etes-vous sur de vouloir supprimer ce portlet ?<br />
					<input type="submit" value="Oui"/> <input type="button" value="Non" onclick="closeWindowDelete()"/>

						
		</form>
		
		</div>

	</div>
<%				
				
		}
		
} // if( windows != null)
%>




<!--  fin window  -->		
			

<!-- ADD PORTLET -->		

	<script type="text/javascript">
	
		var messageModaladdPortal = new DHTML_modalMessage();

		function displayAddPortlet( regionID){
			$("modal-window-add-portlet-regionid").setValue(regionID);
			messageModaladdPortal.setHtmlContent($("modal-window-add-portlet").innerHTML);
			messageModaladdPortal.display();
		}
		function closeAddPortlet(){
			messageModaladdPortal.close();
		}
		function selectPortlet( instanceId, formulaire){
			formulaire.instanceId.value = instanceId;
			formulaire.submit();
		}

		
	</script>	

<%

if( portletDefinitions != null)	{
	
%>

	<div id="modal-window-add-portlet" style="display: none;">
			<div class="close-settings clickable-element" onclick="closeAddPortlet();"><img src="<%= request.getContextPath() %>/images/blank.png"/></div>
	
		<div style="height:200px; overflow: auto; overflow-x: hidden; border: 1px solid #333;">
		
		<form name="addPortlet" id="formAddPortlet" method="get" action="<%= commandUrl %>">
		
					<input type="hidden" name="action" value="addPorlet"/>
					<input type="hidden" name="pageId" value="<%= pageId %>"/>
					<input id="modal-window-add-portlet-regionid" type="hidden" name="regionId" value="REGIONID"/>
					<input type="hidden" id ="modal-window-add-portlet-instanceid" name="instanceId" value="rssInstance"/>
					
						<table border="0" cellpadding="0" cellspacing="0">
	
<%


	int i=0;

 	for(InstanceDefinition instance : portletDefinitions){
 		
		try	{
			 instance.getPortlet();
			}
		catch( Exception e)	{
			//Portlet non déployé
			continue;
		}
		
	
 		
 		String instanceName = instance.getDisplayName().getString(request.getLocale(), true);
 		if( instanceName == null)
 			instanceName = instance.getId();
 		
 	
		
 		i++;
 		String style = "";
 		if( i % 2 == 0)
 			style = "style=\"background-color:#F2F2F2;	border:1px solid #D5D5D5;\"";
			
%>							
							
							<tr <%= style %>>
								<td align="left">
<% 
		
		String icon = null;
		
		PortletInfo info = instance.getPortlet().getInfo();
		PortletInfoInfo portletInfo = info.getAttachment(PortletInfoInfo.class);
		String iconType = PortletIconInfo.SMALL;
	
		if (portletInfo != null)  {
			
            if (portletInfo != null && portletInfo.getPortletIconInfo() != null && portletInfo.getPortletIconInfo().getIconLocation(iconType) != null)
            {
               icon = portletInfo.getPortletIconInfo().getIconLocation(iconType);
            }
    	}	
	
		
		if( icon != null)	{
%>		
									<img src="<%= icon %>" align="absmiddle" border="none" style="margin: 0pt 4px 0pt 0pt"/>
<%			
		} else {
%>

									<img src="/portal-core/images/portletIcon_Default1.gif" align="absmiddle" border="none" style="margin: 0pt 4px 0pt 0pt;"/>
<%			
		}
%>								
								</td>
								
								<td align="left">
<%	
	String displayName = instance.getDisplayName().getString(request.getLocale(), true);
	if( displayName == null)
		displayName = instance.getId();
%>
									<span>  <%= displayName %></span>
								</td>

								<td align="left">
									<input type="submit" value="Ajouter" onclick="selectPortlet('<%=instance.getId()%>', this.form )"/>								
								</td>
							</tr>	
							
<%
	} // for(InstanceDefinition instance : portletDefinitions){
%>	
							
						</table>
						
					</form>
			</div>

	</div>
		
<%

	} // if( portletDefinitions != null)
%>



<div class="wizzardMenu">


<%
    // ie not a template
	if( pageName != null && pageName.length() != 0){
%>

	<div class="wizzardSettings clickable-element" onclick="displaySettings();">Configuration de la page</div>

<%
	}
%>



<%
	if( templateUrl != null)	{
%>
	<div class="templateEdit clickable-element" ><a href="<%=templateUrl %>" >Accéder au template</a></div>

<%
	}
%>
<%
	String nav = (String)request.getAttribute("osivia.setting.navigation");
%>

<script type="text/javascript">
		var messageModalNav = new DHTML_modalMessage();
		function displayNav(){
			if($("modal-nav")){
				messageModalNav.setHtmlContent($("modal-nav").innerHTML);
				$("modal-nav").remove();
				messageModalNav.setShadowDivVisible(false);
			}
			messageModalNav.display();
		}
		function closeNav(){
			messageModalNav.close();
		}

	</script>



	<div class="wizzardNav clickable-element" onclick="displayNav();">Liste des pages</div>
	<div id="modal-nav" style="display: none;">
			<div class="close-settings clickable-element" onclick="closeNav();"><img src="<%= request.getContextPath() %>/images/blank.png"/><span class="title-settings">Liste des pages</span></div>
			<div class="wizzard-navigation-content">
				<%= nav %>
			</div>
	</div>
	
	
<%
	String initCacheUrl = (String)request.getAttribute("osivia.setting.initCachesUrl");
%>
	<div class="wizzardCacheInit clickable-element" ><a href="<%=initCacheUrl %>" >Initialisation des caches</a></div>

		
</div>







