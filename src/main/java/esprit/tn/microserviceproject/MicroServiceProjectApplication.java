package esprit.tn.microserviceproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MicroServiceProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceProjectApplication.class, args);
    }

}

