package com.kaoyan.common.util;

import com.kaoyan.common.exception.BizException;
import com.kaoyan.common.result.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLES = "roles";

    @Value("${kaoyan.jwt.secret}")
    private String secret;

    @Value("${kaoyan.jwt.expire-minutes:720}")
    private long expireMinutes;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username, List<String> roles) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + expireMinutes * 60 * 1000);
        return Jwts.builder()
                .claims(Map.of(
                        CLAIM_USER_ID, userId,
                        CLAIM_USERNAME, username,
                        CLAIM_ROLES, roles
                ))
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expire)
                .signWith(key())
                .compact();
    }

    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.debug("JWT 解析失败: {}", e.getMessage());
            throw BizException.of(ResultCode.UNAUTHORIZED);
        }
    }

    public Long getUserId(Claims claims) {
        Object v = claims.get(CLAIM_USER_ID);
        return v == null ? null : Long.valueOf(v.toString());
    }

    public String getUsername(Claims claims) {
        Object v = claims.get(CLAIM_USERNAME);
        return v == null ? null : v.toString();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        Object v = claims.get(CLAIM_ROLES);
        return v instanceof List ? (List<String>) v : List.of();
    }

    public long getExpireMinutes() {
        return expireMinutes;
    }
}
