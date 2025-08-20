package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.dumbzarro.template.repository.po.PermPo;

import java.util.Collection;
import java.util.List;

public interface PermRepository extends JpaRepository<PermPo, Long> {

    List<PermPo> findByIdIn(Collection<Long> ids);

}
