package ru.doc.converter;

import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

  @Bean
  public OfficeManager officeManager() throws OfficeException {
    OfficeManager manager = LocalOfficeManager.install();
    manager.start();
    return manager;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
