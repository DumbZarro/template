package top.dumbzarro.template.repository.po;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "user_basic_info")
public class UserBasicInfoPo extends sysPo {

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "account_status")
    private Integer accountStatus;


    @Getter
    public enum AccountStatus {
        UNVERIFY(1, "待验证"),
        NORMAL(2, "正常");
        final Integer code;
        final String desc;

        AccountStatus(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private static final Map<Integer, AccountStatus> CODE_MAP = new HashMap<>();

        static {
            for (AccountStatus accountStatus : AccountStatus.values()) {
                CODE_MAP.put(accountStatus.code, accountStatus);
            }
        }

        public static String getDescByCode(Integer code) {
            AccountStatus status = CODE_MAP.get(code);
            if (Objects.isNull(status)) {
                throw new BizException(BizEnum.DATA_EXCEPTION, "枚举异常");
            }
            return status.desc;

        }

    }
}
