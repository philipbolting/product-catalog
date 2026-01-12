package de.philipbolting.product_catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ProductCatalogApplication {

	static void main(String[] args) {
		SpringApplication.run(ProductCatalogApplication.class, args);
	}

}
