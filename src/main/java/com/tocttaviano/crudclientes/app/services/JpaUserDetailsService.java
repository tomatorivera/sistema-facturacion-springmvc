package com.tocttaviano.crudclientes.app.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tocttaviano.crudclientes.app.models.Usuario;
import com.tocttaviano.crudclientes.app.repositories.IUsuarioRepository;

@Service
// Aquí se implementaría la lógica para cargar los detalles del usuario desde cualquier sistema de persistencia
public class JpaUserDetailsService implements UserDetailsService {
	
	private final IUsuarioRepository usuarioRepository;
	private final Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);
	
	public JpaUserDetailsService(IUsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Usuario solicitado: " + username);
		
		Optional<Usuario> optUsuario = Optional.ofNullable(usuarioRepository.findByUsername(username));
		if (optUsuario.isEmpty()) {
			throw new UsernameNotFoundException("Usuario no encontrado: " + username);
		}
		
		logger.info("Usuario encontrado: " + optUsuario.get().getUsername());
		if (optUsuario.get().getRoles() == null || optUsuario.get().getRoles().isEmpty()) {
			logger.warn("El usuario " + username + " no tiene roles asignados");
		}
		
		List<GrantedAuthority> authorities = optUsuario.get().getRoles().stream()
				.map(rol -> {
					logger.info("Rol del usuario: " + rol.getAuthority());
					return new SimpleGrantedAuthority(rol.getAuthority());
				})
				.collect(Collectors.toList());
		
		return new User(
				optUsuario.get().getUsername(),
				optUsuario.get().getPassword(),
				optUsuario.get().getEnabled(),
				// En una implementación real, se deberían verificar estas condiciones en función de los datos del usuario.
				true, // accountNonExpired - En este ejemplo, se asume que la cuenta no expira. 
				true, // credentialsNonExpired - En este ejemplo, se asume que las credenciales no expiran.
				true, // accountNonLocked - En este ejemplo, se asume que la cuenta no está bloqueada.
				authorities
		);
	}

}
