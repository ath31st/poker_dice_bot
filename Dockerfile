FROM openjdk:19-alpine
RUN mkdir /app
#RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
#RUN chown -R javauser:javauser /app
#USER javauser
ADD target/*.jar /app/poker-dice.jar
WORKDIR /app
EXPOSE 28882
ENV TOKEN=""
ENTRYPOINT ["java", "-jar", "/app/poker-dice.jar", "--bot.token=${TOKEN}"]