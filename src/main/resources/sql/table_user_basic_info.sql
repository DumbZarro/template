-- 用户表结构
DROP TABLE IF EXISTS user_basic_info;
CREATE TABLE user_basic_info
(
    id               BIGINT PRIMARY KEY          DEFAULT generate_snowflake_id(),
    email            VARCHAR(50) UNIQUE NOT NULL,
    password         VARCHAR(60)        NOT NULL,
    name             VARCHAR(50)        NOT NULL,
    avatar_url       VARCHAR(255)       NOT NULL,
    account_status   INTEGER            NOT NULL DEFAULT 1,
    sys_created_by   VARCHAR(50)        NOT NULL DEFAULT '_system',
    sys_created_time TIMESTAMPTZ        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_updated_by   VARCHAR(50)        NOT NULL DEFAULT '_system',
    sys_updated_time TIMESTAMPTZ        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_deleted      BOOLEAN            NOT NULL DEFAULT FALSE
);

COMMENT ON COLUMN user_basic_info.id IS '主键';
COMMENT ON COLUMN user_basic_info.email IS '用户邮箱';
COMMENT ON COLUMN user_basic_info.password IS '用户密码';
COMMENT ON COLUMN user_basic_info.name IS '用户昵称';
COMMENT ON COLUMN user_basic_info.avatar_url IS '用户头像';
COMMENT ON COLUMN user_basic_info.account_status IS '用户状态';
COMMENT ON COLUMN user_basic_info.sys_created_by IS '创建人（创建人）';
COMMENT ON COLUMN user_basic_info.sys_created_time IS '创建时间（系统字段）';
COMMENT ON COLUMN user_basic_info.sys_updated_by IS '更新人（系统字段）';
COMMENT ON COLUMN user_basic_info.sys_updated_time IS '更新时间（系统字段）';
COMMENT ON COLUMN user_basic_info.sys_deleted IS '逻辑删除标识（系统字段）';

CREATE INDEX idx_user_basic_info_email ON user_basic_info (email) WHERE sys_deleted = FALSE;

CREATE TRIGGER trigger_update_sys_time
    BEFORE UPDATE
    ON user_basic_info
    FOR EACH ROW
EXECUTE FUNCTION update_sys_update_time();