package de.philipbolting.product_catalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@Import(ContainersConfig.class)
class ProductCatalogApplicationTests {

    @Test
	void contextLoads() {
	}

}
