FROM openjdk:8-jdk-alpine
RUN mkdir -p /root/.ssh

COPY model-manager-services-${project.version}.jar /opt/ericsson/model_manager/model-manager-services-${project.version}.jar

ENV JAVA_OPTS="-Xmx2g"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/ericsson/model_manager/model-manager-services-${project.version}.jar --spring.profiles.active=container"]