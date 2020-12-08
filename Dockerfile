FROM openjdk:8-alpine
RUN apk update && apk add maven && apk add --no-cache tzdata
RUN mkdir -p /opt/eleuth-node && mkdir /opt/data
WORKDIR /opt/eleuth-node
COPY ./target/eleuth.jar /opt/eleuth-node/.
ENV TZ UTC
ENTRYPOINT ["sh","-c", "java -jar eleuth.jar ${ARGS} -Duser.timezone=UTC"]