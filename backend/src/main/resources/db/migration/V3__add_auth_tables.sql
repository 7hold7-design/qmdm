-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Таблица ролей
CREATE TABLE IF NOT EXISTS roles (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

-- Таблица прав
CREATE TABLE IF NOT EXISTS permissions (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

-- Связь пользователей и ролей
CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(255) NOT NULL,
    role_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Связь ролей и прав
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id VARCHAR(255) NOT NULL,
    permission_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Добавляем базовые роли
INSERT INTO roles (id, name, description) VALUES 
    (gen_random_uuid()::VARCHAR, 'ROLE_ADMIN', 'Администратор системы'),
    (gen_random_uuid()::VARCHAR, 'ROLE_OPERATOR', 'Оператор'),
    (gen_random_uuid()::VARCHAR, 'ROLE_VIEWER', 'Наблюдатель')
ON CONFLICT (name) DO NOTHING;

-- Добавляем базовые права
INSERT INTO permissions (id, name, description) VALUES 
    (gen_random_uuid()::VARCHAR, 'device:read', 'Просмотр устройств'),
    (gen_random_uuid()::VARCHAR, 'device:write', 'Управление устройствами'),
    (gen_random_uuid()::VARCHAR, 'device:delete', 'Удаление устройств'),
    (gen_random_uuid()::VARCHAR, 'command:send', 'Отправка команд'),
    (gen_random_uuid()::VARCHAR, 'stats:read', 'Просмотр статистики'),
    (gen_random_uuid()::VARCHAR, 'user:manage', 'Управление пользователями')
ON CONFLICT (name) DO NOTHING;

-- Даем права ролям
-- Администратору все права
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Оператору права на просмотр и управление устройствами
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_OPERATOR' 
AND p.name IN ('device:read', 'device:write', 'command:send', 'stats:read')
ON CONFLICT DO NOTHING;

-- Наблюдателю только просмотр
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_VIEWER' 
AND p.name IN ('device:read', 'stats:read')
ON CONFLICT DO NOTHING;
