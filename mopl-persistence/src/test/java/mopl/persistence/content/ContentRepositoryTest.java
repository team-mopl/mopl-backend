package mopl.persistence.content;

import mopl.persistence.rdb.domain.content.entity.Content;
import mopl.persistence.rdb.domain.content.enums.ContentType;
import mopl.persistence.rdb.domain.content.repository.ContentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
public class ContentRepositoryTest {

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("Content 저장 후 ID로 조회할 수 있다")
    void saveAndFindById() {
        Content content = new Content(
                ContentType.movie,
                1001L,
                "Inception",
                "Dream within a dream",
                "https://example.com/inception.png"
        );

        Content saved = contentRepository.save(content);
        em.flush();
        em.clear();

        Optional<Content> found = contentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Inception");
        assertThat(found.get().getContentType()).isEqualTo(ContentType.movie);
        assertThat(found.get().getOriginId()).isEqualTo(1001L);
        assertThat(found.get().getAverageRating()).isEqualTo(0.0);
        assertThat(found.get().getReviewCount()).isZero();
    }

    @Test
    @DisplayName("Content update 변경사항이 반영된다")
    void updateContent() {
        Content content = new Content(
                ContentType.tvSeries,
                2001L,
                "Old Title",
                "Old Description",
                "https://example.com/old.png"
        );
        Content saved = contentRepository.save(content);

        saved.update("New Title", "New Description", "https://example.com/new.png");
        em.flush();
        em.clear();

        Content found = contentRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getTitle()).isEqualTo("New Title");
        assertThat(found.getDescription()).isEqualTo("New Description");
        assertThat(found.getThumbnailUrl()).isEqualTo("https://example.com/new.png");
    }
}
