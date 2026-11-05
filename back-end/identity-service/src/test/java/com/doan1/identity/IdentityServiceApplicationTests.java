package com.doan1.identity;

import com.doan1.identity.repository.AccountRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:identity;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret=0123456789abcdef0123456789abcdef"
})
@AutoConfigureMockMvc
class IdentityServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accounts;

    @Test
    void registersAndAuthenticatesWithEmailAndPhoneWithoutReturningPasswordHash() throws Exception {
        String registration = """
                {
                  "email": "customer@example.com",
                  "phoneNumber": "+84901234567",
                  "username": "customer_1",
                  "fullName": "Khách hàng",
                  "password": "StrongPass123",
                  "role": "CUSTOMER"
                }
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("CUSTOMER")))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        org.junit.jupiter.api.Assertions.assertNotEquals(
                "StrongPass123",
                accounts.findByEmailIgnoreCase("customer@example.com").orElseThrow().getPasswordHash());

        String login = """
                {"identifier":"customer@example.com","password":"StrongPass123"}
                """;
        String token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.phoneNumber", is("+84901234567")))
                .andExpect(jsonPath("$.account.passwordHash").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll("(?s).*\"accessToken\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("customer_1")));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifier":"+84901234567","password":"StrongPass123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.email", is("customer@example.com")));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifier":"+84901234567","password":"WrongPassword"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsDuplicateIdentifiersAndAdminSelfRegistration() throws Exception {
        String registration = """
                {
                  "email": "provider@example.com",
                  "phoneNumber": "+84909876543",
                  "username": "provider_1",
                  "fullName": "Nhà cung cấp",
                  "password": "StrongPass123",
                  "role": "PROVIDER"
                }
                """;
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration.replace("\"PROVIDER\"", "\"ADMIN\"")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsFieldSpecificMessagesWhenRegistrationValidationFails() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "phoneNumber": "0123",
                                  "username": "x",
                                  "fullName": "",
                                  "password": "short",
                                  "role": "CUSTOMER"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Vui lòng kiểm tra lại thông tin đã nhập.")))
                .andExpect(jsonPath("$.errors.email", is("Email không đúng định dạng.")))
                .andExpect(jsonPath("$.errors.phoneNumber", is("Số điện thoại không đúng định dạng.")))
                .andExpect(jsonPath("$.errors.username", is("Tên đăng nhập phải có 3-40 ký tự gồm chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.")))
                .andExpect(jsonPath("$.errors.fullName", is("Họ và tên không được để trống.")))
                .andExpect(jsonPath("$.errors.password", is("Mật khẩu phải có từ 8 đến 128 ký tự.")));
    }

    @Test
    void explainsWhenPasswordExceedsBcryptByteLimit() throws Exception {
        String request = """
                {
                  "email": "byte-limit@example.com",
                  "phoneNumber": "+84901112233",
                  "username": "byte_limit_user",
                  "fullName": "Byte Limit",
                  "password": "%s",
                  "role": "CUSTOMER"
                }
                """.formatted("ă".repeat(40));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Mật khẩu không được vượt quá 72 byte.")));
    }
}
