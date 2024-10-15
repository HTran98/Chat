package com.codreal.chatservice.util;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JwtTokenUtil {
    private final String SECRET = "your_secret_key"; // THAY BẰNG KHÓA BẢO MẬT CỦA BẠN
    private final long EXPIRATION_TIME = 3600000; // 1 GIỜ

    public String generateToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }
}
