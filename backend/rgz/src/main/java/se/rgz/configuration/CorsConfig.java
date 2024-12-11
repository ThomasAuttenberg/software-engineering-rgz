package se.rgz.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Применяется ко всем эндпоинтам
                .allowedOrigins("http://localhost:5173") // Разрешённые источники
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешённые методы
                .allowedHeaders("*") // Разрешённые заголовки
                .allowCredentials(true) // Разрешить отправку учетных данных
                .maxAge(3600); // Время кэширования ответа CORS в секундах
    }
}
