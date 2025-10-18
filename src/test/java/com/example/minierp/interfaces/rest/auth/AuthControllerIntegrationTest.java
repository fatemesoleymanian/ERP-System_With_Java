package com.example.minierp.interfaces.rest.auth;

import com.example.minierp.domain.user.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void register_ShouldReturn201() throws Exception {
        String body = """
            {
                "username": "testuser",
                "password": "12345678",
                "role": "ADMIN"
            }
            """;

        mockMvc.perform(post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void register_ShouldReturn201_WhenCalledByAdmin() throws Exception {
        // مرحله ۱: ثبت‌نام ادمین اولیه
        String adminBody = """
        {
            "username": "adminuser",
            "password": "adminpass",
            "role": "ADMIN"
        }
        """;

        mockMvc.perform(post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminBody))
                .andExpect(status().isCreated());

        // مرحله ۲: لاگین ادمین برای گرفتن توکن
        String loginBody = """
        {
            "username": "adminuser",
            "password": "adminpass"
        }
        """;

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        String adminToken = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.data.token");

        // مرحله ۳: ارسال درخواست register با Authorization header
        String newUserBody = """
        {
            "username": "testuser",
            "password": "12345678",
            "role": "SALES"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.role").value("SALES"));
    }

    @Test
    void login_ShouldReturn200() throws Exception {
        // اول ثبت‌نام
        String reg = """
            {
                "username": "loginuser",
                "password": "pass1234",
                "role": "SALES"
            }
            """;

        mockMvc.perform(post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reg))
                .andExpect(status().isCreated());

        // حالا لاگین
        String login = """
            {
                "username": "loginuser",
                "password": "pass1234"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.role").value("SALES"));
    }

    @Test
    void refreshToken_ShouldReturnNewToken() throws Exception {
        // ثبت‌نام
        String reg = """
        {
            "username": "refuser",
            "password": "pass1234",
            "role": "ADMIN"
        }
        """;

        var result = mockMvc.perform(post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reg))
                .andExpect(status().isCreated())
                .andReturn();

        String oldToken = JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");

        // حالا refresh
        mockMvc.perform(post("/api/auth/refresh-token")
                        .header("Authorization", "Bearer " + oldToken)
                        .param("token", oldToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void forgotPassword_ShouldReturnOk() throws Exception {
        // ثبت‌نام
        String reg = """
        {
            "username": "forgetuser",
            "password": "pass1234",
            "role": "SALES"
        }
        """;

        mockMvc.perform(post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reg))
                .andExpect(status().isCreated());

        // forgot-password
        mockMvc.perform(post("/api/auth/forgot-password")
                        .param("username", "forgetuser"))
                .andExpect(status().isOk());
    }

}
