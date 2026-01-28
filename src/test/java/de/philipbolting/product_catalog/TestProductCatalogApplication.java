package de.philipbolting.product_catalog;

import org.springframework.boot.SpringApplication;

public class TestProductCatalogApplication {

    static void main(String[] args) {
        SpringApplication
                .from(ProductCatalogApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}
