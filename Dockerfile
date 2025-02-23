#  ---------------------------------- setup our needed libreoffice engaged server with newest glibc
# we cannot use the official image since we then cannot have sid and the glibc fix
#FROM openjdk:11.0-jre-slim-stretch as jodconverter-base
FROM debian:sid as jodconverter-base
# backports would only be needed for stretch
#RUN echo "deb http://ftp2.de.debian.org/debian stretch-backports main contrib non-free" > /etc/apt/sources.list.d/debian-backports.list
RUN apt-get update && apt-get -y install \
        openjdk-8-jdk \
        apt-transport-https locales-all libpng16-16 libxinerama1 libgl1-mesa-glx libfontconfig1 libfreetype6 libxrender1 \
        libxcb-shm0 libxcb-render0 adduser cpio findutils \
        # procps needed for us finding the libreoffice process, see https://github.com/sbraconnier/jodconverter/issues/127#issuecomment-463668183
        procps \
    # only for stretch
    #&& apt-get -y install -t stretch-backports libreoffice --no-install-recommends \
    # sid variant
    && apt-get -y install libreoffice --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*
ENV JAR_FILE_BASEDIR=/opt/app
ENV LOG_BASE_DIR=/var/log
ENV JAR_FILE_NAME=app.jar

COPY bin/docker-entrypoint.sh /docker-entrypoint.sh

RUN mkdir -p ${JAR_FILE_BASEDIR} /etc/app \
  && touch /etc/app/application.properties \
  && chmod +x /docker-entrypoint.sh
WORKDIR /app

COPY target/*SNAPSHOT.jar ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}

ENTRYPOINT ["/docker-entrypoint.sh"]