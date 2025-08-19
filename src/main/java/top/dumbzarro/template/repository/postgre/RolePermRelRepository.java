package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import top.dumbzarro.template.repository.entity.RolePermRelEntity;

import java.util.List;

public interface RolePermRelRepository extends JpaRepository<RolePermRelEntity, Long> {
    @NativeQuery("""
            SELECT *
            FROM role_perm_rel
            WHERE sys_deleted = FALSE
            AND role_id IN (collation for :roleIds);
            """)
    List<RolePermRelEntity> queryByRoleIds(@Param("roleIds") List<Long> roleIds);
}
