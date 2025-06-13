package com.github.narcispurghel.bookswap.config;

import com.github.narcispurghel.bookswap.security.JwtSecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.AUTHENTICATION_ENDPOINT;
import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.DOCUMENTATION_ENDPOINT;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final JwtSecurityFilter jwtSecurityFilter;

    public SecurityConfig(ReactiveUserDetailsService reactiveUserDetailsService,
            JwtSecurityFilter jwtSecurityFilter) {
        this.reactiveUserDetailsService = reactiveUserDetailsService;
        this.jwtSecurityFilter = jwtSecurityFilter;
    }

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(
                        NoOpServerSecurityContextRepository.getInstance())
                .authenticationManager(authenticationManager())
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec.pathMatchers("/v3/api-docs").permitAll();
                    authorizeExchangeSpec.pathMatchers("/").permitAll();
                    authorizeExchangeSpec.pathMatchers("/favicon.ico").permitAll();
                    authorizeExchangeSpec.pathMatchers(AUTHENTICATION_ENDPOINT + "/**")
                            .permitAll();
                    authorizeExchangeSpec.pathMatchers(DOCUMENTATION_ENDPOINT + "/**")
                            .permitAll();
                    authorizeExchangeSpec.anyExchange().authenticated();
                })
                .exceptionHandling(
                        exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint(
                                        ((serverWebExchange,
                                                authenticationException) -> {
                                            serverWebExchange.getResponse()
                                                    .setStatusCode(HttpStatusCode.valueOf(401));
                                            return Mono.empty();
                                        }))
                                .accessDeniedHandler(
                                        ((serverWebExchange, accessDeniedException) -> {
                                            serverWebExchange.getResponse()
                                                    .setStatusCode(
                                                            HttpStatusCode.valueOf(403));
                                            return Mono.empty();
                                        })))
                .addFilterBefore(jwtSecurityFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(
                        reactiveUserDetailsService);
        authManager.setPasswordEncoder(passwordEncoder());
        return authManager;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
