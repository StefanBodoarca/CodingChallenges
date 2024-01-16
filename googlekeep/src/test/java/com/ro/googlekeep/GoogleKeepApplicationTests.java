package com.ro.googlekeep;

import com.ro.googlekeep.model.entities.NoteEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/*
how tests are isolated if we use the same container?
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
@Testcontainers
class GoogleKeepApplicationTests {

	@Autowired
	WebTestClient webTestClient;

	static final MySQLContainer<?> MY_SQL_CONTAINER;

	static {
		MY_SQL_CONTAINER = new MySQLContainer<>("mysql:latest");
		MY_SQL_CONTAINER.start();
	}

	@DynamicPropertySource
	static void configureTestProperties(DynamicPropertyRegistry registry){
		registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnAnExistingNoteById() {
		webTestClient
				.get()
				.uri("/notes/1")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().valueEquals("Content-Type", "application/json")
				.expectBody()
					.jsonPath("id").isEqualTo("1")
					.jsonPath("title").isEqualTo("note1")
					.jsonPath("content").isEqualTo("buy food");
	}

	@Test
	void shouldCreateANewNote() {
		final String title = "testNote";
		final String content = """
				testContent
					- write code
					- check unit vs automated tests
					- finish CRUD
				""";

		final NoteEntity newNote = new NoteEntity(title, content);

		final String location = webTestClient
				.post()
				.uri("/notes")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(newNote)
				.exchange()
				.expectAll(
						spec -> spec.expectStatus().isCreated(),
						spec -> spec.expectBody().isEmpty(),
						spec -> spec.expectHeader().exists("Location")
				).returnResult(String.class).getResponseHeaders().getFirst("Location");

        assert location != null;
        webTestClient
				.get()
				.uri(location)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().valueEquals("Content-Type", "application/json")
				.expectBody()
				.jsonPath("id").isNotEmpty()
				.jsonPath("title").isEqualTo(title)
				.jsonPath("content").isEqualTo(content);
	}

	@Test
	void shouldReturnAllNotes() {
		webTestClient
				.get()
				.uri("/notes")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().valueEquals("Content-Type", "application/json")
				.expectBody()
				.jsonPath("$.length()").isEqualTo(2);

	}

}
