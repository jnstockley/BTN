FROM openjdk:latest

ADD out/bsn-2.0-alpha3-SNAPSHOT-jar-with-dependencies.jar BSN.jar

ENTRYPOINT ["java", "-jar", "BSN.jar"]