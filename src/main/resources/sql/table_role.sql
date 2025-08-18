DROP TABLE IF EXISTS role;
CREATE TABLE role
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    code             VARCHAR(50) NOT NULL UNIQUE,
    name             VARCHAR(50) NOT NULL,
    description      VARCHAR(50) NOT NULL,
    sys_created_by   VARCHAR(50) NOT NULL DEFAULT '_system',
    sys_created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_updated_by   VARCHAR(50) NOT NULL DEFAULT '_system',
    sys_updated_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_deleted      BOOLEAN     NOT NULL DEFAULT FALSE
);

COMMENT ON COLUMN role.id IS '主键';
COMMENT ON COLUMN role.code IS '角色编码';
COMMENT ON COLUMN role.name IS '角色名称';
COMMENT ON COLUMN role.description IS '角色描述';
COMMENT ON COLUMN role.sys_created_by IS '创建人（创建人）';
COMMENT ON COLUMN role.sys_created_time IS '创建时间（系统字段）';
COMMENT ON COLUMN role.sys_updated_by IS '更新人（系统字段）';
COMMENT ON COLUMN role.sys_updated_time IS '更新时间（系统字段）';
COMMENT ON COLUMN role.sys_deleted IS '逻辑删除标识（系统字段）';


CREATE TRIGGER trigger_update_sys_time
    BEFORE UPDATE
    ON role
    FOR EACH ROW
EXECUTE FUNCTION update_sys_update_time();