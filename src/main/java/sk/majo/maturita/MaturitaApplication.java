package sk.majo.maturita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
@EnableOAuth2Sso
public class MaturitaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MaturitaApplication.class, args);
    }
}
