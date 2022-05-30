package net.curso.springbootapirest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.curso.springbootapirest.model.Usuario;

/*ESTABELECE O NOSSO GERENCIADOR DE TOKEN*/
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter{

	/*OBRIGAMOS A AUTENTICAR A URL*/
	protected JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
		
		/*OBRIGA A AUTENTICAR A URL*/
		super(new AntPathRequestMatcher(url));
		
		/*GERENCIADOR DE AUTENTICAÇÃO*/
		setAuthenticationManager(authenticationManager);
	}

	/*RETORNA O USUÁRO AO PROCESSAR A AUTENTICAÇÃO*/
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		/*ESTÁ PEGANDO O TOKEN PARA VALIDAR*/
		Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
		
		/*RETORNA O USUÁRIO LOGIN, SENHA E ACESSOS*/
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha()));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());
	}

}
