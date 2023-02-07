FROM openjdk:17

EXPOSE 7001

ADD target/dlt-patient.jar dlt-patient.jar

ENTRYPOINT ["java" , "-jar" , "dlt-patient.jar"]