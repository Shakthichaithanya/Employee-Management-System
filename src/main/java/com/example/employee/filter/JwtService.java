package com.example.employee.filter;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final String SECRETE_KEY = "4D6251655468576D5A7134743777217A25432A462D4A614E635266556A586E3272357538782F413F4428472B4B6250655367566B597033733676397924422645";

	public String generateJwtToken(UserDetails userDetails) {
		return generateJwtToken(new HashMap<>(), userDetails);
	}

	public String generateJwtToken(Map<String, Object> getClaims, UserDetails userDetails) {
		List<GrantedAuthority> roles = (List<GrantedAuthority>) userDetails.getAuthorities().stream().toList();
		String role = roles.get(0).toString();
		System.out.println(role);
		return Jwts.builder().setClaims(getClaims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 30)))
				.claim("scope", role)
				.signWith(getSignInKey(), SignatureAlgorithm.HS512).compact();
	}

	public boolean isTokenValid(String jwt, UserDetails userDetails) {

		final String email = getEmail(jwt);

		return email.equals(userDetails.getUsername()) && !isTokenExpired(jwt);

	}

	private boolean isTokenExpired(String jwt) {
		return getExpiration(jwt).before(new Date());
	}

	private Date getExpiration(String jwt) {
		return getClaim(jwt, Claims::getExpiration);
	}

	public String getEmail(String jwt) {

		return getClaim(jwt, Claims::getSubject);
	}

	public <T> T getClaim(String jwt, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaims(jwt);
		return claimsResolver.apply(claims);
	}

	public Claims getAllClaims(String jwt) {

		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(jwt).getBody();
	}

	private Key getSignInKey() {

		byte[] keyBytes = Decoders.BASE64.decode(SECRETE_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
