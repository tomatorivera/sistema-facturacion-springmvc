package com.tocttaviano.crudclientes.app;

import java.nio.file.Paths;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	/***
	 * Este método se encarga de configurar los manejadores de recursos estáticos. En este caso, 
	 * se configura un manejador para servir archivos desde la carpeta "uploads" del sistema de archivos.
	 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);

        String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
        
        // Para que se busque en la carpeta real del proyecto
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourcePath);
    }
    
    /**
     * Este método se encarga de configurar los controladores de vista. 
     * En este caso, se configura un controlador para la ruta "/auth/403" que devuelve la vista 
     * "/auth/403". Esto es útil para mostrar una página de error personalizada cuando un usuario 
     * intenta acceder a una página sin los permisos necesarios.
     */
    public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/auth/403").setViewName("/auth/403");
	}
    
    @Bean
	static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
    
    /**
     * Se encarga de gestionar el adaptador que resolverá el lenguaje (una cookie, session...)
     * @return LocaleResolver
     */
    @Bean
    LocaleResolver localeResolver() {
    	SessionLocaleResolver localeResolver = new SessionLocaleResolver(); // <-- En este caso lo guardo en la sesión
    	localeResolver.setDefaultLocale(Locale.of("es", "ES"));
    	return localeResolver;
    }
    
    /**
     * Se ejecuta entre cada request y ejecuta algo mediante un handler, como cambiar el lenguaje
     * @return LocaleChangeInterceptor
     */
    @Bean
    LocaleChangeInterceptor localeChangeInterceptor() {
    	LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
    	localeInterceptor.setParamName("lang"); // <-- Parámetro que ejecuta al interceptor para cambiar el lenguaje
    	return localeInterceptor;
    }

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
    
}
