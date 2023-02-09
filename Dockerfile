FROM openjdk:19-alpine
RUN mkdir /app
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
#RUN ./mvnw dependency:resolve
RUN ./mvnw package -DskipTests=true

#RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
#RUN chown -R javauser:javauser /app
#USER javauser
WORKDIR /app
COPY target/*.jar /app/poker-dice.jar

#RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
#RUN chown -R javauser:javauser /app
#USER javauser

EXPOSE 28882
ENV TOKEN=""
#COPY src ./src
#CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.arguments=--bot.token=${TOKEN}"]
ENTRYPOINT ["java", "-jar", "/app/poker-dice.jar", "--bot.token=${TOKEN}"]
