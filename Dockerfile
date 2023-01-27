FROM openjdk:8-jdk-alpine

EXPOSE 7000

COPY target/dlt-patient.jar dlt-patient.jar 

ENTRYPOINT [ "java" , "-jar" , "dlt-patient.jar" ]

