package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import top.dumbzarro.template.repository.po.RolePermRelPo;

import java.util.Collection;
import java.util.List;

public interface RolePermRelRepository extends JpaRepository<RolePermRelPo, Long> {
    List<RolePermRelPo> findByRoleIdIn(Collection<Long> roleIds);
}
