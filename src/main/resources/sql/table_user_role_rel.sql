DROP TABLE IF EXISTS user_role_rel;
CREATE TABLE user_role_rel
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id          BIGINT      NOT NULL,
    role_id          BIGINT      NOT NULL,
    role_code        BIGINT      NOT NULL,
    sys_created_by   VARCHAR(50) NOT NULL DEFAULT '_system',
    sys_created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_updated_by   VARCHAR(50) NOT NULL DEFAULT '_system',
    sys_updated_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_deleted      BOOLEAN     NOT NULL DEFAULT FALSE
);

COMMENT ON COLUMN user_role_rel.id IS '主键';
COMMENT ON COLUMN user_role_rel.user_id IS '用户id';
COMMENT ON COLUMN user_role_rel.role_id IS '角色id';
COMMENT ON COLUMN user_role_rel.role_code IS '角色编码（冗余）';
COMMENT ON COLUMN user_role_rel.sys_created_by IS '创建人（创建人）';
COMMENT ON COLUMN user_role_rel.sys_created_time IS '创建时间（系统字段）';
COMMENT ON COLUMN user_role_rel.sys_updated_by IS '更新人（系统字段）';
COMMENT ON COLUMN user_role_rel.sys_updated_time IS '更新时间（系统字段）';
COMMENT ON COLUMN user_role_rel.sys_deleted IS '逻辑删除标识（系统字段）';

CREATE TRIGGER trigger_update_sys_time
    BEFORE UPDATE
    ON user_role_rel
    FOR EACH ROW
EXECUTE FUNCTION update_sys_update_time();