package lt.skafis.bankas.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun genericFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.authorizeHttpRequests { authorize ->
            authorize
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/index.html").permitAll()
                .requestMatchers("/**").permitAll()
//                .requestMatchers(HttpMethod.GET, "/source/**").authenticated()
//                .requestMatchers(HttpMethod.POST, "/source/**").hasAnyRole("USER", "ADMIN", "SUPER_ADMIN")
//                .requestMatchers(HttpMethod.DELETE, "/source/**").hasRole("SUPER_ADMIN")
                .anyRequest().denyAll()
        }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            }
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults())
            .cors { it.disable() }
            .csrf { it.disable() }
            .build()
    }
}
