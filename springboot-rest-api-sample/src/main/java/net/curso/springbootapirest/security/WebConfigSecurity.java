package net.curso.springbootapirest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import net.curso.springbootapirest.service.ImplementacaoDetailsService;

/*MAPEIA URL, ENDEREÇOS, AUTORIZA OU BLOQUEIA ACESSO A URL*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
	private ImplementacaoDetailsService implementacaoDetailsService;
	
	//CONFIGURA AS SOLICITAÇÕES DE ACESSO POR HTTP
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		/*ATIVANDO A PROTEÇÃO CONTRA USUÁRIOS QUE NÃO ESTÃO VALIDADOS POR TOKEN*/
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		
		/*ATIVANDO A PERMISSÃO PARA ACESSO A PAGINA INICIAL DO SISTEMA EX: SISTEMA.COM.BR/INDEX*/
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		
		//
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		
		/*URL DE LOGOUT - REDIRECIONA APÓS O USER DESLOGAR DO SISTEMA*/
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		/*MAPEIA O USUÁRIO DE LOGOUT E INVALIDA O USUÁRIO*/
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		/*FILTRA REQUISIÇÕES DE LOGIN PARA AUTENTICAÇÃO*/
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
		
		/*FILTRA DEMAIS REQUISIÇÕES PARA VERIFICAR A PRESENÇÃO DO TOKEN JWT NO HEADER HTTP*/
		.addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
		
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			
		/*SERVICE QUE IRÁ CONSULTAR O USUÁRIO NO BANCO DE DADOS*/
		auth.userDetailsService(implementacaoDetailsService)
		
		//PADRÃO DE CODIFICAÇÃO DE SENHA
		.passwordEncoder(new BCryptPasswordEncoder());
		
	}
}
