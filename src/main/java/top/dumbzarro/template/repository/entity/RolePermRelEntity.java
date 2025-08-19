package top.dumbzarro.template.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
@Table(name = "role_perm_rel")
public class RolePermRelEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "perm_id")
    private Long permId;

    @Column(name = "perm_code")
    private String permCode;

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