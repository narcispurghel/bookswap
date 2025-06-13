package com.github.narcispurghel.bookswap.repository;

import com.github.narcispurghel.bookswap.entity.User;
import com.github.narcispurghel.bookswap.model.UserDto;
import com.github.narcispurghel.bookswap.model.UserSecurity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {

    @Query(value = """
            SELECT u.id, u.email, u.first_name, u.last_name, u.password, u.created_at, u.updated_at
            FROM users u
            WHERE u.email = :email
            """)
    Mono<UserDto> findUserDtoByEmail(@Param(value = "email") String email);

    @Query(value = """
            SELECT u.id, u.email, u.password, u.is_account_non_expired, u.is_account_non_locked, u.is_credentials_non_expired, u.is_enabled
            FROM users u
            WHERE u.email = :email
            """)
    Mono<UserSecurity> findUserSecurityByEmail(@Param(value = "email") String email);

    @Query(value = """
            SELECT COUNT(*) > 0 from users u
            WHERE u.email = :email
            """)
    Mono<Boolean> existsByEmail(@Param(value = "email") String email);
}
