package org.tron.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableScheduling
@SpringBootApplication
public class Application {

  @Bean
  public ObjectMapper objectMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    // disabled features:
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  @Autowired
  ObjectMapper objectMapper;

  @Bean
  public WebMvcConfigurer webMvcConfigurer() {
    return new WebMvcConfigurerAdapter() {

      @Override
      public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        final ProtobufHttpMessageConverter protobufconverter = new ProtobufHttpMessageConverter();
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        converters.add(converter);
        converters.add(protobufconverter);
        super.configureMessageConverters(converters);
      }
    };
  }

  @Configuration
  public class CorsConfig {
    private CorsConfiguration buildConfig() {
      CorsConfiguration corsConfiguration = new CorsConfiguration();
      corsConfiguration.addAllowedOrigin("*"); // 1
      corsConfiguration.addAllowedHeader("*"); // 2
      corsConfiguration.addAllowedMethod("*"); // 3
      return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", buildConfig()); // 4
      return new CorsFilter(source);
    }
  }

  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}