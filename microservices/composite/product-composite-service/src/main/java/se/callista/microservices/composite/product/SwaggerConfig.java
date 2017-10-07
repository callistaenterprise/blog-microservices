package se.callista.microservices.composite.product;

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.security.Principal;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static final String API_VERSION = "v1.0.0-M1";
    public static final String securitySchemaOAuth2 = "oauth2schema";
    public static final String SCOPE_OPENID = "openid";
    public static final String SCOPE_EMAIL = "email";
    public static final String SCOPE_PROFILE = "profile";
    public static final String SCOPE_OPENID_DESCRIPTION = "Get an openid-token";
    public static final String SCOPE_EMAIL_DESCRIPTION = "Get the email-address";
    public static final String SCOPE_PROFILE_DESCRIPTION = "Get the name";

    private static final String CLIENT_ID = "812816101196-hutf4ialanlr6tndpuitb5o213ft5v50.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "etL9ZkKB5VyWR-eMESz16F4O";

    private static final String OAUTH_AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/auth";
    private static final String OAUTH_GET_ACCESS_TOKEN_URL = "https://www.googleapis.com/oauth2/v3/token";
    private static final String API_LICENSE_URL = "https://raw.githubusercontent.com/swagger-api/swagger-ui/master/LICENSE";

    private AuthorizationScope authorizationScopeOpenId = new AuthorizationScope(SCOPE_OPENID, SCOPE_OPENID_DESCRIPTION);
    private AuthorizationScope authorizationScopeEmail = new AuthorizationScope(SCOPE_EMAIL, SCOPE_EMAIL_DESCRIPTION);
    private AuthorizationScope authorizationScopeProfile = new AuthorizationScope(SCOPE_PROFILE, SCOPE_PROFILE_DESCRIPTION);

    private Predicate<String> paths = path -> path.toLowerCase().startsWith("/api/");

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceApplication.class);

    public SwaggerConfig() {
        LOG.debug("new SwaggerConfig() called!");
    }

    @Bean
    public Docket apiDocumentation() {

        LOG.debug("apiDocumentation() called!");

        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(Principal.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(paths)
                .build()
                .apiInfo(apiInfo());
    }

    @SuppressWarnings("deprecation")
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "REST API",
                "This is a description of the API...",
                API_VERSION,
                "API TOS",
                "magnus.larsson@callistaenterprise.se",
                "API License",
                API_LICENSE_URL
        );

        return apiInfo;
    }

}
