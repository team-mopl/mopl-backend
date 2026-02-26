package mopl.api.content;

import mopl.api.domain.content.dto.request.CreateContentDto;
import mopl.api.domain.content.dto.response.ContentDto;
import mopl.api.domain.content.service.ContentService;
import mopl.persistence.rdb.domain.content.entity.Content;
import mopl.persistence.rdb.domain.content.entity.Tag;
import mopl.persistence.rdb.domain.content.enums.ContentType;
import mopl.persistence.rdb.domain.content.repository.ContentRepository;
import mopl.persistence.rdb.domain.content.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContentService create 단위 테스트")
public class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ContentService contentService;

    @Test
    @DisplayName("새 태그와 중복 태그가 함께 오면 distinct 처리 후 저장한다")
    void create_savesDistinctTagsAndReturnsDto() {
        CreateContentDto request = new CreateContentDto(
                ContentType.movie,
                "기생충",
                "설명",
                List.of("thriller", "drama", "thriller")
        );
        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnail",
                "thumb.png",
                "image/png",
                "image-bytes".getBytes(StandardCharsets.UTF_8)
        );

        when(tagRepository.findByTag("thriller")).thenReturn(Optional.empty());
        when(tagRepository.findByTag("drama")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag savedTag = invocation.getArgument(0);
            String tagName = savedTag.getTag();
            long id = "thriller".equals(tagName) ? 10L : 20L;
            ReflectionTestUtils.setField(savedTag, "id", id);
            return savedTag;
        });

        when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> {
            Content savedContent = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedContent, "id", 1L);
            return savedContent;
        });

        ContentDto result = contentService.create(request, thumbnail);

        assertEquals("1", result.id());
        assertEquals(ContentType.movie, result.type());
        assertEquals("기생충", result.title());
        assertEquals("설명", result.description());
        assertEquals("https://example.com/presigned-url", result.thumbnailUrl());
        assertEquals(List.of("thriller", "drama"), result.tags());
        assertEquals(0.0, result.averageRating());
        assertEquals(0, result.reviewCount());
        assertEquals(0, result.watchCount());

        verify(tagRepository, times(1)).findByTag("thriller");
        verify(tagRepository, times(1)).findByTag("drama");
        verify(tagRepository, times(2)).save(any(Tag.class));
        verify(contentRepository, times(1)).save(any(Content.class));
    }

    @Test
    @DisplayName("기존 태그가 있으면 새 태그를 생성하지 않고 재사용한다")
    void create_reusesExistingTag() {
        CreateContentDto request = new CreateContentDto(
                ContentType.movie,
                "기생충",
                "설명",
                List.of("thriller")
        );
        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnail",
                "thumb.png",
                "image/png",
                "image-bytes".getBytes(StandardCharsets.UTF_8)
        );
        Tag existingTag = new Tag("thriller");
        ReflectionTestUtils.setField(existingTag, "id", 10L);

        when(tagRepository.findByTag("thriller")).thenReturn(Optional.of(existingTag));
        when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> {
            Content savedContent = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedContent, "id", 2L);
            return savedContent;
        });

        ContentDto result = contentService.create(request, thumbnail);

        assertEquals("2", result.id());
        assertEquals(List.of("thriller"), result.tags());

        verify(tagRepository, times(1)).findByTag("thriller");
        verify(tagRepository, never()).save(argThat(tag -> "thriller".equals(tag.getTag())));
        verify(contentRepository, times(1)).save(any(Content.class));
    }

    @Test
    @DisplayName("태그가 null이면 태그 저장 로직 없이 콘텐츠만 저장한다")
    void create_skipsTagMapping_whenTagsIsNull() {
        CreateContentDto request = new CreateContentDto(
                ContentType.movie,
                "기생충",
                "설명",
                null
        );
        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnail",
                "thumb.png",
                "image/png",
                "image-bytes".getBytes(StandardCharsets.UTF_8)
        );

        when(contentRepository.save(any(Content.class))).thenAnswer(invocation -> {
            Content savedContent = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedContent, "id", 3L);
            return savedContent;
        });

        ContentDto result = contentService.create(request, thumbnail);

        assertEquals("3", result.id());
        assertEquals(List.of(), result.tags());

        verify(tagRepository, never()).findByTag(any());
        verify(tagRepository, never()).save(any(Tag.class));
        verify(contentRepository, times(1)).save(any(Content.class));
    }
}
