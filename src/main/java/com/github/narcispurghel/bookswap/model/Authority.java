package com.github.narcispurghel.bookswap.model;

import com.github.narcispurghel.bookswap.enums.AuthorityType;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

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
    
    @Override public String getAuthority() {
        return authorityType.name();
    }
}
