package top.dumbzarro.template.repository.po;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
public class sysPo {
    @Id
    @Column(name = "id")
    private Long id;

    @CreatedBy
    @Column(name = "sys_created_by")
    private String sysCreatedBy;

    @LastModifiedBy
    @Column(name = "sys_updated_by")
    private String sysUpdatedBy;

    @CreatedDate
    @Column(name = "sys_created_time")
    private Instant sysCreatedTime;

    @LastModifiedDate
    @Column(name = "sys_updated_time")
    private Instant sysUpdatedTime;

    @SoftDelete
    @Column(name = "sys_deleted")
    private Boolean sysDeleted;
}
