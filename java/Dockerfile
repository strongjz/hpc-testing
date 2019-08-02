FROM openjdk:8-jre-alpine
COPY *.jar /
COPY series_1k.txt /

VOLUME        ["/var/volume1", "/var/volume2"]


ENTRYPOINT ["java", "-jar", "OptionPricer.jar"]
CMD ["999", "25", "21", ".05", ".5", "series_1k.txt"]
