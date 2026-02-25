package mopl.api.domain.content.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mopl.api.domain.content.dto.request.CreateContentDto;
import mopl.api.domain.content.dto.response.ContentDto;
import mopl.persistence.rdb.domain.content.entity.Content;
import mopl.persistence.rdb.domain.content.entity.Tag;
import mopl.persistence.rdb.domain.content.repository.ContentRepository;
import mopl.persistence.rdb.domain.content.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final TagRepository tagRepository;

    public ContentDto create(CreateContentDto req, MultipartFile thumbnail) {
        log.info("콘텐츠 생성 요청: {}", req.title());

        String thumbnailUrl = "https://example.com/thumbnail.png"; // TODO: S3에 썸네일 업로드
        Content content = new Content(req.type(), req.title(), req.description(), thumbnailUrl);    // 콘텐츠 생성
        addTagsToContent(req.tags(), content);                                                      // 태그 추가 및 저장

        Content newContent = contentRepository.save(content);
        List<String> tagNames = getTagNames(newContent);
        String presignedUrl = "https://example.com/presigned-url"; // TODO: presignedUrl 발급

        return toDto(newContent, presignedUrl, tagNames, 0);
    }

    /** 콘텐츠 태그 매핑 및 저장 */
    private void addTagsToContent(List<String> tagNames, Content content) {
        if (tagNames == null || tagNames.isEmpty()) return;

        tagNames.stream()
                .distinct()
                .forEach(tagName -> {
                    Tag tag = tagRepository.findByTag(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(tagName)));

                    content.addTag(tag);
                });
    }

    /** 콘텐츠에서 태그 이름만 추출 */
    private List<String> getTagNames(Content content) {
        return content.getContentTags().stream()
                .map(Contenttag -> Contenttag.getTag().getTag())
                .toList();
    }

    /** Dto 변환 */
    private ContentDto toDto(Content content, String thumbnailUrl, List<String> tagNames, int watchCount) {
        return new ContentDto(
                content.getId().toString(),
                content.getContentType(),
                content.getTitle(),
                content.getDescription(),
                thumbnailUrl,
                tagNames,
                content.getAverageRating(),
                content.getReviewCount(),
                watchCount
        );
    }
}
