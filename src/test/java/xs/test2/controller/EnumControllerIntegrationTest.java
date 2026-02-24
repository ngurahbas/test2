package xs.test2.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EnumControllerIntegrationTest {

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:18.2")
            .withDatabaseName("testdb");

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Test
    void getGenders_shouldReturnAllGenderValues() {
        restClient = RestClient.create();

        var response = restClient.get()
                .uri("http://localhost:%d/api/enum/gender".formatted(port))
                .retrieve()
                .body(String.class);

        assertThat(response).isNotNull();
        assertThat(response).contains("\"value\":\"FEMALE\"");
        assertThat(response).contains("\"label\":\"Female\"");
        assertThat(response).contains("\"value\":\"MALE\"");
        assertThat(response).contains("\"label\":\"Male\"");
        assertThat(response).contains("\"value\":\"OTHER\"");
        assertThat(response).contains("\"label\":\"Other\"");
    }

    @Test
    void getIdentifierTypes_shouldReturnAllIdentifierTypeValues() {
        restClient = RestClient.create();

        var response = restClient.get()
                .uri("http://localhost:%d/api/enum/identifier-type".formatted(port))
                .retrieve()
                .body(String.class);

        assertThat(response).isNotNull();
        assertThat(response).contains("\"value\":\"MRN\"");
        assertThat(response).contains("\"label\":\"Mrn\"");
        assertThat(response).contains("\"value\":\"NATIONAL_ID\"");
        assertThat(response).contains("\"label\":\"National ID\"");
        assertThat(response).contains("\"value\":\"PHONE\"");
        assertThat(response).contains("\"label\":\"Phone\"");
        assertThat(response).contains("\"value\":\"EMAIL\"");
        assertThat(response).contains("\"label\":\"Email\"");
    }
}
