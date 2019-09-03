package com.litoos11.bolsaideas.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.litoos11.bolsaideas.app.component.LoginSuccessHandler;
import com.litoos11.bolsaideas.app.service.impl.UserDetailServiceImpl;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	// @Bean
	// public BCryptPasswordEncoder passwordEncoder() {
	// return new BCryptPasswordEncoder();
	// }

	@Autowired
	@Qualifier("loginSuccessComponent")
	private LoginSuccessHandler successHandler;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private DataSource dataSource;

	@Autowired
	@Qualifier("userDetailServiceImpl")
	private UserDetailServiceImpl userDetailService;

	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {

		/* ##COMENTADO PARA OCUPAR JDBC## */
		// PasswordEncoder encoder = this.passwordEncoder;
		//
		// UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		//
		// builder.inMemoryAuthentication().withUser(users.username("admin").password("12345").roles("ADMIN",
		// "USER"))
		// .withUser(users.username("litoos11").password("nomelase").roles("USER"));

		/* ##COMENTADO PARA OCUPAR hiberante y JPA## */
		/*
		 * builder.jdbcAuthentication().dataSource(dataSource).passwordEncoder(
		 * passwordEncoder)
		 * .usersByUsernameQuery("SELECT usuario, contrasenia, enabled FROM USUARIOS WHERE usuario = ?"
		 * ) .authoritiesByUsernameQuery(
		 * "SELECT u.usuario, r.rol FROM roles r INNER JOIN usuarios u ON r.id_usuario = u.id WHERE u.usuario = ?"
		 * );
		 */

		builder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.authorizeRequests().antMatchers("/cliente/", "/css/**", "/js/**", "/img/**", "/cliente/list").permitAll()
				/* ##COMENTADO PARA AGREGAR SEGURIDAD POR ANOTACIONES## */
				// .antMatchers("/cliente/ver/**").hasAnyRole("USER")
				.antMatchers("/uploads/**").hasAnyRole("USER")
				// .antMatchers("/cliente/form/**").hasAnyRole("ADMIN")
				// .antMatchers("/cliente/delete/**").hasAnyRole("ADMIN")
				// .antMatchers("/factura/**").hasAnyRole("ADMIN")
				.anyRequest().authenticated().and().formLogin().successHandler(successHandler).loginPage("/login")
				.permitAll().defaultSuccessUrl("/cliente/").and().logout().permitAll().and().exceptionHandling()
				.accessDeniedPage("/error_403");
	}

}
