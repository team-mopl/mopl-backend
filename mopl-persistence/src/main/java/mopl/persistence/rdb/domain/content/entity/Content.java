package mopl.persistence.rdb.domain.content.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mopl.persistence.rdb.domain.content.enums.ContentType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "contents",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_content_origin", columnNames = {"content_type", "origin_id"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(nullable = false, unique = true)
    private Long originId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailUrl;

    private double averageRating;

    private int reviewCount;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentTag> contentTags = new ArrayList<>();
}
