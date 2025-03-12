package com.example.autopark.autopark.service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Сервис для работы с JWT-токенами.
 * Предоставляет методы для генерации, валидации и извлечения данных из токенов.
 */
@Service
public class JwtService {

    // Секретный ключ для подписи токенов
    private final String SECRET_KEY = "mySuperSecureKeyWithMoreThan32Characters123!";

    /**
     * Генерирует JWT-токен для указанного пользователя.
     *
     * @param username Имя пользователя
     * @param role     Роль пользователя
     * @return Сгенерированный JWT-токен
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 день
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Проверяет валидность JWT-токена.
     *
     * @param token JWT-токен для проверки
     * @return true, если токен валиден и не истек срок его действия, иначе false
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Извлекает все данные (claims) из JWT-токена.
     *
     * @param token JWT-токен
     * @return Объект Claims, содержащий данные токена
     * @throws IllegalArgumentException Если токен некорректен
     */
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Некорректный токен");
        }
    }
}