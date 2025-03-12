package com.example.autopark.autopark.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности приложения.
 * Определяет настройки для защиты HTTP-запросов и управления доступом.
 */
@Configuration
public class SecurityConfig {

    /**
     * Создает цепочку фильтров безопасности (SecurityFilterChain) для настройки правил доступа.
     *
     * @param http Объект HttpSecurity для конфигурирования безопасности
     * @return Настроенная цепочка фильтров безопасности
     * @throws Exception Возможные исключения при настройке безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Отключаем защиту CSRF (Cross-Site Request Forgery)
        // Разрешаем все запросы без аутентификации
        http.csrf().disable().authorizeRequests().anyRequest().permitAll();

        return http.build();
    }
}