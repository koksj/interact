# FIPM manager 

This manager is implemented on Thorntail 2.2.0.Final. To run this manger it is recommend you run it the CVMI root directory above this directory. 

To run the binary:

```
java -Dswarm.project.stage=local -jar centilliard.io.fipm/target/fipm-thorntail.jar

```

To build and run and debug :

```
mvn clean install package -Dmaven.test.skip=true; java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -Dswarm.project.stage=local -jar centilliard.io.fipm/target/fipm-thorntail.jar

mvn clean install package -Dmaven.test.skip=true; java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -Dswarm.project.stage=prod -jar centilliard.io.fipm/target/fipm-thorntail.jar


OR

mvn thorntail:run

```

## Stages 

Please note the java parameter "-Dswarm.project.stage=local" in the example above. This instructs Thorntail to start the appropriate  environment for you. This parameter relates to files which can be found  the "src/main/resources/" directory relative to this file.

For example: 

```
-Dswarm.project.stage=local   /* File: src/main/resources/project-local.yml  */ 
-Dswarm.project.stage=dev     /* File: src/main/resources/project-dev.yml  */
-Dswarm.project.stage=qa      /* File: src/main/resources/project-qa.yml  */ 
-Dswarm.project.stage=prod    /* File: src/main/resources/project-prod.yml  */
```
 
 You can also create an environment for yourself based on the above files you deem appropriate for your situation.

## Application configurable parameters

FIPM uses the Eclipse MicroProfile Config standard if you would like to read more please see:
`https://www.eclipse.org/community/eclipse_newsletter/2017/september/article3.php`
 
 FIPM use the Thorntail implementation below:

### Dependencies

```
       <dependency>
			<groupId>io.thorntail</groupId>
			<artifactId>microprofile-config</artifactId>
		</dependency>
```

### Application Configuration

There are two possible ways of specifying the key and value pairs.  The first is using the `microprofile-config.properties` file.
The second is files of which the keys are the file names.


Using the `src/main/resources/META-INF/microprofile-config.properties` file 

```
config_ordinal = 100
fipm.environment.configuration = Local
interact.interactive.channel   = UPSIZE 
nde.manager = http://192.168.2.38:8080
nde.param.offer.advice = offerAdvice
nde.sub.type = ANY
interact.interaction.point = J4UU_PRE_FI
nde.campaign.type = Campaign
nde.notification.source = IBM Campaign
nde.payment.method = P
nde.template.id = MessageTemplate448
```

The configurable properties of the FIPM manager is listed below. Please note the file names are the keys and the value is the content of the file.

`config/local/*`

```
fipm.environment.configuration
interact.interactive.channel  
nde.manager
nde.param.offer.advice
nde.sub.type
interact.interaction.point
nde.campaign.type
nde.notification.source
nde.payment.method
nde.template.id
```

For the files method to work there is a config as shown below in the relevant stages file. For example the `project-local.yml` and 
`project-prod.yml`.

```
swarm:
 microprofile:
    config:
      config-sources:
         env-config:
           dir: centilliard.io.fipm/config/local/
           ordinal: 200
```

In the above example note the `ordinal: 200`  entry. Should you populate the `microprofile-config.properties`  as shown above. Then the values will be loaded from the content of the
files found in `centilliard.io.fipm/config/local/` **because** the `ordinal:` 200 is **higher** than `config_ordinal = 100` found in the `microprofile-config.properties` file.  

## Logging

### Dependencies

FIPM makes use of SLF4J 

```
       <dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>1.7.25</version>
		</dependency>
```

### Configuration

Logging configuration via the stages files and/or `project-defaults.yml`

```
swarm:  
  logging:
    pattern-formatters:
      NORMAL:
        pattern: "%p [%c] %s%e%n"
      EXCEPTION:
        pattern: "%p [%c.%m] %s%e%n"
    periodic-rotating-file-handlers:
      DEBUGLOG:
        file:
          path: centilliard.io.fipm/logs/fipm.log
        suffix: .yyyy-MM-dd
        named-formatter: NORMAL
        level: DEBUG
      EXCEPTIONLOG:
        file:
          path: centilliard.io.fipm/logs/exception.log
        suffix: .yyyy-MM-dd
        named-formatter: EXCEPTION
        level: WARN
    root-logger:
      handlers:
      - CONSOLE
      - DEBUGLOG
      - EXCEPTIONLOG
    loggers:
      cvmi.fipm:
        level: DEBUG  
      ibm.interact.soap:
        level: DEBUG
```

It is highly recommended to configure the prod stage logging with a asynchronous logger.  

## Logstash

### Dependencies

`pom.xml`

```
   <dependency>
	 <groupId>io.thorntail</groupId>
	 <artifactId>logstash</artifactId>
	</dependency>
```

### Configuration


```
SWARM:
  logstash:
    hostname: localhost
    port: 9300
    level: DEBUG
```


LOGSTASH_HOME/config/logstash-wildfly.conf

```
input {
  tcp {
    port => 9300
  }
}

filter {
  json {
    source => "message"
  }
}

output {
  stdout { codec => rubydebug }
}

```



## Microprofile-metrics

Metrics exposed at: http://127.0.0.1:8081/metrics.  The exposed metrics can be monitored by Prometheus.


### Dependencies

pom.xml

```
  <dependency>
	 <groupId>io.thorntail</groupId>
	<artifactId>microprofile-metrics</artifactId>
  </dependency>
```

### Configuration

The port number is controlled via the stages files and/or `project-defaults.yml`

```
swarm:
  http:
    port: 8081
```
 

 
 

## Management

**Not configured**

http://127.0.0.1:9990/management  /*URL will display an outline with null values*/

### Dependencies

pom.xml

```
<dependency>
  <groupId>io.thorntail</groupId>
  <artifactId>management</artifactId>
</dependency>
```

###  Management Console

**Not configured**

### Dependencies
pom.xml

```
 <dependency>
   <groupId>io.thorntail</groupId>
   <artifactId>management-console</artifactId>
 </dependency>
```


## Oracle jdbc

**Oracle Maven Repo Setup**

`https://blogs.oracle.com/dev2dev/get-oracle-jdbc-drivers-and-ucp-from-oracle-maven-repository-without-ides`

### Dependencies

pom.xml

```
      <dependency>
			<groupId>com.oracle.jdbc</groupId>
			<artifactId>ojdbc8</artifactId>
			<version>12.2.0.1</version>
	 </dependency>
```

## Data sources


### Dependencies

```
   <dependency>
	 <groupId>io.thorntail</groupId>
	 <artifactId>datasources</artifactId>
	</dependency>
```

### Configuration

Stages files and/or `project-defaults.yml`

```
swarm:
  data-sources:
      MyDS:
        driver-name: oracle
        connection-url: jdbc:oracle:thin:@mq.centtilliard.io:1521:mccmdev
        user-name: xxxxxxx
        password:  xxxxxxx
        valid-connection-checker-class-name: org.jboss.jca.adapters.jdbc.extensions.oracle.OracleValidConnectionChecker
        validate-on-match: true
        background-validation: false
        stale-connection-checker-class-name: org.jboss.jca.adapters.jdbc.extensions.oracle.OracleStaleConnectionChecker
        exception-sorter-class-name: org.jboss.jca.adapters.jdbc.extensions.oracle.OracleExceptionSorter
```

## JCA 1.7 compliant interact.soap.jca outbound connector

The deployment  deployment descriptor for this jca connector can be found in the relevant yml file for example:
`src/main/resources/project-local.yml`


### Configuration

 TODO: explain the makeup of the descriptor string below.

```
swarm:
  deployment:
    ibm.soap.interact:interact.soap.jca.rar:
    
```

For this connector to work you must have the below entry for web services in the pom.xml file.

Required POM entry for Thorntail:

### Dependencies


```
<dependency>
 <groupId>io.thorntail</groupId>
 <artifactId>webservices</artifactId>
</dependency>
```

If you don't the SOAP call to interact from the java connector interact.soap.jca **WILL**  fail with errors in the logs like below:

```
	... 102 more
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

## JCA 1.7 compliant eclipse.restclient.jca outbound connector


## Appendix Thorntail Documentation

http://docs.wildfly-swarm.io/

## Interact Environments

QA: http://inter.io.centilliard.corp:9081/interact/services/InteractService

