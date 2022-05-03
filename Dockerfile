FROM maven:3.6.0-jdk-11-slim AS build

COPY . /webprotege

WORKDIR /webprotege

FROM tomcat:8-jre11-slim

RUN apt-get update && \
    apt-get install -y git

RUN rm -rf /usr/local/tomcat/webapps/* \
    && mkdir -p /srv/webprotege \
    && mkdir -p /usr/local/tomcat/webapps/ROOT

WORKDIR /usr/local/tomcat/webapps/ROOT

# Here WEBPROTEGE_VERSION is coming from the custom build args WEBPROTEGE_VERSION=$DOCKER_TAG hooks/build script.
# Ref: https://docs.docker.com/docker-hub/builds/advanced/
#ARG WEBPROTEGE_VERSION

#ENV WEBPROTEGE_VERSION $WEBPROTEGE_VERSION
COPY --from=build /webprotege/webprotege-cli/target/webprotege-cli-5.0.0-SNAPSHOT.jar /webprotege-cli.jar
COPY --from=build /webprotege/webprotege-server/target/webprotege-server-5.0.0-SNAPSHOT.war ./webprotege.war
RUN unzip webprotege.war \
    && rm webprotege.war
