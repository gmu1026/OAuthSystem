package net.cproduction.oauthsystem.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String profile;

    @Builder
    public User(String username, String email, String profile) {
        this.username = username;
        this.email = email;
        this.profile = profile;
    }

    public void update(String username, String profile) {
        this.username = username;
        this.profile = profile;
    }

    public boolean hasChanged(String username, String profile) {
        return !Objects.equals(this.username, username) ||
                !Objects.equals(this.profile, profile);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return Objects.hash(username, email, profile);
        }
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User user)) {
            return false;
        }

        if (id != null && user.id != null) {
            return Objects.equals(id, user.id);
        }

        return Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(profile, user.profile);
    }
}
