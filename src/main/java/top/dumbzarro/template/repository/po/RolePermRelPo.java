package top.dumbzarro.template.repository.po;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "role_perm_rel")
public class RolePermRelPo extends sysPo {

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "perm_id")
    private Long permId;

    @Column(name = "perm_code")
    private String permCode;

}