package com.example.minierp.interfaces.rest.product;

import com.example.minierp.application.auth.JwtService;
import com.example.minierp.domain.user.User;
import com.example.minierp.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private String jwt;

    @BeforeEach
    void setup() {
        User user = userRepository.findByUsername("admin@gmail.com").orElseThrow();
        jwt = jwtService.generateToken(user);
    }

    @Test
    void shouldGetProductsWithValidToken() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailWithoutToken() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isForbidden());
    }
}
