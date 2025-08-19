package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import top.dumbzarro.template.repository.entity.UserRoleRelEntity;

import java.util.List;

public interface UserRoleRelRepository extends JpaRepository<UserRoleRelEntity, Long> {
    @NativeQuery("""
            SELECT *
            FROM user_role_rel
            WHERE  sys_deleted = FALSE
            AND user_id = :userId;
            """)
    List<UserRoleRelEntity> queryByUserId(@Param("userId") Long userId);
}
