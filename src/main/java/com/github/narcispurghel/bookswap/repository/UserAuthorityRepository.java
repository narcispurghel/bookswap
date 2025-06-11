package com.github.narcispurghel.bookswap.repository;

import com.github.narcispurghel.bookswap.entity.UserAuthority;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface UserAuthorityRepository extends ReactiveCrudRepository<UserAuthority,
        UUID> {
    @Query(value = """
                   SELECT * FROM users_authorities ua
                   WHERE ua.user_id = :userId
                   """)
    Flux<UserAuthority> findAllByUserId(@Param(value = "userId") UUID userId);
}
