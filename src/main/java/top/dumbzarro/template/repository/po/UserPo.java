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
@Table(name = "t_user")
public class UserPo extends SysPo {

    @Column(name = "email")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;


    @Getter
    public enum AccountStatus implements BaseEnum {
        UNVERIFY(1, "待验证"),
        NORMAL(2, "正常");
        private final Integer code;
        private final String desc;

        AccountStatus(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @Converter(autoApply = true) // autoApply = true 表示自动应用于所有AccountStatus类型的属性
        public static class UserPoAccountStatusConverter extends EnumColumnConverter<AccountStatus> {
            public UserPoAccountStatusConverter() {
                super(AccountStatus.class);
            }
        }

    }
}
