package top.dumbzarro.template.repository.postgre;

import top.dumbzarro.template.repository.entity.RoleEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface RoleRepository extends R2dbcRepository<RoleEntity, Long> {

}
