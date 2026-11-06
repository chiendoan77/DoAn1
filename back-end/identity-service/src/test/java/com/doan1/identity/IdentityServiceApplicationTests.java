package com.doan1.identity;

import com.doan1.identity.repository.AccountRepository;
import com.doan1.identity.repository.AccountStatusHistoryRepository;
import com.doan1.identity.entity.Account;
import com.doan1.identity.enums.Role;
import com.doan1.security.test.JwtTestKeys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

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
        "spring.flyway.enabled=false",
        "service.api-keys.profile=profile-internal-key-for-identity-tests"
})
@AutoConfigureMockMvc
class IdentityServiceApplicationTests {
    @DynamicPropertySource
    static void jwtKeys(DynamicPropertyRegistry registry) {
        JwtTestKeys.registerKeyPair(registry);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accounts;

    @Autowired
    private AccountStatusHistoryRepository statusHistory;

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

    @Test
    void adminCanSuspendAccountWithAuditAndInternalStatusIsProtected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"suspend@example.com","phoneNumber":"+84902223344",
                                 "username":"suspend_user","fullName":"Suspended","password":"StrongPass123",
                                 "role":"PROVIDER"}
                                """))
                .andExpect(status().isOk());
        var account = accounts.findByEmailIgnoreCase("suspend@example.com").orElseThrow();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(
                        "/api/admin/users/" + account.getId() + "/status")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"SUSPENDED","reason":"Yêu cầu kiểm tra tài khoản"}
                                """))
                .andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertEquals(1, statusHistory.count());

        mockMvc.perform(get("/internal/accounts/" + account.getId() + "/status")
                        .header("X-Service-Api-Key", "profile-internal-key-for-identity-tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUSPENDED")));

        mockMvc.perform(get("/internal/accounts/" + account.getId() + "/status"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rotatesRefreshTokensAndRevokesTheEntireFamilyWhenAnOldTokenIsReused() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"rotation@example.com","phoneNumber":"+84902223355",
                                 "username":"rotation_user","fullName":"Rotation","password":"StrongPass123",
                                 "role":"CUSTOMER"}
                                """))
                .andExpect(status().isOk());

        String login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifier":"rotation@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        String originalRefresh = login.replaceAll("(?s).*\"refreshToken\":\"([^\"]+)\".*", "$1");

        String rotation = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + originalRefresh + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String rotatedRefresh = rotation.replaceAll("(?s).*\"refreshToken\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + originalRefresh + "\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + rotatedRefresh + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutDeletesAdminAndInvalidatesAllOfTheirRefreshSessions() throws Exception {
        String identity = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String email = "admin-" + identity + "@example.com";
        String password = "StrongPass123";
        accounts.save(new Account(email, "+8490" + identity.substring(0, 7), "admin_" + identity,
                "Admin", new BCryptPasswordEncoder().encode(password), Role.ADMIN));
        String otherAdminIdentity = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Account otherAdmin = accounts.save(new Account("other-admin-" + otherAdminIdentity + "@example.com",
                "+8490" + otherAdminIdentity.substring(0, 7), "admin_" + otherAdminIdentity,
                "Other Admin", "unused-hash", Role.ADMIN));

        String firstLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String firstRefresh = firstLogin.replaceAll("(?s).*\"refreshToken\":\"([^\"]+)\".*", "$1");
        String secondLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String secondRefresh = secondLogin.replaceAll("(?s).*\"refreshToken\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + firstRefresh + "\"}"))
                .andExpect(status().isOk());

        org.junit.jupiter.api.Assertions.assertTrue(accounts.findByEmailIgnoreCase(email).isEmpty());
        org.junit.jupiter.api.Assertions.assertTrue(accounts.findById(otherAdmin.getId()).isPresent());
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + secondRefresh + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutWithUnknownRefreshTokenDoesNotDeleteAdmin() throws Exception {
        String identity = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String email = "admin-" + identity + "@example.com";
        Account admin = accounts.save(new Account(email, "+8490" + identity.substring(0, 7),
                "admin_" + identity, "Admin", "unused-hash", Role.ADMIN));
        String unknownToken = Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[32]);

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + unknownToken + "\"}"))
                .andExpect(status().isOk());

        org.junit.jupiter.api.Assertions.assertTrue(accounts.findById(admin.getId()).isPresent());
    }

    @Test
    void logoutRevokesCustomerSessionWithoutDeletingTheAccount() throws Exception {
        String identity = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String email = "customer-" + identity + "@example.com";
        String password = "StrongPass123";
        Account customer = accounts.save(new Account(email, "+8490" + identity.substring(0, 7),
                "customer_" + identity, "Customer", new BCryptPasswordEncoder().encode(password), Role.CUSTOMER));
        String login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String refreshToken = login.replaceAll("(?s).*\"refreshToken\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk());

        org.junit.jupiter.api.Assertions.assertTrue(accounts.findById(customer.getId()).isPresent());
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    private static String token(UUID id, String role) {
        return JwtTestKeys.token(id, role, "Admin");
    }
}
