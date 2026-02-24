package xs.test2.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PatientControllerIntegrationTest {

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
    @Order(1)
    void createPatientAddIdentifierAndGet_shouldReturn201AndPatientData() {
        restClient = RestClient.builder()
                .defaultStatusHandler(
                        status -> status.value() >= 400,
                        (request, response) -> {
                            String body = new String(response.getBody().readAllBytes());
                            System.err.println("Error response: " + response.getStatusCode() + " - " + body);
                        }
                )
                .build();

        String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "dob": "1990-01-15",
                    "gender": "MALE",
                    "phoneNo": "0412345678",
                    "australianAddress": {
                        "address": "123 Main St",
                        "suburb": "Sydney",
                        "state": "NSW",
                        "postcode": "2000"
                    }
                }
                """;

        var createResponse = restClient.post()
                .uri("http://localhost:%d/api/patient".formatted(port))
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(String.class);

        assertThat(createResponse).isNotNull();
        assertThat(createResponse).contains("\"id\"");

        String patientId = createResponse.split("\"id\":\"")[1].split("\"")[0];

        String identifierRequestBody = """
                {
                    "idType": "EMAIL",
                    "idValue": "john.doe@example.com"
                }
                """;

        var addIdentifierResponse = restClient.post()
                .uri("http://localhost:%d/api/patient/%s/identifier".formatted(port, patientId))
                .header("Content-Type", "application/json")
                .body(identifierRequestBody)
                .retrieve()
                .body(String.class);

        assertThat(addIdentifierResponse).isNotNull();
        assertThat(addIdentifierResponse).contains("\"idType\":\"EMAIL\"");
        assertThat(addIdentifierResponse).contains("\"idValue\":\"john.doe@example.com\"");

        String updateRequestBody = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "dob": "1990-01-15",
                    "gender": "MALE",
                    "phoneNo": "0498765432",
                    "australianAddress": {
                        "address": "123 Main St",
                        "suburb": "Sydney",
                        "state": "NSW",
                        "postcode": "2000"
                    }
                }
                """;

        var updateResponse = restClient.put()
                .uri("http://localhost:%d/api/patient/%s".formatted(port, patientId))
                .header("Content-Type", "application/json")
                .body(updateRequestBody)
                .retrieve()
                .body(String.class);

        assertThat(updateResponse).isNotNull();
        assertThat(updateResponse).contains("\"phoneNo\":\"+61498765432\"");

        var getResponse = restClient.get()
                .uri("http://localhost:%d/api/patient/%s".formatted(port, patientId))
                .retrieve()
                .body(String.class);

        assertThat(getResponse).isNotNull();
        assertThat(getResponse).contains("\"id\":\"%s\"".formatted(patientId));
        assertThat(getResponse).contains("\"firstName\":\"John\"");
        assertThat(getResponse).contains("\"lastName\":\"Doe\"");
        assertThat(getResponse).contains("\"dob\":\"1990-01-15\"");
        assertThat(getResponse).contains("\"gender\":\"MALE\"");
        assertThat(getResponse).contains("\"phoneNo\":\"+61498765432\"");
        assertThat(getResponse).contains("\"address\":\"123 Main St\"");
        assertThat(getResponse).contains("\"suburb\":\"Sydney\"");
        assertThat(getResponse).contains("\"state\":\"NSW\"");
        assertThat(getResponse).contains("\"postcode\":\"2000\"");
        assertThat(getResponse).contains("\"idType\":\"EMAIL\"");
        assertThat(getResponse).contains("\"idValue\":\"john.doe@example.com\"");
        assertThat(getResponse).contains("\"idType\":\"PHONE\"");
        assertThat(getResponse).contains("\"idValue\":\"+61498765432\"");
        assertThat(getResponse).doesNotContain("\"idValue\":\"+61412345678\"");
    }

    @Test
    void deletePatientFlow_shouldDeleteIdentifierAndPatient() {
        var localRestClient = RestClient.builder()
                .defaultStatusHandler(
                        status -> status.value() >= 400,
                        (request, response) -> {
                            String body = new String(response.getBody().readAllBytes());
                            System.err.println("Error response: " + response.getStatusCode() + " - " + body);
                        }
                )
                .build();

        String createRequestBody = """
                {
                    "firstName": "Jane",
                    "lastName": "Smith",
                    "dob": "1985-06-20",
                    "gender": "FEMALE"
                }
                """;

        var createResponse = localRestClient.post()
                .uri("http://localhost:%d/api/patient".formatted(port))
                .header("Content-Type", "application/json")
                .body(createRequestBody)
                .retrieve()
                .body(String.class);

        assertThat(createResponse).isNotNull();
        assertThat(createResponse).contains("\"id\"");

        String patientId = createResponse.split("\"id\":\"")[1].split("\"")[0];

        String identifierRequestBody = """
                {
                    "idType": "EMAIL",
                    "idValue": "jane.smith@example.com"
                }
                """;

        var addIdentifierResponse = localRestClient.post()
                .uri("http://localhost:%d/api/patient/%s/identifier".formatted(port, patientId))
                .header("Content-Type", "application/json")
                .body(identifierRequestBody)
                .retrieve()
                .body(String.class);

        assertThat(addIdentifierResponse).isNotNull();
        String identifierId = addIdentifierResponse.split("\"id\":\"")[1].split("\"")[0];

        var getResponse = localRestClient.get()
                .uri("http://localhost:%d/api/patient/%s".formatted(port, patientId))
                .retrieve()
                .body(String.class);

        assertThat(getResponse).isNotNull();
        assertThat(getResponse).contains("\"idType\":\"EMAIL\"");
        assertThat(getResponse).contains("\"idValue\":\"jane.smith@example.com\"");

        localRestClient.delete()
                .uri("http://localhost:%d/api/patient/%s/identifier/%s".formatted(port, patientId, identifierId))
                .retrieve()
                .toBodilessEntity();

        localRestClient.delete()
                .uri("http://localhost:%d/api/patient/%s".formatted(port, patientId))
                .retrieve()
                .toBodilessEntity();

        var notFoundClient = RestClient.builder()
                .defaultStatusHandler(
                        status -> status.is4xxClientError(),
                        (request, response) -> {
                        }
                )
                .build();

        var getDeletedResponse = notFoundClient.get()
                .uri("http://localhost:%d/api/patient/%s".formatted(port, patientId))
                .retrieve()
                .toBodilessEntity();

        assertThat(getDeletedResponse.getStatusCode().value()).isEqualTo(404);
    }
}
