package mopl.api.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import mopl.api.domain.content.controller.ContentController;
import mopl.api.domain.content.dto.response.ContentDto;
import mopl.api.domain.content.service.ContentService;
import mopl.persistence.rdb.domain.content.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContentController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ContentController create API 테스트")
public class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContentService contentService;

    @Test
    @DisplayName("정상 multipart 요청이면 콘텐츠 생성에 성공한다")
    void create_success() throws Exception {
        ContentDto response = new ContentDto(
                "1",
                ContentType.movie,
                "기생충",
                "설명",
                "https://example.com/presigned-url",
                List.of("thriller", "drama"),
                0.0,
                0,
                0
        );

        when(contentService.create(any(), any())).thenReturn(response);

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(
                        new CreateRequestPayload("movie", "기생충", "설명", List.of("thriller", "drama"))
                )
        );
        MockMultipartFile thumbnailPart = new MockMultipartFile(
                "thumbnail",
                "thumb.png",
                "image/png",
                "image-bytes".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/contents")
                                .file(requestPart)
                                .file(thumbnailPart)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.type").value("movie"))
                .andExpect(jsonPath("$.title").value("기생충"));

        verify(contentService, times(1)).create(any(), any());
    }

    @Test
    @DisplayName("썸네일이 비어있으면 INVALID_THUMBNAIL 예외를 반환한다")
    void create_exception() throws Exception {
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(
                        new CreateRequestPayload("movie", "기생충", "설명", List.of("thriller", "drama"))
                )
        );
        MockMultipartFile emptyThumbnailPart = new MockMultipartFile(
                "thumbnail",
                "thumb.png",
                "image/png",
                new byte[0]
        );

        mockMvc.perform(
                        multipart("/api/contents")
                                .file(requestPart)
                                .file(emptyThumbnailPart)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("INVALID_THUMBNAIL"))
                .andExpect(jsonPath("$.code").value("C001"));
    }

    private record CreateRequestPayload(
            String type,
            String title,
            String description,
            List<String> tags
    ) {
    }
}
