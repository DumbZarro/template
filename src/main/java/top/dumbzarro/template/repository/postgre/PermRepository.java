package top.dumbzarro.template.repository.postgre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import top.dumbzarro.template.repository.entity.PermEntity;

import java.util.List;

public interface PermRepository extends JpaRepository<PermEntity, Long> {
    @NativeQuery("""
            SELECT *
            from perm
            WHERE id IN (collation for :permsIds);
            """)
    PermEntity queryByPermIds(@Param("permsIds") List<Long> permsIds);

}
