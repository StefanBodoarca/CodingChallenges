package com.ro.googlekeep;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.ro.googlekeep.model.entities.NoteEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoogleKeepApplicationTests {
	@LocalServerPort
	private int port;

	@Autowired
	WebTestClient webTestClient = WebTestClient
			.bindToServer()
			.baseUrl("https://localhost:%d".formatted(port))
			.build();

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
	@Transactional
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

}
