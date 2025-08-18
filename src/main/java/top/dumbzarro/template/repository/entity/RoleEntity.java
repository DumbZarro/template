package top.dumbzarro.template.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table(name = "role")
public class RoleEntity {
    @Id
    @Column("id")
    private Long id;

    @Column("code")
    private String code;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

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