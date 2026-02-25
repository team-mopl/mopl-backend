package mopl.persistence.rdb.content.repository;

import mopl.persistence.rdb.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

}