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

import java.time.Instant;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_user_oauth_rel")
public class UserOAuthRelPo extends SysPo {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "registration")
    private Registration registration;

    @Column(name = "provider_user_id")
    private String providerUserId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expireTime")
    private Instant expireTime;

    @Column(name = "scope")
    private String scope;

    @Getter
    public enum Registration implements BaseEnum {
        GITHUB_USER_INFO(1, "github-user-info"),
        GOOGLE_USER_INFO(2, "google-user-info");

        private final Integer code;
        private final String id;

        Registration(Integer code, String id) {
            this.code = code;
            this.id = id;
        }

        @Converter(autoApply = true) // autoApply = true 表示自动应用于所有AccountStatus类型的属性
        public static class RegistrationConverter extends EnumColumnConverter<Registration> {
            public RegistrationConverter() {
                super(Registration.class);
            }
        }

    }

}
