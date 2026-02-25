package mopl.persistence.rdb.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mopl.persistence.rdb.domain.user.enums.Role;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name  = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = true, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    @Column (name = "locked", nullable = false)
    private Boolean locked;

    @Column(name = "profile_image_url", nullable = true)
    private String profileImageUrl;

    @Column(name = "provider", nullable = true)
    private String provider;

    @Column(name  = "provider_id", nullable = true)
    private String providerId;
}
