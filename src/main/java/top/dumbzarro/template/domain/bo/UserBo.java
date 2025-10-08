package top.dumbzarro.template.domain.bo;

import lombok.Data;
import top.dumbzarro.template.repository.po.UserPo;

import java.util.Set;

@Data
public class UserBo {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String avatarUrl;
    private UserPo.AccountStatus accountStatus;
    Set<String> perms;
    Set<String> roles;
}
