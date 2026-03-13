#!/bin/sh
set -e

# Ждем, пока бэкенд станет доступен
echo "Waiting for backend to be ready..."
while ! nc -z backend 8080; do
  echo "Backend not ready yet. Retrying in 2 seconds..."
  sleep 2
done
echo "Backend is ready!"

# Заменяем переменные в nginx конфиге если нужно
# envsubst < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf

# Проверяем конфигурацию (теперь бэкенд доступен)
nginx -t

# Запускаем nginx
exec "$@"
