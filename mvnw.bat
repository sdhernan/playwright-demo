set JAVA_HOME=D:\Tools\02.- Java\jdk-11.0.24
echo %JAVA_HOME%
set PATH=%JAVA_HOME%\bin;%PATH%

SET MAVEN_HOME=D:\Tools\01.-Maven\apache-maven-3.6.0
echo %MAVEN_HOME%
set PATH=%MAVEN_HOME%\bin;%PATH%

mvn  clean -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true verify