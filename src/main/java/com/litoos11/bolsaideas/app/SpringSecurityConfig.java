package com.litoos11.bolsaideas.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.litoos11.bolsaideas.app.component.LoginSuccessHandler;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	@Qualifier("loginSuccessComponent")
	private LoginSuccessHandler successHandler;
		
	@Autowired 
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception{
		
		PasswordEncoder encoder = passwordEncoder();
		
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		
		builder.inMemoryAuthentication()
		.withUser(users.username("admin").password("12345").roles("ADMIN", "USER"))
		.withUser(users.username("litoos11").password("nomelase").roles("USER"));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.authorizeRequests()
			.antMatchers("/cliente/", "/css/**", "/js/**", "/img/**", "/cliente/list").permitAll()
			.antMatchers("/cliente/ver/**").hasAnyRole("USER")
			.antMatchers("/uploads/**").hasAnyRole("USER")
			.antMatchers("/cliente/form/**").hasAnyRole("ADMIN")
			.antMatchers("/cliente/delete/**").hasAnyRole("ADMIN")
			.antMatchers("/factura/**").hasAnyRole("ADMIN")
			.anyRequest().authenticated()
			.and()
				.formLogin()
					.successHandler(successHandler)
					.loginPage("/login")
				.permitAll()
				.defaultSuccessUrl("/cliente/")
			.and()
				.logout().permitAll()
			.and()
				.exceptionHandling()
				.accessDeniedPage("/error_403");
	}
	
	

}
