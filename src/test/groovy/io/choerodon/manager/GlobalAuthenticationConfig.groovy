package io.choerodon.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * @author superlee
 */
@Configuration
class GlobalAuthenticationConfig extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    private TestAuthenticationProvider testAuthenticationProvider

    @Override
    void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(testAuthenticationProvider)
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }
}
