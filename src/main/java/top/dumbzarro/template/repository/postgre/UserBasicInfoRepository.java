package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import top.dumbzarro.template.repository.po.UserBasicInfoPo;

public interface UserBasicInfoRepository extends JpaRepository<UserBasicInfoPo, Long> {
    @NativeQuery("""
            SELECT *
            FROM user_basic_info
            WHERE sys_deleted = FALSE
            AND email = :email
            LIMIT 1;
            """)
    UserBasicInfoPo findByEmail(@Param("email") String email);
}
