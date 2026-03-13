-- Создание таблицы устройств
CREATE TABLE IF NOT EXISTS devices (
    id VARCHAR(255) PRIMARY KEY,
    device_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255),
    model VARCHAR(255) NOT NULL,
    manufacturer VARCHAR(255) NOT NULL,
    android_version VARCHAR(50),
    api_level INTEGER,
    status VARCHAR(50) NOT NULL,
    last_seen TIMESTAMP,
    enrollment_token VARCHAR(255),
    enrolled_at TIMESTAMP,
    group_id VARCHAR(255),
    properties JSONB,  -- Добавлена эта колонка
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Создание таблицы команд
CREATE TABLE IF NOT EXISTS commands (
    id VARCHAR(255) PRIMARY KEY,
    device_id VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    parameters JSONB,
    status VARCHAR(50) NOT NULL,
    result JSONB,
    error_message TEXT,
    sent_at TIMESTAMP,
    completed_at TIMESTAMP,
    expires_at TIMESTAMP,
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (device_id) REFERENCES devices(device_id) ON DELETE CASCADE
);

-- Создание таблицы приложений
CREATE TABLE IF NOT EXISTS applications (
    id VARCHAR(255) PRIMARY KEY,
    package_name VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    version VARCHAR(50),
    version_code INTEGER,
    apk_url TEXT,
    icon_url TEXT,
    system_app BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Создание таблицы групп устройств
CREATE TABLE IF NOT EXISTS device_groups (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES device_groups(id)
);

-- Создание таблицы политик
CREATE TABLE IF NOT EXISTS policies (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    configuration JSONB NOT NULL,
    group_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (group_id) REFERENCES device_groups(id)
);

-- Индексы для оптимизации
CREATE INDEX idx_devices_status ON devices(status);
CREATE INDEX idx_devices_last_seen ON devices(last_seen);
CREATE INDEX idx_commands_device_id ON commands(device_id);
CREATE INDEX idx_commands_status ON commands(status);
CREATE INDEX idx_commands_created_at ON commands(created_at);
