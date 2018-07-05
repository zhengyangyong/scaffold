package org.apache.servicecomb.scaffold.log;

import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableServiceComb
public class LogApplication {
  public static void main(String[] args) {
    SpringApplication.run(LogApplication.class, args);
  }
}
