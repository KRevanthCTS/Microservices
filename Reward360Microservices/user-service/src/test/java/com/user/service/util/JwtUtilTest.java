package com.user.service.util;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.user.service.filter.UserJwtFilter;

import jakarta.servlet.FilterChain;

public class JwtUtilTest {

    @Test
    public void generateAndParseToken() throws Exception {
        JwtUtil jwtUtil = new JwtUtil();

        // set private fields via reflection
        Field secretField = JwtUtil.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        // use a long secret to satisfy key length
        secretField.set(jwtUtil, "this-is-a-test-secret-that-is-long-enough-12345");

        Field expField = JwtUtil.class.getDeclaredField("jwtExpirationMs");
        expField.setAccessible(true);
        expField.setLong(jwtUtil, 1000L * 60 * 60);

        String token = jwtUtil.generateToken(Map.of("userId", 42L), "test@example.com");
        assertNotNull(token);

        var claims = jwtUtil.parseClaims(token);
        assertEquals("test@example.com", claims.getSubject());
        assertEquals(42, ((Number)claims.get("userId")).longValue());
    }

    @Test
    public void userJwtFilter_allowsAndBlocks() throws Exception {
        JwtUtil jwtUtil = new JwtUtil();
        Field secretField = JwtUtil.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "this-is-a-test-secret-that-is-long-enough-12345");

        Field expField = JwtUtil.class.getDeclaredField("jwtExpirationMs");
        expField.setAccessible(true);
        expField.setLong(jwtUtil, 1000L * 60 * 60);

        String token = jwtUtil.generateToken(Map.of("userId", 7L), "user@test.com");

        UserJwtFilter filter = new UserJwtFilter();
        // inject jwtUtil
        Field utilField = UserJwtFilter.class.getDeclaredField("jwtUtil");
        utilField.setAccessible(true);
        utilField.set(filter, jwtUtil);

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();

        // case: missing header -> 401
        req.setRequestURI("/user/me");
        filter.doFilter(req, resp, (request, response) -> {});
        assertEquals(401, resp.getStatus());

        // case: valid token
        MockHttpServletRequest req2 = new MockHttpServletRequest();
        MockHttpServletResponse resp2 = new MockHttpServletResponse();
        req2.setRequestURI("/user/me");
        req2.addHeader("Authorization", "Bearer " + token);

        final boolean[] invoked = {false};
        FilterChain chain = (request, response) -> {
            invoked[0] = true;
            ((MockHttpServletResponse) response).setStatus(200);
        };

        filter.doFilter(req2, resp2, chain);
        assertTrue(invoked[0]);
        assertEquals(200, resp2.getStatus());
    }
}
