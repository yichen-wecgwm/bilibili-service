FROM eclipse-temurin:19.0.2_7-jre as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:19.0.2_7-jre
WORKDIR application
RUN chmod a+w /application && mkdir /application/videos
RUN addgroup --system springboot && adduser --system springboot --ingroup springboot
USER springboot
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
# TODO 添加启动参数
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
