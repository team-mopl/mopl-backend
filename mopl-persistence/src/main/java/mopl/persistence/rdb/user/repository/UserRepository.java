package mopl.persistence.rdb.user.repository;

import mopl.persistence.rdb.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
