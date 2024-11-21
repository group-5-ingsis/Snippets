package com.ingsis.snippets.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
open class OAuth2ResourceServerSecurityConfiguration(
  @Value("\${auth0.audience}")
  val audience: String,
  @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  val issuer: String
) {
  @Bean
  open fun filterChain(http: HttpSecurity): SecurityFilterChain {
    http.authorizeHttpRequests {
      it
        .anyRequest().authenticated()
    }
      .oauth2ResourceServer { it.jwt(withDefaults()) }
      .cors { it.configurationSource(corsConfigurationSource()) }
    return http.build()
  }

  @Bean
  open fun jwtDecoder(): JwtDecoder {
    val jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuer).build()
    val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
    val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
    val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
    jwtDecoder.setJwtValidator(withAudience)
    return jwtDecoder
  }

  @Bean
  open fun corsConfigurationSource(): CorsConfigurationSource {
    val corsConfig = CorsConfiguration()
    corsConfig.allowedOrigins = listOf("http://localhost:5173", "https://printscript-group5.duckdns.org", "https://testing-ps-g5.duckdns.org")
    corsConfig.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
    corsConfig.allowedHeaders = listOf("Authorization", "Content-Type")
    corsConfig.allowCredentials = true

    val source = UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", corsConfig)
    return source
  }
}
