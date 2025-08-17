package br.com.famper.events.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.famper.events")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}