package top.dumbzarro.template.repository.po;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_role")
public class RolePo extends SysPo {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

}