package top.dumbzarro.template.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
@Table(name = "user_role_rel")
public class UserRoleRelEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_code")
    private Long roleCode;

    @Column(name = "sys_created_by")
    private String sysCreatedBy;

    @Column(name = "sys_updated_by")
    private String sysUpdatedBy;

    @Column(name = "sys_created_time")
    private Instant sysCreatedTime;

    @Column(name = "sys_updated_time")
    private Instant sysUpdatedTime;

    @Column(name = "sys_deleted")
    private Boolean sysDeleted;

}