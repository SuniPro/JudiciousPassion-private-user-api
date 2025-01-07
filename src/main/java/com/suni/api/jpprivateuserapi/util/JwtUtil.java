package com.suni.api.jpprivateuserapi.util;

import com.suni.api.jpprivateuserapi.dto.CustomUserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@PropertySource("classpath:application-private.properties")
@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration_time}") long accessTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    /*
     * Access Token 생성
     * @param customUserDto
     * @return Access Token String
     */
    public String createAccessToken(CustomUserDto customUserDto) {
        return createToken(customUserDto, accessTokenExpTime);
    }

    /**
     * JWT 생성
     *
     * @param customUserDto : 로직 내부에서 인증정보를 저장해둘 dto 입니다.
     * @param expireTime : 토큰의 만료시간입니다.
     * @return JWT String
     */
    private String createToken(CustomUserDto customUserDto, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("username", customUserDto.getUsername());
        claims.put("email", customUserDto.getEmail());
        claims.put("role", customUserDto.getRoleType());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Token 에서 User ID 추출
     *
     * @param token : 발급된 토큰입니다.
     * @return User ID
     */
    public String getUserEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public String getUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    /**
     * JWT 검증
     *
     * @param token : 발급된 토큰입니다.
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    /**
     * JWT Claims 추출
     *
     * @param accessToken : 발급된 엑세스토큰입니다.
     * @return JWT Claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
