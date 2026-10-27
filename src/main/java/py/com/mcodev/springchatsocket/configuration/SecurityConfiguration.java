package py.com.mcodev.springchatsocket.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import py.com.mcodev.springchatsocket.service.GoogleUserService;

@Configuration
public class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			GoogleUserService googleUserService,
			@Value("${spring.application.front.url}") String frontendUrl) throws Exception {
		http
				.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
						.ignoringRequestMatchers(
								new AntPathRequestMatcher("/chat-socket"),
								new AntPathRequestMatcher("/chat-socket/**")))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/oauth2/**", "/login/**").permitAll()
						.requestMatchers("/api/**", "/chat-socket", "/chat-socket/**").authenticated()
						.anyRequest().permitAll())
				.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
						new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
						new AntPathRequestMatcher("/api/**")))
				.oauth2Login(oauth -> oauth
						.userInfoEndpoint(userInfo -> userInfo.oidcUserService(googleUserService))
						.defaultSuccessUrl(frontendUrl + "/", true))
				.logout(logout -> logout.logoutSuccessHandler(
						(request, response, authentication) -> response.setStatus(HttpStatus.NO_CONTENT.value())));
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource(
			@Value("${spring.application.front.url}") String frontendUrl) {
		CorsConfiguration cors = new CorsConfiguration();
		cors.setAllowedOrigins(List.of(frontendUrl));
		cors.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS"));
		cors.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN"));
		cors.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", cors);
		return source;
	}
}
