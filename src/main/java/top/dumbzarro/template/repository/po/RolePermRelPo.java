package top.dumbzarro.template.repository.po;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_role_perm_rel")
public class RolePermRelPo extends SysPo {

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "role_code", nullable = false)
    private String roleCode;

    @Column(name = "perm_id", nullable = false)
    private Long permId;

    @Column(name = "perm_code", nullable = false)
    private String permCode;

}