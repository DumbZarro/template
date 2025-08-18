-- 创建一个序列用于生成序列号
CREATE SEQUENCE snowflake_seq START 1;
-- 创建雪花 ID 生成函数
CREATE OR REPLACE FUNCTION generate_snowflake_id() RETURNS BIGINT AS
$$
DECLARE
    timestamp_ms BIGINT;
    machine_id   INT := 1; -- 手动写死机器 ID 为 1
    sequence     INT;
    snowflake_id BIGINT;
BEGIN
    -- 获取当前时间戳（毫秒级）
    timestamp_ms := EXTRACT(epoch FROM now()) * 1000;

    -- 获取序列号（在同一毫秒内，确保唯一性）
    sequence := nextval('snowflake_seq');

    -- 生成雪花 ID： timestamp (41 bits) + machine_id (10 bits) + sequence (12 bits)
    snowflake_id := (timestamp_ms << 22) | (machine_id << 12) | sequence;

    RETURN snowflake_id;
END;
$$ LANGUAGE plpgsql;