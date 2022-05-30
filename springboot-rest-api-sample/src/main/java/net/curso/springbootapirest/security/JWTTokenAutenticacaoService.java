package net.curso.springbootapirest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.curso.springbootapirest.ApplicationContextLoad;
import net.curso.springbootapirest.model.Usuario;
import net.curso.springbootapirest.repository.UsuarioRepository;



@Service
@Component
public class JWTTokenAutenticacaoService {

	/*TEMPO DE VALIDADE DO TOKEN 2 DIAS*/
	private static final long EXPIRATION_TIME =172800000;
	
	/*UMA SENHA UNICA PARA COMPOR A AUTENTICAÇÃO E AJUDAR NA SEGURANÇA*/
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	
	/*PREFIXO PADRÃO DE TOKEN*/
	private static final String TOKEN_PREFIX = "Bearer";
	
	/**/
	private static final String HEADER_STRING = "Authorization";
	
	
	/*GERANDO TOKEN DE AUTENTICADO E DIRECIONANDO AO CABEÇALHO E RESPOSTA HTTP*/
	public void addAuthentication(HttpServletResponse response, String username) throws IOException{
		
		/*MONTAGEM DO TOKEN*/
		
		String JWT = Jwts.builder()//CHAMA O GERADOR DE TOKEN
				.setSubject(username)//ADICIONA O USUÁRIO
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))//TEMPO DE EXPIRAÇÃO
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();//COMPACTAÇÃO E ALGORITIMOS DE GERAÇÃO DE SENHA
		
		/*JUNTA O TOKEN COM O PREFIXO*/
		String token = TOKEN_PREFIX + " " + JWT;//BEARER 8787we8787we8787we8787we
		
		/*ADICIONA O CABEÇALHO HTTP*/
		response.addHeader(HEADER_STRING, token);//AUTHORIZATION 8787we8787we8787we8787we
		
		/*LIBERANDO RESPOSTAS PARA PORTAS DIFERENTE DO PROJETO ANGULAR*/
		//response.addHeader("Access-Control-Allow-Origin", "*");
		
		ApplicationContextLoad.getApplicationContext()
		.getBean(UsuarioRepository.class).atualizaTokenUser(JWT, username);
		
		//LIBERANDO RESPOSTAS PARA PORTAS DIFERENTES QUE USAM A API OU CASO CLIENTES WEB
		liberacaoCors(response);
		
		/*ESCREVE TOKEN COMO RESPOSTA NO CORPO HTTP*/
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
		
		
		
	}
	
	/*RETORNA O USUÁRIO VALIDADO COM TOKEN OU CASO NÃO SEJA VALIDO RETORNA NULL*/
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		
		/*PEGA O TOKEN ENVIADO NO CABEÇALHO HTTP*/
		
		String token = request.getHeader(HEADER_STRING);
		
		
		try {
			
		
		if(token != null) {
			
			String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
			
			/*FAZ A VALIDAÇÃO DO TOKEN DO USUÁRIO NA REQUISIÇÃO*/
			String user = Jwts.parser().setSigningKey(SECRET)//BEARER 8787we8787we8787we8787we
					.parseClaimsJws(tokenLimpo)//8787we8787we8787we8787we
					.getBody().getSubject();//JOÃO SILVA
			
			if(user != null) {
				
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class).findUserByLogin(user);
				
				//SE USUÁRIO EXISTIR
				if(usuario != null) {
					
				
					//SE TOKEN LIMPO É IGUAL AO TOKEN DO USUARIO NO BANCO DE DADOS
					if(tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
						
					return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
					
					}
				}
				
			}
			
		}//FIM DA CONDIÇÃO DO TOKEN
		}catch (ExpiredJwtException e) {
			
			try {
				response.getOutputStream().println("Seu TOKEN está expirado, faça o login ou informe um novo Token para autenticação");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		liberacaoCors(response);
		
		return null;//NÃO AUTORIZADO
	
	}
	
	
//MÉTODO DE LIBERAÇÃO DE ACESSO DE CORS, CABEÇALHO E ORIGINS
	private void liberacaoCors(HttpServletResponse response) {
		
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
	
}
