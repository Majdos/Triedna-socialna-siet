package sk.majo.maturita.security.authentication;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties("domains")
@Data
@NoArgsConstructor
public class DomainConfiguration {
    private Set<String> allowedDomains;
}
