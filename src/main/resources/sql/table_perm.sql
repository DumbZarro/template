DROP TABLE IF EXISTS perm;
CREATE TABLE perm
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    code             VARCHAR(50) NOT NULL UNIQUE,
    name             VARCHAR(50) NOT NULL,
    description      VARCHAR(50) NOT NULL,
    type             INTEGER     NOT NULL,
    resource         VARCHAR(50) NOT NULL,
    sys_created_by   VARCHAR(50) NOT NULL DEFAULT '_system',
    sys_created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_updated_by   VARCHAR(50) NOT NULL DEFAULT '_system',
    sys_updated_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_deleted      BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_orders_active ON perm (code) WHERE sys_deleted = FALSE;

COMMENT ON COLUMN perm.id IS '主键';
COMMENT ON COLUMN perm.code IS '权限编码';
COMMENT ON COLUMN perm.name IS '权限名称';
COMMENT ON COLUMN perm.description IS '权限描述';
COMMENT ON COLUMN perm.type IS '权限类型：菜单权限、按钮权限、API调用权限等';
COMMENT ON COLUMN perm.resource IS '资源标识，不同权限类型对应不同的资源类型、如表单路径、按钮标识、API路径等';
COMMENT ON COLUMN perm.sys_created_by IS '创建人（创建人）';
COMMENT ON COLUMN perm.sys_created_time IS '创建时间（系统字段）';
COMMENT ON COLUMN perm.sys_updated_by IS '更新人（系统字段）';
COMMENT ON COLUMN perm.sys_updated_time IS '更新时间（系统字段）';
COMMENT ON COLUMN perm.sys_deleted IS '逻辑删除标识（系统字段）';


CREATE TRIGGER trigger_update_sys_time
    BEFORE UPDATE
    ON perm
    FOR EACH ROW
EXECUTE FUNCTION update_sys_update_time();