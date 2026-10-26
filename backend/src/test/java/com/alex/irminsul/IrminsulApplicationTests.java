package com.alex.irminsul;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires a running PostgreSQL instance with DB_PASSWORD configured")
class IrminsulApplicationTests {

	@Test
	void contextLoads() {
	}

}