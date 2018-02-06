package net.ins.reactiveapp;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.ins.reactiveapp.domain.User;
import net.ins.reactiveapp.repository.ReactiveUserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static java.util.Collections.emptyList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
//@WebFluxTest
/**
 * junitPlatformTest runs test classes matching filenames by pattern: ^.*Tests?$
 */
@Slf4j
public class LearningappApplicationTests {

	private static final String LOCALHOST = "localhost";

	private final User[] testUsers = new User[]{
			new User("0", 0L, "Ivan", "Ivanov", "IvanInvanov@corpmail.com", emptyList()),
			new User("1", 1L, "Petr", "Petrov", "PetrPetrov@corpmail.com", emptyList()),
			new User("2", 2L, "John", "Doe", "JohnDoe@corpmail.com", emptyList())
	};

	private MongodExecutable mongodExecutable;

	private MongoClient mongoClient;

	private WebTestClient webTestClient;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ReactiveUserRepository userRepository;

	@Value("${test.mongo.port}")
	private int testMongoPort;

	@BeforeAll
	@SneakyThrows
	void init() {
		webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
				.build();

		IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
				.net(new Net(LOCALHOST, testMongoPort, Network.localhostIsIPv6()))
				.build();

		MongodStarter mongodStarter = MongodStarter.getDefaultInstance();
		mongodExecutable = mongodStarter.prepare(mongodConfig);
		mongodExecutable.start();
		mongoClient = new MongoClient(LOCALHOST, testMongoPort);
	}

	@AfterAll
	void cleanup() {
		if (mongodExecutable != null) {
			mongodExecutable.stop();
		}
	}

	@AfterEach
	void cleanupTest() {
		userRepository.deleteAll()
				.subscribe();
	}

	private void prepareMongoData() {
		userRepository.saveAll(Flux.fromArray(testUsers))
				.subscribe();
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
		prepareMongoData();
		WebTestClient.ListBodySpec<User> userListBodySpec = webTestClient.get()
				.uri("/api/users")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectBodyList(User.class)
				.isEqualTo(Arrays.asList(testUsers));
	}

	@Test
	@DisplayName("Should successfully stream users as frames")
	public void shouldReturnUsersStream() {
		prepareMongoData();
		FluxExchangeResult<User> response = webTestClient.get()
				.uri("/api/users/stream")
				.exchange()
				.expectStatus().isOk()
				.returnResult(User.class);

		StepVerifier.create(response.getResponseBody())
				.expectNext(testUsers[0])
				.expectNext(testUsers[1])
				.expectNext(testUsers[2])
				.expectComplete()
				.verify();
	}
}
