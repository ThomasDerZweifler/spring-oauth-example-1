package com.wetjens.powergrid.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableSocial
class SpringBootExampleApplication {

    @Configuration
    @EnableAuthorizationServer
    class OAuth2ServerConfiguration : WebSecurityConfigurerAdapter() {

        override fun configure(http: HttpSecurity) {
            http
                    .csrf().disable()
                    .antMatcher("/").authorizeRequests().anyRequest().permitAll()
                    .and()
                    .antMatcher("/signin/**").authorizeRequests().anyRequest().permitAll()
        }

        override fun configure(auth: AuthenticationManagerBuilder) {
            auth.inMemoryAuthentication()
                    .withUser("tom")
                    .password("tom")
                    .roles("ADMIN", "USER")
        }

        // Must be declared here as @Bean to be able to configure the AuthenticationManager
        @Bean
        override fun authenticationManager(): AuthenticationManager {
            return super.authenticationManager()
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableWebSecurity
    class OAuth2ResourceConfiguration : ResourceServerConfigurerAdapter() {

        override fun configure(http: HttpSecurity) {
            // Because we are both resource and authorization server,
            // we must always define a specific matcher on which authorizeRequests() is called
            // else the resource server filter will also protect any authorization server endpoints
            http.antMatcher("/api/**")
                    .authorizeRequests()
                    .anyRequest().authenticated()
        }

    }

}

fun main(args: Array<String>) {
    SpringApplication.run(SpringBootExampleApplication::class.java, *args)
}