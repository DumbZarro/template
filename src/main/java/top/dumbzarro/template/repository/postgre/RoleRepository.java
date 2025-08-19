package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import top.dumbzarro.template.repository.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

}
