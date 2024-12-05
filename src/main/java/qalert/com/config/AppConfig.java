package qalert.com.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import qalert.com.utils.consts.EnvironmentConst;

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
	AmazonS3 awsS3() {
		AWSCredentials credentials = new BasicAWSCredentials(
				env.getRequiredProperty(EnvironmentConst.AWS_S3_ACCESS_KEY), 
				env.getRequiredProperty(EnvironmentConst.AWS_S3_SECRET_KEY)
				);
		
		return AmazonS3ClientBuilder
			.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.US_EAST_2)
			.build();
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
