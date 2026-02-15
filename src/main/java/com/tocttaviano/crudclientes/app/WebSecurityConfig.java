package com.tocttaviano.crudclientes.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * Registro de usuarios en memoria para pruebas. En un entorno de producción, se debería implementar un UserDetailsService
	 * que consulte una base de datos u otro sistema de almacenamiento.
	 * 
	 * @return Un UserDetailsService con usuarios registrados en memoria.
	 * @throws Exception Si ocurre un error al crear el UserDetailsService.
	 */
	@Bean
	UserDetailsService userDetailsService() throws Exception {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		
		// Usuario normal de prueba
		manager.createUser(User
				.withUsername("demo")
				.password(passwordEncoder().encode("demo"))
				.roles("USER")
				.build()
		);
		
		// Usuario administrador de prueba
		manager.createUser(User
	               .withUsername("admin")
	               .password(passwordEncoder().encode("admin"))
	               .roles("ADMIN","USER")
	               .build()
	    );
		
		return manager;
	}
	
	/**
	 * Configuración de seguridad HTTP. Define las reglas de acceso a las diferentes rutas de la aplicación, 
	 * así como la configuración de inicio de sesión y cierre de sesión.
	 * 
	 * @param http El objeto HttpSecurity utilizado para configurar la seguridad HTTP.
	 * @return Un SecurityFilterChain que define la configuración de seguridad para la aplicación.
	 * @throws Exception Si ocurre un error al configurar la seguridad HTTP.
	 */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http.authorizeHttpRequests(
		            (authz) -> authz
		                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar", "/index").permitAll()
		                .requestMatchers("/detalle/**").hasAnyRole("USER")
		                .requestMatchers("/uploads/**").hasAnyRole("USER")
		                .requestMatchers("/crear/**").hasAnyRole("ADMIN")
		                .requestMatchers("/editar/**").hasAnyRole("ADMIN")
		                .requestMatchers("/guardar/**").hasAnyRole("ADMIN")
		                .requestMatchers("/eliminar/**").hasAnyRole("ADMIN")
		                .requestMatchers("/factura/**").hasAnyRole("ADMIN")
		                .anyRequest().authenticated()
         )
		 // Formulario y permisos para login
         .formLogin(login -> {
        	 login.loginPage("/login").permitAll();
        	 login.defaultSuccessUrl("/", true);
         })
         // Permisos para logout
         .logout(logout -> logout.permitAll());
		 
		return http.build();
	}
	
}
