package com.example.minierp.interfaces.rest.reports;
import com.example.minierp.application.auth.JwtService;
import com.example.minierp.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void managerCanDownloadPdf() throws Exception {
        var user = userRepository.findByUsername("admin@gmail.com").orElseThrow();
        String jwt = jwtService.generateToken(user);

        mockMvc.perform(get("/api/reports/orders/pdf")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }
}
