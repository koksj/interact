
# JCA 1.7 adapter interact.soap.jca



Must have the below entry for web services in the pom.xml file if not, the SOAP call to interact from the java connector interact.soap.jca WILL fail with errors in the logs like below:

```	... 102 more
Caused by: java.lang.ClassNotFoundException: com.unicacorp.interact.api.soap.InteractService from [Module "deployment.interact.soap.jca.rar" from Service Module Loader]
	at org.jboss.modules.ModuleClassLoader.findClass(ModuleClassLoader.java:198)
	at org.jboss.modules.ConcurrentClassLoader.performLoadClassUnchecked(ConcurrentClassLoader.java:412)
	at org.jboss.modules.ConcurrentClassLoader.performLoadClass(ConcurrentClassLoader.java:400)
	at org.jboss.modules.ConcurrentClassLoader.loadClass(ConcurrentClassLoader.java:116)
	... 135 more


Caused by: java.lang.NoClassDefFoundError: Failed to link com/unicacorp/interact/api/soap/InteractService (Module "deployment.interact.soap.jca.rar" from Service Module Loader): javax/xml/ws/Service
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at sun.reflect.DelegatingConstructorAccessorImpl.newIns
```


## Required POM entry for Thorntail  

```xml
<dependency>
 <groupId>io.thorntail</groupId>
 <artifactId>webservices</artifactId>
</dependency>
```