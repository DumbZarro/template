package top.dumbzarro.template.repository.po;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "perm")
public class PermPo extends sysPo {

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "resource")
    private String resource;

}