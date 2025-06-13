package com.github.narcispurghel.bookswap.entity;

import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Table("users_authorities")
public class UserAuthority {
    private UUID userId;
    private UUID authorityId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(UUID authorityId) {
        this.authorityId = authorityId;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof UserAuthority that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) &&
                Objects.equals(authorityId, that.authorityId);
    }

    @Override public int hashCode() {
        return Objects.hash(userId, authorityId);
    }
}
