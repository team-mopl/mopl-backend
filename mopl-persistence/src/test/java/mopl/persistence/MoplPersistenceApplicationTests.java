package mopl.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ContextConfiguration(classes = MoplPersistenceApplicationTests.TestConfig.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
class MoplPersistenceApplicationTests {

    @Test
    void contextLoads() {
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}
