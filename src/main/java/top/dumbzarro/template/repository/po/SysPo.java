package top.dumbzarro.template.repository.po;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
public class SysPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @CreatedBy
    @Column(name = "sys_created_by", updatable = false)
    private String sysCreatedBy;

    @LastModifiedBy
    @Column(name = "sys_updated_by", nullable = false)
    private String sysUpdatedBy;

    @CreatedDate
    @Column(name = "sys_created_time", nullable = false)
    private Instant sysCreatedTime;

    @LastModifiedDate
    @Column(name = "sys_updated_time", nullable = false)
    private Instant sysUpdatedTime;

    @SoftDelete
    @Column(name = "sys_deleted",nullable = false)
    private Boolean sysDeleted;
}
