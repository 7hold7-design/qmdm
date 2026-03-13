# QMDM - Quantum Mobile Device Management

QMDM - это современная система управления мобильными устройствами (MDM) для Android, построенная на основе Spring Boot 3 и совместимая с клиентами Headwind MDM (hmdm-android).

## 🚀 Технологический стек

### Backend
- Java 21 + Spring Boot 3.2
- Spring Security с OAuth2 Resource Server
- Spring Data JPA + PostgreSQL
- Flyway для миграций БД
- Redis для кэширования
- RabbitMQ для асинхронных операций
- MQTT для коммуникации с устройствами

### Frontend (в разработке)
- React 18 / Vue 3
- TypeScript
- Vite
- TanStack Query
- Tailwind CSS

## 📋 Предварительные требования

- Docker и Docker Compose
- Java 21 (для локальной разработки)
- Maven 3.8+
- Node.js 18+ (для фронтенда)

## 🏁 Быстрый старт

1. **Клонируйте репозиторий**
   ```bash
   git clone https://github.com/7hold7-design/qmdm.git
   cd qmdm