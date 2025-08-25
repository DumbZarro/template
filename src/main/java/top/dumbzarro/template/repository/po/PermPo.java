package top.dumbzarro.template.repository.po;

import jakarta.persistence.Column;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import top.dumbzarro.template.common.enums.BaseEnum;
import top.dumbzarro.template.common.enums.EnumColumnConverter;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_perm")
public class PermPo extends SysPo {

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private Type type;

    @Column(name = "resource")
    private String resource;

    @Getter
    public enum Type implements BaseEnum {
        MENU(1, "菜单"),
        BUTTON(2, "按钮"),
        API(3, "API");

        private final Integer code;
        private final String desc;

        Type(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @Converter(autoApply = true) // autoApply = true 表示自动应用于所有AccountStatus类型的属性
        public static class PermPoTypeConverter extends EnumColumnConverter<PermPo.Type> {
            public PermPoTypeConverter() {
                super(PermPo.Type.class);
            }
        }

    }
}