FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM tomcat:10.1-jdk21-temurin

ENV PORT=10000
ENV CATALINA_OPTS="-Dorg.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource"

# Render supplies PORT; Tomcat must listen on that public port.
RUN sed -i 's/port="8080"/port="${PORT:-10000}"/' /usr/local/tomcat/conf/server.xml

# Deploy the WAR at the domain root instead of /sql-gateway.
COPY --from=build /workspace/target/sql-gateway.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]