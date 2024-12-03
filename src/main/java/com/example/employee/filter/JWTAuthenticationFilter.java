package com.example.employee.filter;

import java.io.IOException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
//	private final UserDetailsService userDetailsService;

	@Autowired
	public JWTAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
//		this.userDetailsService = userDetailsService;
	}

	public static Set<SimpleGrantedAuthority> convertToGrantedAuthorities(Set<String> roles) {
		return roles.stream()
				.map(SimpleGrantedAuthority::new) // Convert each role to SimpleGrantedAuthority
				.collect(Collectors.toSet());    // Collect as a Set
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String email;
		if (authHeader == null || !authHeader.startsWith("Bearer")) {
			filterChain.doFilter(request, response);
			return;
		}
		jwt = authHeader.substring(7);

		System.out.println(jwt);
		/**
		 * below commented is for authorization jwt for general with microservice
		 */
//		email = jwtService.getEmail(jwt);
//		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//			UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
//			if (jwtService.isTokenValid(jwt, userDetails)) {
//				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//						email, null, userDetails.getAuthorities());
//				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//			}
//		}
		/**
		 * below logic is for jwt authentication from authorization server
		 */
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			PublicKey rsaPublicKey = jwtService.getPublicKey(jwt);
			if (jwtService.verifyJwtSignature(jwt, rsaPublicKey)) {

				Map<String,Object> cliams = jwtService.getClaimsFromJwt(jwt);

				String role = cliams.get("role").toString();

				Set<String> scopes = new HashSet<>();
				scopes.add(role);
				email = cliams.get("sub").toString();
				Set<SimpleGrantedAuthority> authorities = convertToGrantedAuthorities(scopes);

				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						email, null, authorities);
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}


		}

		filterChain.doFilter(request, response);
	}
}

