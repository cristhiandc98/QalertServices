package qalert.com.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import qalert.com.models.BaseData;
import qalert.com.utils.consts.EnvironmentConst;
import java.time.Duration;

import reactor.netty.http.client.HttpClient;

@Configuration
@ComponentScan
public class AppConfig implements WebMvcConfigurer{
	
	@Autowired
	private Environment env;
    
	@Bean
    DataSource dataSource() {
        HikariConfig config = new HikariConfig();
		
		config.setJdbcUrl(env.getRequiredProperty(EnvironmentConst.SPRING_DATASOURCE_HIKARI_URL));
        config.setUsername(env.getRequiredProperty(EnvironmentConst.SPRING_DATASOURCE_HIKARI_USUARIO));
        config.setPassword(env.getRequiredProperty(EnvironmentConst.SPRING_DATASOURCE_HIKARI_CONTRASENIA));
        config.setDriverClassName(env.getRequiredProperty(EnvironmentConst.SPRING_DATASOURCE_HIKARI_DRIVER_CLASS));

        return new HikariDataSource(config);
    }
 
    @Bean
    JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

	@Bean
    BaseData baseData() {
        BaseData data = new BaseData();
        // puedes leerlo del properties también
        data.setSchema(env.getRequiredProperty(EnvironmentConst.SPRING_DATASOURCE_HIKARI_SCHEMA));
        return data;
    }
 
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	

	@Bean
	ObjectMapper objectMapper() {
		JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);

        ObjectMapper objectMapper = new ObjectMapper(jsonFactory);

		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
		// Ignora los campos nulos
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		return objectMapper;
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
    WebClient izipayWebClient() {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30));

        return WebClient.builder()
                .baseUrl(env.getRequiredProperty(EnvironmentConst.IZIPAY_BASE_URL))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }



    @Bean
    CorsFilter corsFilter() {
        System.out.println("CORS FILTER ACTIVADO");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
