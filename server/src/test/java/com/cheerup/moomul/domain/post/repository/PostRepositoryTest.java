package com.cheerup.moomul.domain.post.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
// @DataJdbcTest
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

	@Container
	public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0");

	@Autowired
	PostRepository postRepository;

	@Test
	void connectionEstablished() {
		assertThat(mysqlContainer.isCreated()).isTrue();
		assertThat(mysqlContainer.isRunning()).isTrue();
	}
}