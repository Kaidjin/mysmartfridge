package com.mysmartfridge.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;

import com.mysmartfridge.security.AuthoritiesConstants;
import com.mysmartfridge.security.ClientStatelessCsrfFilter;
import com.mysmartfridge.security.Http401UnauthorizedEntryPoint;
import com.mysmartfridge.security.RestAuthenticationFailureHandler;
import com.mysmartfridge.security.RestAuthenticationSuccessHandler;
import com.mysmartfridge.security.RestLogoutSuccessHandler;
import com.mysmartfridge.security.UserDetailsService;


@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint;
	
	@Autowired
	private RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
	
	@Autowired
	private RestAuthenticationFailureHandler restAuthenticationFailureHandler;
	
	@Autowired
	private RestLogoutSuccessHandler restLogoutSuccessHandler;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/fonts/**")
			.antMatchers("/js/**.js")
			.antMatchers("/img/**")
			.antMatchers("/css/**.css")
			.antMatchers("/partials/**.html")
			.antMatchers("/console/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
	    	.csrf()
	    	.disable()
	    	.addFilterBefore(new ClientStatelessCsrfFilter(), CsrfFilter.class)
	    	.exceptionHandling()
	    	.authenticationEntryPoint(http401UnauthorizedEntryPoint)
	    .and()
	    	.formLogin()
	    	.loginProcessingUrl("/api/login")
	    	.successHandler(restAuthenticationSuccessHandler)
	    	.failureHandler(restAuthenticationFailureHandler)
	    	.usernameParameter("login")
	    	.passwordParameter("password")
	    	.permitAll()
	    .and()
	    	.logout()
	    	.logoutUrl("/api/logout")
	    	.logoutSuccessHandler(restLogoutSuccessHandler)
	    	.deleteCookies("JSESSIONID", ClientStatelessCsrfFilter.CSRF_COOKIE_NAME)
	    	.permitAll()
	    .and()
	        .headers()
	        .frameOptions()
	        .disable()
	    .and()
	        .authorizeRequests()
	        .antMatchers(HttpMethod.POST, "/api/recipes").authenticated()
	        .antMatchers(HttpMethod.GET, "/api/recipes").permitAll()
	        .antMatchers("/api/users/current/**").permitAll()
	        .antMatchers("/manage/**").hasAuthority(AuthoritiesConstants.ADMIN);
	}
	
}
