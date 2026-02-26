package mopl.api.domain.content.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mopl.api.domain.content.dto.request.CreateContentDto;
import mopl.api.domain.content.dto.response.ContentDto;
import mopl.api.domain.content.exception.ContentErrorCode;
import mopl.api.domain.content.exception.ContentException;
import mopl.api.domain.content.service.ContentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "콘텐츠 생성")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ContentDto> create(
            @RequestPart("request") @Valid CreateContentDto request,
            @RequestPart(value = "thumbnail") MultipartFile thumbnail
    ) {
        // 썸네일 필수
        if (thumbnail == null || thumbnail.isEmpty()) {
            throw new ContentException(ContentErrorCode.INVALID_THUMBNAIL);
        }

        return ResponseEntity.ok(contentService.create(request, thumbnail));
    }
}
