-- Добавляем колонку properties в таблицу devices
ALTER TABLE devices ADD COLUMN IF NOT EXISTS properties JSONB;

-- Обновляем колонку parameters и result в таблице commands, если они ещё не JSONB
ALTER TABLE commands ALTER COLUMN parameters TYPE JSONB USING parameters::JSONB;
ALTER TABLE commands ALTER COLUMN result TYPE JSONB USING result::JSONB;
