package top.dumbzarro.template.repository.postgre;

import top.dumbzarro.template.repository.entity.RolePermRelEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.util.List;

public interface RolePermRelRepository extends R2dbcRepository<RolePermRelEntity, Long> {
    @Query("""
            SELECT *
            from role_perm_rel
            WHERE sys_deleted = FALSE
            AND role_id IN (collation for :roleIds);
            """)
    Flux<RolePermRelEntity> queryByRoleIds(@Param("roleIds") List<Long> roleIds);
}
