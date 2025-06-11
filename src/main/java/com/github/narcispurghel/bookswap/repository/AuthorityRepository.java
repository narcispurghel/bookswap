package com.github.narcispurghel.bookswap.repository;

import com.github.narcispurghel.bookswap.entity.Authority;
import com.github.narcispurghel.bookswap.model.AuthorityDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Repository
public interface AuthorityRepository extends ReactiveCrudRepository<Authority, UUID> {
    
    @Query(value = """
                   SELECT a.authority_type FROM authorities a
                   WHERE a.id IN (:authorityId)
                   """)
    Flux<AuthorityDto> findAllAuthorityDtoByIds(@Param(value = "authorityIds")
                                                Set<UUID> authorityIds);
    
    @Query("""
           SELECT * FROM authorities a
           WHERE a.authority_type = :authorityType
           """)
    Mono<Authority> findByAuthorityType(
            @Param(value = "authorityType") String authorityType);
}
