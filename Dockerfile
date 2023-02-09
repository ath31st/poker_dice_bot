FROM openjdk:19-alpine
RUN mkdir /app
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
#RUN ./mvnw package -DskipTests=true
RUN ./mvnw dependency:resolve
#RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
#RUN chown -R javauser:javauser /app
#USER javauser
#COPY target/*.jar /app/poker-dice.jar

EXPOSE 28882
#ENV TOKEN=""
#ENTRYPOINT ["java", "-jar", "/app/poker-dice.jar", "--bot.token=${TOKEN}"]
COPY src ./src
CMD ["./mvnw", "spring-boot:run", "--bot.token=${TOKEN}"]