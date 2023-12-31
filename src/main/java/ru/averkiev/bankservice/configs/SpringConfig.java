package ru.averkiev.bankservice.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author mrGreenNV
 */
@Configuration
public class SpringConfig {

    /**
     * Создает Bean для преобразования DTO к модели и наоборот.
     * @return объект ModelMapper.
     */
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    /**
     * Создает Bean BCryptPasswordEncoder для кодирования информации.
     * @return новый объект BCryptPasswordEncoder.
     */
    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
