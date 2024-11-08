FROM gradle:8.10.1-jdk21 AS build

COPY . /home/gradle/src
WORKDIR /home/gradle/src

RUN --mount=type=secret,id=USERNAME,required,env=USERNAME \
    --mount=type=secret,id=TOKEN,required,env=TOKEN \
    ./gradlew assemble --no-daemon

FROM amazoncorretto:21-alpine

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/permission.jar

ENTRYPOINT ["java", "-jar", "/app/permission.jar"]

