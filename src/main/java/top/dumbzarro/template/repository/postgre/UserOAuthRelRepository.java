package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import top.dumbzarro.template.repository.po.UserOAuthRelPo;

public interface UserOAuthRelRepository extends JpaRepository<UserOAuthRelPo, Long> {

    UserOAuthRelPo findByRegistrationAndProviderUserId(UserOAuthRelPo.Registration registration, String providerUserId);
}
