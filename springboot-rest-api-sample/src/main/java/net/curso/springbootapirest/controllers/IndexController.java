package net.curso.springbootapirest.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import net.curso.springbootapirest.model.Usuario;
import net.curso.springbootapirest.model.UsuarioDTO;
import net.curso.springbootapirest.repository.UsuarioRepository;


@RestController//ARQUITETURA REST
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	//AÇÃO PARA INICIAR A PRIMEIRA PAGINA
	//SERVIÇO RESTFULL
	/*@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity init(){
		
		
		return new ResponseEntity("Hello Spring Boot API REST", HttpStatus.OK);
	}*/
	
	//MÉTODO PARA CONSULTAR UM USUÁRIO POR ID
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> consult(@PathVariable("id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		System.out.println("Executando versão 1");
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	/*//MÉTODO PARA CONSULTAR UM USUÁRIO POR ID
		@GetMapping(value = "V2/{id}", produces = "application/json")
		public ResponseEntity<Usuario> consult2(@PathVariable("id") Long id) {
			
			Optional<Usuario> usuario = usuarioRepository.findById(id);
			System.out.println("Executando versão 2");
			return new ResponseEntity(usuario.get(), HttpStatus.OK);
		}
		
		//MÉTODO PARA CONSULTAR UM USUÁRIO POR ID
				@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v3")
				public ResponseEntity<Usuario> consultV3(@PathVariable("id") Long id) {
					
					Optional<Usuario> usuario = usuarioRepository.findById(id);
					System.out.println("Executando versão 2");
					return new ResponseEntity(usuario.get(), HttpStatus.OK);
				}*/
	
	//MÉTODO PARA LISTAR OS USUÁRIOS DO BANCO
	//@CrossOrigin(origins = "")LIBERA ACESSO DE UM DETERMINADO LUGAR QUE ESPECIFICAR, SOMENTE ELE VAI TER ACESSO EX: WWW.GOOGLE.COM
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value = "cacheusuarios", allEntries = true)//CACHES QUE ESTÃO HÁ MUITO TEMPO ALLENTRIES REMOVE QUANDO CONSULTAR ESSE MÉTODO
	@CachePut("cacheusuarios")//IDENTIFICA QUE TEM ATUALIZAÇÕES E ADICIONA NO CACHE
	public ResponseEntity<List<Usuario>> usuario() throws InterruptedException{
		
		List<Usuario> listUsu = (List<Usuario>) usuarioRepository.findAll();
		
		return new ResponseEntity<List<Usuario>>(listUsu, HttpStatus.OK);
	}

	//MÉTODO PARA CADASTRAR NO BANCO DE DADOS
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {
		
		//FOR PARA PEGAR O USUÁRIO NOVO PARA CADASTAR UM TELEFONE PARA ELE JUNTO
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		/*CONSUMINDO UMA API PUBLICA EXTERNA*/
		
		URL url = new URL("https://viacep.com.br/ws/"+ usuario.getCep() +"/json/");
		URLConnection connection = url.openConnection();//ABRE A CONEXÃO
		InputStream is = connection.getInputStream();//OBTEM RETORNO COM OS DADOS DA REQUISIÇÃO
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		System.out.println("RESULT API EXTERNA ==> " +jsonCep.toString());
		
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setComplemento(userAux.getComplemento());
		usuario.setBairro(userAux.getBairro());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		usuario.setIbge(userAux.getIbge());
		usuario.setDdd(userAux.getDdd());
		
		/*CONSUMINDO UMA API PUBLICA EXTERNA*/
		
		
		//CRIPTOGRAFANDO A SENHA CADASTRADA
		String senhaCrypto = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCrypto);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	//MÉTODO PARA ATUALIZAR UM USUÁRIO NO BANCO DE DADOS
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario){
		
		//AÇÃO PARA ASSOCIAR O USUÁRIO AO TELEFONE PARA REALIZAR A ATUALIZAÇÃO
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
	
		/*String senhaCrypto = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCrypto);*/
		
		Usuario usuarioTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		//SE SENHA USUARIOTEMPORARIO NÃO FOR IGUAL A USUARIOSENHA
		if(!usuarioTemporario.getSenha().equals(usuario.getSenha())) {
			String senhaCrypto = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCrypto);
		}
		
		Usuario usUpdate = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usUpdate, HttpStatus.OK);
		
	}
	
	//MÉTODO PARA DELETAR UM USUÁRIO DO BANCO DE DADOS
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity delete(@PathVariable("id") Long id) {
		
		usuarioRepository.deleteById(id);
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	
}
