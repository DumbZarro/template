package top.dumbzarro.template.repository.postgre;

import top.dumbzarro.template.repository.entity.PermEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.util.List;

public interface PermRepository extends R2dbcRepository<PermEntity, Long> {
    @Query("""
            SELECT *
            from perm
            WHERE id IN (collation for :permsIds);
            """)
    Flux<PermEntity> queryByPermIds(@Param("permsIds") List<Long> permsIds);

}
