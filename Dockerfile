FROM gradle:8.10.1-jdk21 AS build

COPY . /home/gradle/src
WORKDIR /home/gradle/src

ARG USERNAME
ARG TOKEN

ENV USERNAME=${USERNAME}
ENV TOKEN=${TOKEN}

RUN ./gradlew assemble --no-daemon

FROM amazoncorretto:21-alpine

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/snippet.jar

ENTRYPOINT ["java", "-jar", "/app/snippet.jar"]