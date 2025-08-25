package top.dumbzarro.template.repository.po;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_role_perm_rel")
public class RolePermRelPo extends SysPo {

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "perm_id")
    private Long permId;

    @Column(name = "perm_code")
    private String permCode;

}