package com.github.narcispurghel.bookswap.model;

import com.github.narcispurghel.bookswap.entity.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserDetailsDto implements UserDetails {

    private final String username;

    private final String passwordHash;

    private final boolean isEmailVerified;

    private final boolean isAccountNonExpired;

    private final boolean isAccountNonLocked;

    private final boolean isCredentialsNonExpired;

    private final boolean isEnabled;

    private final Set<Authority> authorities;

    public UserDetailsDto(String username,
            String passwordHash,
            boolean isEmailVerified,
            boolean isAccountNonExpired,
            boolean isAccountNonLocked,
            boolean isCredentialsNonExpired,
            boolean isEnabled, Set<Authority> authorities) {
        this.username = Objects.requireNonNull(username);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.isEmailVerified = isEmailVerified;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
        this.authorities = new HashSet<>(Objects.requireNonNull(authorities));
    }


    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    /**
     * @return a NonNull collection of {@link Authority authority}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Objects.requireNonNull(authorities);
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    /**
     * @return the username used to authenticate the user (never null)
     */
    @Override
    public String getUsername() {
        return Objects.requireNonNull(username);
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserDetailsDto that)) {
            return false;
        }
        return isEmailVerified == that.isEmailVerified &&
                isAccountNonExpired == that.isAccountNonExpired &&
                isAccountNonLocked == that.isAccountNonLocked &&
                isCredentialsNonExpired == that.isCredentialsNonExpired &&
                isEnabled == that.isEnabled &&
                Objects.equals(username, that.username) &&
                Objects.equals(passwordHash, that.passwordHash) &&
                Objects.equals(authorities, that.authorities);
    }

    @Override public int hashCode() {
        return Objects.hash(username,
                passwordHash,
                isEmailVerified,
                isAccountNonExpired,
                isAccountNonLocked,
                isCredentialsNonExpired,
                isEnabled,
                authorities);
    }
}
