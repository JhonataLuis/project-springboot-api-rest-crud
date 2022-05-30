package net.curso.springbootapirest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * Spring Boot application starter class
 */
@SpringBootApplication
@EntityScan(basePackages = {"net.curso.springbootapirest.model"})
@ComponentScan(basePackages = {"net.*"})
@EnableJpaRepositories(basePackages = {"net.curso.springbootapirest.repository"})
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
public class Application implements WebMvcConfigurer{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //System.out.println(new BCryptPasswordEncoder().encode("123"));
    }
    
    //MAPEAMENTO GLOBAL QUE REFLETE EM TODO O SISTEMA
    //LIBERA ACESSO AO CONTROLLER
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    		
    	registry.addMapping("/usuario/**")//LIBERANDO MAPEAMENTO DO /USUARIO
    	.allowedMethods("*")//LIBERANDO POST E DELETE /PODE LIBERAR TODOS COM O *
    	.allowedOrigins("*");//LIBERANDO APENAS PARA DOIS CLIENTES /PODE LIBERAR TODOS COM O *
    	/*LIBERANDO APENAS REQUISIÇÕES POST APENAS PARA O USUÁRIO DO SERVIDOR 40*/
    	
    	}
}
