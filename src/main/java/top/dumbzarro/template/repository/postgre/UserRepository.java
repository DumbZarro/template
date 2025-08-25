package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import top.dumbzarro.template.repository.po.UserPo;

import java.util.List;

public interface UserRepository extends JpaRepository<UserPo, Long> {
    @NativeQuery("""
            SELECT *
            FROM user_basic_info
            WHERE sys_deleted = FALSE
            AND email = :email
            LIMIT 1;
            """)
    UserPo findByEmail(@Param("email") String email);

}
