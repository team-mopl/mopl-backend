package mopl.persistence.rdb.domain.content.repository;

import mopl.persistence.rdb.domain.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

}