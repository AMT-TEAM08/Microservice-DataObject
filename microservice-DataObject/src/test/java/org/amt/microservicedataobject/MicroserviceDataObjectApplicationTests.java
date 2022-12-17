package org.amt.microservicedataobject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MicroserviceDataObjectApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void getObjectsShouldReturnObjects() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/objects", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[\"basicImage\",\"coolMonkey/orangutan\",\"file\",\"frog\\\\frog\",\"testpng\",\"testpngpng\"]", response.getBody());
    }

    @Test
    public void postObjectWithFileShouldReturnOk() throws Exception {
        // Given
        String url = "http://localhost:" + port + "/objects";
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());

        // When
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // Then
        result.andExpect(status().isOk());
    }

    @Test
    public void postObjectWithoutFileShouldReturnUnsupportedMediaType() {
        // Given
        String url = "http://localhost:" + port + "/objects";

        // When
        ResponseEntity<String> response = this.restTemplate.postForEntity(url, null, String.class);

        // Then
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }

    @Test
    public void postObjectWithFileShouldAddObject() throws Exception {
        // Given
        String url = "http://localhost:" + port + "/objects";
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());

        // When
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart(url).file(file));

        // Then
        result.andExpect(status().isOk());
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/objects", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody() && Objects.requireNonNull(response.getBody()).contains("file"));
    }
}
