FROM maven:3.8.7-openjdk-18 AS build
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn -e clean package -DskipTests=true

FROM openjdk:19-alpine
RUN apk add dumb-init
RUN mkdir /app
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
COPY --from=build /project/target/*.jar /app/poker-dice.jar
WORKDIR /app
RUN chown -R javauser:javauser /app
USER javauser
EXPOSE 28882
ENV TOKEN=""
CMD "dumb-init" "java" "-jar" "poker-dice.jar" "--bot.token=${TOKEN}"