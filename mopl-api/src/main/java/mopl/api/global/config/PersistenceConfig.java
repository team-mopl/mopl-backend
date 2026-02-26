package mopl.api.global.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
* 멀티 모듈 구조에서 mopl-api가 mopl-persistence의 JPA 엔티티와 리포지토리를 스캔하도록 명시하는 설정
* (컨텍스트 로드 시 Repository 빈 누락 방지)
* */
@Configuration
@EntityScan(basePackages = "mopl.persistence.rdb")
@EnableJpaRepositories(basePackages = "mopl.persistence.rdb")
public class PersistenceConfig {
}
