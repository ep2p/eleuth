FROM openjdk:8-alpine
RUN apk update && apk add maven
RUN mkdir -p /opt/eleuth-node
WORKDIR /opt/eleuth-node
COPY ./target/eleuth.jar /opt/eleuth-node/.

ENTRYPOINT ["sh","-c", "java -jar eleuth.jar ${ARGS}"]