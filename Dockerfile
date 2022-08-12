FROM debian:stable

RUN echo 'deb http://ftp.debian.org/debian stretch-backports main' | tee /etc/apt/sources.list.d/stretch-backports.list

RUN apt update && apt upgrade -y && apt install openjdk-17-jre -y

ADD out/BSN.jar BSN.jar

RUN echo 'alias BSN="java -jar BSN.jar"' >> ~/.bashrc

ENTRYPOINT ["java", "-jar", "BSN.jar"]
