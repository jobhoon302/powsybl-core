FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV MAVEN_VERSION=3.9.12
ENV MAVEN_HOME=/opt/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

# Base system
RUN apt update && apt install -y \
    openjdk-21-jdk \
    curl \
    unzip \
    git \
    ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# ~/.itools/config.yml
RUN mkdir -p /root/.itools
# COPY itools/config.yml /root/.itools/config.yml

# Install Maven 3.9.12
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.zip \
    -o /tmp/maven.zip \
 && unzip /tmp/maven.zip -d /opt \
 && mv /opt/apache-maven-${MAVEN_VERSION} ${MAVEN_HOME} \
 && ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn \
 && rm /tmp/maven.zip

WORKDIR /workspace

CMD ["bash"]
