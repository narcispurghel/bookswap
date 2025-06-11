package com.github.narcispurghel.bookswap.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Table("users")
public class User implements UserDetails {
    
    @Id
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Set<Authority> authorities = new HashSet<>();
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override public String getPassword() {
        return password;
    }
    
    @Override public String getUsername() {
        return email;
    }
    
    @Override public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }
    
    @Override public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }
    
    @Override public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }
    
    @Override public boolean isEnabled() {
        return isEnabled;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }
    
    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }
    
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }
    
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = new HashSet<>(authorities);
    }
    
    @Override public boolean equals(Object o) {
        if (!(o instanceof User user)) {
            return false;
        }
        return isAccountNonExpired == user.isAccountNonExpired &&
               isAccountNonLocked == user.isAccountNonLocked &&
               isCredentialsNonExpired == user.isCredentialsNonExpired &&
               isEnabled == user.isEnabled &&
               Objects.equals(id, user.id) &&
               Objects.equals(firstName, user.firstName) &&
               Objects.equals(lastName, user.lastName) &&
               Objects.equals(password, user.password) &&
               Objects.equals(email, user.email) &&
               Objects.equals(createdAt, user.createdAt) &&
               Objects.equals(updatedAt, user.updatedAt) &&
               Objects.equals(authorities, user.authorities);
    }
    
    @Override public int hashCode() {
        return Objects.hash(id,
                            firstName,
                            lastName,
                            isAccountNonExpired,
                            isAccountNonLocked,
                            isCredentialsNonExpired,
                            isEnabled,
                            password,
                            email,
                            createdAt,
                            updatedAt,
                            authorities);
    }
}
