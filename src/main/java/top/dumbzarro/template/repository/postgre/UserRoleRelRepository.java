package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import top.dumbzarro.template.repository.po.UserRoleRelPo;

import java.util.List;

public interface UserRoleRelRepository extends JpaRepository<UserRoleRelPo, Long> {
    @NativeQuery("""
            SELECT *
            FROM user_role_rel
            WHERE sys_deleted = FALSE
            AND user_id = :userId;
            """)
    List<UserRoleRelPo> queryByUserId(@Param("userId") Long userId);
}
