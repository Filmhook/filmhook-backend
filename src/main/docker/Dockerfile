From amazoncorretto:17

ARG RUN_JAVA_VERSION=1.3.8
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

RUN yum install curl shadow-utils sudo -y \
    && yum update -y \
    && yum clean all \
    && useradd docker \
    && usermod -G wheel docker \
    && mkdir /deployments \
    && chown docker:docker /deployments \
    && chmod "g+rwX" /deployments \
    && curl https://repo1.maven.org/maven2/io/fabric8/run-java-sh/${RUN_JAVA_VERSION}/run-java-sh-${RUN_JAVA_VERSION}-sh.sh -o /deployments/run-java.sh \
    && chown docker /deployments/run-java.sh \
    && chmod 540 /deployments/run-java.sh \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/lib/security/java.security \
    && echo '%wheel ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

COPY --chown=docker target/*.jar /deployments/

EXPOSE 8080
USER docker

ENTRYPOINT [ "/deployments/run-java.sh" ]