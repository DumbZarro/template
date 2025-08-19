package top.dumbzarro.template.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
@Table(name = "role")
public class RoleEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

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