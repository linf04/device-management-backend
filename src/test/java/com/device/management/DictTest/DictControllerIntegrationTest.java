
package com.device.management.DictTest;
import com.device.management.DeviceManagementApplication;
import com.device.management.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DeviceManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DictControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetDictItemsByTypeCode_Integration() {
        String url = "http://localhost:" + port + "/api/dict/items?dictTypeCode=USER_TYPE";
        
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}
