package org.amt.microservicedataobject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MicroserviceDataObjectApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    private static final String FILE_NAME = "ZOIMjQo0nBMHIcjDfdQL";
    private static final String FILE_PARAM_NAME = "file";
    private static final byte[] FILE_CONTENT = "This is a test file".getBytes();
    private static final String FILE_CONTENT_TYPE = "text/plain";


    @Test
    public void getObjectsShouldReturnObjects() throws Exception {
        // Given
        String url = getBaseUrl() + "/objects";
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM_NAME, FILE_NAME, FILE_CONTENT_TYPE, FILE_CONTENT);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertTrue(Objects.requireNonNull(response.getBody()).contains(FILE_NAME));
    }

    @Test
    public void getObjectsShouldReturnOk() throws Exception {
        // Given
        String url = getBaseUrl() + "/objects";
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM_NAME, FILE_NAME, FILE_CONTENT_TYPE, FILE_CONTENT);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getObjectShouldReturnURL() throws Exception {
        // Given
        String url = getBaseUrl() + "/objects";
        String duration = "1";
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM_NAME, FILE_NAME, FILE_CONTENT_TYPE, FILE_CONTENT);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url + "/" + FILE_NAME + "?duration=" + duration, String.class);

        // Then
        assertTrue(Objects.requireNonNull(response.getBody()).contains("http"));
    }

    @Test
    public void getObjectShouldReturnOk() throws Exception {
        // Given
        String url = getBaseUrl() + "/objects";
        String duration = "1";
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM_NAME, FILE_NAME, FILE_CONTENT_TYPE, FILE_CONTENT);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url + "/" + FILE_NAME + "?duration=" + duration, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getObjectShouldReturnNotFound() {
        // Given
        String url = getBaseUrl() + "/objects";
        String notExistingFile = "MXEV1BN39ZFD9MBZC98H";
        String duration = "1";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url + "/" + notExistingFile + "?duration=" + duration, String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getObjectWithoutDurationShouldReturnBadRequest() {
        // Given
        String url = getBaseUrl() + "/objects";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url + "/" + FILE_NAME, String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
/*
    @Test
    public void getObjectWithInvalidDurationShouldReturnBadRequest() {
        // Given
        String url = getBaseUrl() + "/objects";
        String invalidDuration = "invalid";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url + "/" + FILE_NAME + "?duration=" + invalidDuration, String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
*/
    @Test
    public void getObjectWithNegativeDurationShouldReturnBadRequest() {
        // Given
        String url = getBaseUrl() + "/objects";
        String negativeDuration = "-1";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url + "/" + FILE_NAME + "?duration=" + negativeDuration, String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void postObjectWithFileShouldReturnOk() throws Exception {
        // Given
        String url = getBaseUrl() + "/objects";
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM_NAME, FILE_NAME, FILE_CONTENT_TYPE, FILE_CONTENT);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // When
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // Then
        result.andExpect(status().isOk());
    }

    @Test
    public void postObjectWithFileShouldAddObject() throws Exception {
        // Given
        String url = getBaseUrl() + "/objects";
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM_NAME, FILE_NAME, FILE_CONTENT_TYPE, FILE_CONTENT);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // When
        mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // Then
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/objects", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody() && Objects.requireNonNull(response.getBody()).contains(FILE_NAME));
    }

    @Test
    public void postObjectWithoutFileShouldReturnBadRequest() throws Exception {
        // Given
        String url = getBaseUrl() + "/objects";
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.multipart(url));

        // Then
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void postObjectWithoutMultipartRequestShouldReturnUnsupportedMediaType() {
        // Given
        String url = getBaseUrl() + "/objects";

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        // Then
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }

    @Test
    public void deleteObjectShouldReturnNoContent() throws Exception {
        // Given
        String url = getBaseUrl() + "/objects";
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM_NAME, FILE_NAME, FILE_CONTENT_TYPE, FILE_CONTENT);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // When
        ResponseEntity<String> response = restTemplate.exchange(url + "/" + FILE_NAME, HttpMethod.DELETE, null, String.class);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void deleteObjectShouldDeleteObject() throws Exception {
        // Given
        String url = getBaseUrl() + "/objects";
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM_NAME, FILE_NAME, FILE_CONTENT_TYPE, FILE_CONTENT);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // When
        restTemplate.exchange(url + "/" + FILE_NAME, HttpMethod.DELETE, null, String.class);

        // Then
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody() && !Objects.requireNonNull(response.getBody()).contains(FILE_NAME));
    }

    @Test
    public void deleteObjectShouldReturnNotFound() {
        // Given
        String url = getBaseUrl() + "/objects";
        String notExistingFile = "MXEV1BN39ZFD9MBZC98H";

        // When
        ResponseEntity<String> response = restTemplate.exchange(url + "/" + notExistingFile, HttpMethod.DELETE, null, String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
