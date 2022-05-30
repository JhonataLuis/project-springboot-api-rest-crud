package net.curso.springbootapirest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.curso.springbootapirest.model.Usuario;
import net.curso.springbootapirest.repository.UsuarioRepository;

@Service
public class ImplementacaoDetailsService implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		/*CONSULTA NO BANCO DE DADOS O USUÁRIO*/
		
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		//SE NÃO ENCONTRAR USUARIO
		if(usuario == null) {
			
			throw new UsernameNotFoundException("Usuário não foi encontrado");
		}
		
		return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
	}

}
