package top.dumbzarro.template.repository.entity;

import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Table("user_basic_info")
public class UserBasicInfoEntity {
    @Id
    @Column("id")
    private Long id;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("name")
    private String name;

    @Column("avatar_url")
    private String avatarUrl;

    @Column("account_status")
    private Integer accountStatus;

    @Column("sys_created_by")
    private String sysCreatedBy;

    @Column("sys_updated_by")
    private String sysUpdatedBy;

    @Column("sys_created_time")
    private Instant sysCreatedTime;

    @Column("sys_updated_time")
    private Instant sysUpdatedTime;

    @Column("sys_deleted")
    private Boolean sysDeleted;

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
