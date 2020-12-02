FROM openjdk:8-alpine
RUN apk update && apk add maven
RUN mkdir -p /opt/eleuth-node
WORKDIR /opt/eleuth-node
CMD echo 172.17.0.1 myhost.com >> /etc/hosts; supervisord -n;
COPY ./target/eleuth.jar /opt/eleuth-node/.

ENTRYPOINT ["sh","-c", "java -jar eleuth.jar ${ARGS}"]