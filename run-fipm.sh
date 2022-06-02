#mvn clean install package -Dmaven.test.skip=true; java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n  -Dswarm.project.stage=local -jar target/fipm-thorntail.jar 
mvn clean install package -Dmaven.test.skip=true; java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -Dswarm.project.stage=local -jar centilliard.io.fipm/target/fipm-thorntail.jar 
#mvn clean install package -Dmaven.test.skip=true; java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -Dswarm.project.stage=skok -jar centilliard.io.fipm/target/fipm-thorntail.jar 
