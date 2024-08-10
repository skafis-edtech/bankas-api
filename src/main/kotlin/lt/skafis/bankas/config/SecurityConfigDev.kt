package lt.skafis.bankas.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@Profile("prod")
class SecurityConfigDev {

    @Bean
    fun genericFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.authorizeHttpRequests { authorize ->
            authorize
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/index.html", "/error").permitAll()
                .requestMatchers("/**").permitAll()
                .anyRequest().denyAll()
        }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            }
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults())
            .csrf { it.disable() }
            .build()
    }
}
