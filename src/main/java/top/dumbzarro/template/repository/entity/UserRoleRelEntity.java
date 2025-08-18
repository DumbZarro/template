package top.dumbzarro.template.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table(name = "user_role_rel")
public class UserRoleRelEntity {
    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("role_id")
    private Long roleId;

    @Column("role_code")
    private Long roleCode;

    @Column("sys_created_by")
    private String sysCreatedBy;

    @Column("sys_updated_by")
    private String sysUpdatedBy;

    @Column("sys_created_time")
    private Instant sysCreatedTime;

    @Column("sys_updated_time")
    private Instant sysUpdatedTime;

    @Column("sys_deleted")
    private Boolean sysDeleted;

}