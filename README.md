# alfresco-ssofilter
Alfresco DMS's SSO filter

# Pre requisites
- Alfresco community - Tested on Community - 5.2.0 (r135134-b14)
- Java 1.8
- Maven (Tested on v 3.3.9)

#Build jar file
 - navigate to location of this README.txt
 - run `mvn clean compile package`
 - a package ssofilter.jar will be found at ./target
 
 
#Installation
- Configure your alfresco to use SSO (too lazy to document this at the moment -  sorry) 
- Copy ssofilter.jar to $ALFRESCO_HOME/tomcat/lib/
- Add a filter entry to $ALFRESCO_HOME/tomcat/conf/web.xml


