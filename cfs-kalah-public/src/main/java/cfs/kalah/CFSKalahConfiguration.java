package cfs.kalah;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@EntityScan(basePackages = "cfs.kalah.domain")
@EnableJpaRepositories(basePackages = "cfs.kalah.repository")
@EnableTransactionManagement
@Configuration
public class CFSKalahConfiguration {
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // This code can be used to change the read timeout for testing
        //SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        //simpleClientHttpRequestFactory.setReadTimeout(100);  // millis
        
        return restTemplate;
    }
    
}
