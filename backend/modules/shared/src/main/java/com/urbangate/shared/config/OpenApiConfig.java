// Copyright (c) UrbanGate
package com.urbangate.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

  private static final String SECURITY_SCHEME_NAME = "Keycloak";

  @Bean
  public OpenAPI openAPI() {

    return new OpenAPI()
        .info(
            new Info()
                .title("IAM API — Powered by Keycloak")
                .description(
                    """
                    Identity and Access Management API.

                    **Authentication:** Use `/api/v1/auth/login` to obtain a JWT access token.
                    Then click **Authorize** and enter: `Bearer <your_token>`

                    **Roles:**
                    - `ADMIN` — full access
                    - `MANAGER` — manage users and view reports
                    - `RESIDENT` — standard authenticated access
                    """)
                .version("1.0.0")
                .contact(new Contact().name("UrbanGate Team").email("urbangate@gmail.com")))
        .components(
            new Components()
                .addSecuritySchemes(
                    SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(
                            new OAuthFlows()
                                .password(
                                    new OAuthFlow()
                                        .tokenUrl("/api/v1/auth" + "/login")
                                        .refreshUrl("/api/v1/auth/" + "/refresh")
                                        .scopes(
                                            new Scopes()
                                                .addString("openid", "OpenID Connect")
                                                .addString("profile", "Profile info")
                                                .addString("email", "Email address"))))))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
  }
}
