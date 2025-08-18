package top.dumbzarro.template.repository.postgre;

import top.dumbzarro.template.repository.entity.UserRoleRelEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRelRepository   {
//    @Query("""
//            SELECT *
//            FROM user_role_rel
//            WHERE  sys_deleted = FALSE
//            AND user_id = :userId;
//            """)
    List<UserRoleRelEntity> queryByUserId(@Param("userId")Long userId);
}
