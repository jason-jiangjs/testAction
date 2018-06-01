package org.vog.testa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.vog.testa.web.tag.dialect.VoDialect;

@SpringBootApplication
@ComponentScan(basePackages = "org.vog")
@ServletComponentScan(basePackages = "org.vog.dbd.web")
public class TestActionApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TestActionApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TestActionApplication.class);
	}

	@Bean
	public VoDialect createVoDialect() {
		return new VoDialect();
	}

}
