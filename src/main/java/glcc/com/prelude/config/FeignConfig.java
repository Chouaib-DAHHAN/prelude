package glcc.com.prelude.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "glcc.com.prelude.service.client")
public class FeignConfig {
}