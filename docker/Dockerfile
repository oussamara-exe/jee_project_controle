# ============================================================
# Stage 1: Build du WAR avec Maven
# ============================================================
FROM maven:3.9-eclipse-temurin-8 AS builder

WORKDIR /build

# Copier uniquement les fichiers de dépendances d'abord (meilleur cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Copier le code source et compiler
COPY src ./src
RUN mvn clean package -DskipTests -B -q

# ============================================================
# Stage 2: Image WildFly avec MySQL et l'application
# ============================================================
# Image WildFly JDK 11 (tag fixe supprimé sur Quay, utiliser le tag flottant)
FROM quay.io/wildfly/wildfly:latest-jdk11

USER root

# Installer le driver MySQL comme module WildFly (mysql-connector-j pour MySQL 8+)
ENV MYSQL_CONNECTOR_VERSION=8.0.33
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar /tmp/mysql-connector.jar

RUN MYSQL_JAR="mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar" && \
    mkdir -p ${JBOSS_HOME}/modules/system/layers/base/com/mysql/main && \
    mv /tmp/mysql-connector.jar ${JBOSS_HOME}/modules/system/layers/base/com/mysql/main/${MYSQL_JAR} && \
    printf '%s\n' '<?xml version="1.0" encoding="UTF-8"?>' \
    '<module xmlns="urn:jboss:module:1.9" name="com.mysql">' \
    '  <resources><resource-root path="'${MYSQL_JAR}'"/></resources>' \
    '  <dependencies><module name="javax.api"/><module name="javax.transaction.api"/></dependencies>' \
    '</module>' > ${JBOSS_HOME}/modules/system/layers/base/com/mysql/main/module.xml

# Enregistrer le driver JDBC (embed-server pour config au build)
RUN ${JBOSS_HOME}/bin/jboss-cli.sh --commands="embed-server --server-config=standalone.xml",\
"/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-class-name=com.mysql.cj.jdbc.Driver)",\
"stop-embedded-server"

# Répertoire pour les scripts Docker
RUN mkdir -p ${JBOSS_HOME}/docker

# Copier le template CLI et l'entrypoint (contexte = racine du projet)
COPY docker/wildfly-datasource.cli.template ${JBOSS_HOME}/docker/datasource.cli.template
COPY docker/entrypoint.sh ${JBOSS_HOME}/docker/entrypoint.sh
RUN chmod +x ${JBOSS_HOME}/docker/entrypoint.sh

# Copier le WAR depuis le stage builder
COPY --from=builder /build/target/gestion-rh.war ${JBOSS_HOME}/standalone/deployments/

USER jboss

EXPOSE 8080 9990

ENTRYPOINT ["/opt/jboss/wildfly/docker/entrypoint.sh"]
