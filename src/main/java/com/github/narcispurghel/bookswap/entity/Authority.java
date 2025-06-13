package com.github.narcispurghel.bookswap.entity;

import com.github.narcispurghel.bookswap.enums.AuthorityType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;
import java.util.UUID;

@Table("authorities")
public class Authority implements GrantedAuthority {
    @Id
    private UUID id;
    private AuthorityType authorityType;

    public AuthorityType getAuthorityType() {
        return authorityType;
    }

    public void setAuthorityType(AuthorityType authorityType) {
        this.authorityType = authorityType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override public String getAuthority() {
        return authorityType.name();
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Authority authority)) {
            return false;
        }
        return Objects.equals(id, authority.id) &&
                authorityType == authority.authorityType;
    }

    @Override public int hashCode() {
        return Objects.hash(id, authorityType);
    }
}
