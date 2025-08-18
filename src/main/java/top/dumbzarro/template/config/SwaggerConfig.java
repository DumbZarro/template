package top.dumbzarro.template.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration // https://springdoc.org/#is-graalvm-supported
@OpenAPIDefinition(info = @Info(title = "My App", description = "description", version = "v1.0"))
public class SwaggerConfig {
}