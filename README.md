# alfresco-ssofilter
Alfresco DMS's SSO filter

# How to build
1. mvn clean compile package


#Installation
1. copy the compiled jar - `./target/ssofilter.jar` to $ALFRESCO_HOME/tomcat/lib/
2. Edit the `web.xml` located at  $ALFRESCO_HOME/tomcat/conf

Add these lines after other filter declarations;

	<filter>
	    <filter-name>Demo Filter</filter-name>
	    <filter-class>technovation.alfresco.ssofilter.SSOIntegrationFilter</filter-class>
	</filter>
	
	<filter-mapping>
	    <filter-name>Demo Filter</filter-name>
	    <url-pattern>*</url-pattern>
	</filter-mapping>

	
Save file. Restart alfresco; `$ALFRESCO_HOME/alfresco.sh restart`



# Costomizing Alfresco

1. $ALFRESCO_HOME/

2. $ALFRESCO_HOME/tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml

3. $ALFRESCO_HOME/tomcat/webapps/share/WEB-INF/classes/alfresco/site-webscripts/org/alfresco/share/header/share-header.get.js


Hide stuff (top menu, left trees, footer)

https://community.alfresco.com/thread/209756-hide-menu-tree-menu
$ALFRESCO_HOME/tomcat/webapps/share/WEB-INF/classes/alfresco/templates/org/alfresco/documentlibrary.ftl
$ALFRESCO_HOME/tomcat/webapps/share/WEB-INF/classes/alfresco/templates/org/alfresco/repository.ftl
$ALFRESCOH_HOME/tomcat/webapps/share/WEB-INF/classes/alfresco/templates/org/alfresco/document-details.ftl

in all above files, add the following to;
alf-hd
alf-ft
GUGAMUGA" style="display:none; width:0px;"

then

$ALFRESCO_HOME/tomcat/webapps/share/WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/node-details/node-header.get.html.ftl
change line 77 which looks like;
<div class="node-path">
to
<div class="node-pathGUGAMUGA" style="display:none; width:0px;">


Change default re-direct page after login in
$ALFRESCO_HOME/tomcat/webapps/share/site-index.jsp
Change line #53 to look like below;
response.sendRedirect(request.getContextPath() + "/page/repository");


#Remove navbar from repository view
$ALFRESCO_HOME/tomcat/webapps/share/WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist_v2.lib.ftl
Change  line 196 from
<div id="${id}-navBar" class="nav-bar flat-button theme-bg-2">
to
<div  style="display:none;width:0px">


#ref
--Removing the path of documets:
https://community.alfresco.com/thread/183387-remove-links-from-node-header-in-document-details
--Default home page
http://stackoverflow.com/questions/11226357/is-there-anyway-so-that-when-i-login-to-alfresco-instead-going-to-dashboard-it
https://community.alfresco.com/thread/194923-share-change-default-page-after-login

