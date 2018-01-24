package net.ins.reactiveapp;

import net.ins.reactiveapp.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static java.util.Collections.emptyList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
//@WebFluxTest
/**
 * junitPlatformTest runs test classes matching filenames by pattern: ^.*Tests?$
 */
public class LearningappApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	private WebTestClient webTestClient;

	@BeforeAll
	void init() {
		webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
				.build();
	}

	@Test
	@DisplayName("Application context test")
	public void contextLoads() {
		System.out.println("App ctx checked: " + applicationContext);
		Assertions.assertNotNull(applicationContext, "application context has been autowired");
	}

	@Test
	@DisplayName("Should successfully return full users collection")
	public void shouldReturnWholeUsersList() {
		webTestClient.get()
				.uri("/api/users")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectBody()
				.jsonPath("$.[0].id").isEqualTo("0")
				.jsonPath("$.[0].userId").isEqualTo("0")
				.jsonPath("$.[0].firstName").isEqualTo("Ivan")
				.jsonPath("$.[0].lastName").isEqualTo("Ivanov")
				.jsonPath("$.[0].email").isEqualTo("IvanInvanov@corpmail.com")
				.jsonPath("$.[1].id").isEqualTo("1")
				.jsonPath("$.[1].userId").isEqualTo("1")
				.jsonPath("$.[1].firstName").isEqualTo("Petr")
				.jsonPath("$.[1].lastName").isEqualTo("Petrov")
				.jsonPath("$.[1].email").isEqualTo("PetrPetrov@corpmail.com")
				.jsonPath("$.[2].id").isEqualTo("2")
				.jsonPath("$.[2].userId").isEqualTo("2")
				.jsonPath("$.[2].firstName").isEqualTo("John")
				.jsonPath("$.[2].lastName").isEqualTo("Doe")
				.jsonPath("$.[2].email").isEqualTo("JohnDoe@corpmail.com");
	}

	@Test
	@DisplayName("Should successfully stream users as frames")
	public void shouldReturnUsersStream() {
		FluxExchangeResult<User> response = webTestClient.get()
				.uri("/api/users/stream")
				.exchange()
				.expectStatus().isOk()
				.returnResult(User.class);

		StepVerifier.create(response.getResponseBody())
				.expectNext(new User("0", 0L, "Ivan", "Ivanov", "IvanInvanov@corpmail.com", emptyList()))
				.expectNext(new User("1", 1L, "Petr", "Petrov", "PetrPetrov@corpmail.com", emptyList()))
				.expectNext(new User("2", 2L, "John", "Doe", "JohnDoe@corpmail.com", emptyList()))
				.expectComplete()
				.verify();
	}
}