package mopl.persistence.rdb.domain.user.repository;

import mopl.persistence.rdb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
