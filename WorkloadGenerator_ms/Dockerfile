FROM openjdk:8-jdk-alpine
COPY ./WorkloadGenerator_ms/target/WorkloadGenerator-1.0.0-SNAPSHOT.jar home/WorkloadGenerator-1.0.0-SNAPSHOT.jar
#COPY ./WorkloadGenerator_ms/configs_file.json home/configs/configs_file.json
#CMD ["java","-jar","-Xdebug","-Xrunjdwp:transport=dt_socket,address=8092,server=y,suspend=n","/home/WorkloadGenerator-1.0.0-SNAPSHOT.jar"]
EXPOSE 8091 8001
#ADD start.sh /
#RUN chmod +x /start.sh
#CMD ["/start.sh"]
CMD ["java","-jar","home/WorkloadGenerator-1.0.0-SNAPSHOT.jar"]

