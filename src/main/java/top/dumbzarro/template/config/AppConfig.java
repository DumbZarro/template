package top.dumbzarro.template.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Data
@Configuration
public class AppConfig {

//    @Value("#'${user.list:[123,245]}'.spilt(',')")
//    private List<String> testList;

    @Value("${app.jwt.expirationMs}")
    private long jwtExpirationMs;
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    @Value("${app.jwt.jwkUri}")
    private String jwkUri;
    @Value("classpath:keys/private-key.pem")
    Resource privateKeyRes;
    @Value("classpath:keys/public-key.pem")
    Resource publicKeyRes;

    @Value("${app.role.defaultId:123}")
    private Long defaultRoleId;

    @Value("${spring.mail.username}")
    private String mail;


}
