package com.estateresource.estatemanger.security.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SecurityApplicationConfigs {

    @Value("${jwt.sign-secrets}")
    private String jwtSignSecrets;
}
