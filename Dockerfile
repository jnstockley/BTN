FROM debian:stable

RUN echo 'deb http://ftp.debian.org/debian stretch-backports main' | tee /etc/apt/sources.list.d/stretch-backports.list

RUN apt update && apt install openjdk-17-jre -y

ADD out/bsn-2.0-alpha3-SNAPSHOT-jar-with-dependencies.jar BSN.jar

ENTRYPOINT ["java", "-jar", "BSN.jar"]