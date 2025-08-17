package br.com.famper.events.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "br.com.famper.events")
@EnableJpaRepositories(basePackages = "br.com.famper.events.persistence")
@EntityScan(basePackages = "br.com.famper.events.domain")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}