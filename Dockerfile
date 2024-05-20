FROM openjdk:17-slim

WORKDIR /app

COPY .gradle/ /app/.gradle/
COPY gradle/wrapper/ /app/gradle/wrapper/
COPY build.gradle.kts gradlew gradlew.bat settings.gradle.kts /app/
RUN chmod +x /app/gradlew

RUN /app/gradlew dependencies

COPY . /app

RUN /app/gradlew build --stacktrace --info --debug

#EXPOSE 8080

RUN echo '#!/bin/bash\njava -jar /app/build/libs/StatusChecker.jar "$@"' > /usr/local/bin/sc && \
    chmod +x /usr/local/bin/sc

WORKDIR /app/build/libs/
#ENTRYPOINT ["java", "-jar", "/app/build/libs/StatusChecker.jar"]
#CMD ["/bin/bash"]
CMD ["/bin/sh"]

