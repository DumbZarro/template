package top.dumbzarro.template.repository.po;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_user_role_rel")
public class UserRoleRelPo extends SysPo {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_code")
    private Long roleCode;

}