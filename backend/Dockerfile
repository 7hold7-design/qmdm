# Этап 1: Загрузка зависимостей
FROM gradle:8.5-jdk21 AS deps
WORKDIR /app

# Копируем только файлы с зависимостями
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle gradle

# Скачиваем зависимости и сохраняем их в кэш Docker
RUN gradle dependencies --no-daemon

# Этап 2: Сборка приложения
FROM deps AS build
WORKDIR /app

# Копируем исходный код
COPY src src

# Собираем приложение
RUN gradle clean build -x test --no-daemon

# Этап 3: Финальный образ
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Устанавливаем wget для healthcheck
RUN apk add --no-cache wget

# Создаем пользователя
RUN addgroup -S spring && adduser -S spring -G spring

# Копируем jar из этапа сборки
COPY --from=build /app/build/libs/qmdm-backend.jar app.jar

# Переключаемся на непривилегированного пользователя
USER spring:spring

# Точка входа
ENTRYPOINT ["java", "-jar", "app.jar"]

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

LABEL maintainer="QMDM Team" \
      version="1.0.0" \
      description="QMDM Backend Server"
