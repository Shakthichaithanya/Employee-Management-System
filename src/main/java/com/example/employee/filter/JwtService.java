package com.example.employee.filter;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.client.RestTemplate;

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
//jwt verfication for authorization server
	public String getKidFromJwt(String jwt) {
		try {
			// Split the JWT into its three parts: header, payload, and signature
			String[] jwtParts = jwt.split("\\.");

			if (jwtParts.length != 3) {
				throw new IllegalArgumentException("Invalid JWT format. JWT must have three parts.");
			}

			// The first part of the JWT is the header (Base64Url encoded)
			String header = jwtParts[0];

			// Decode the header from Base64Url encoding
			String decodedHeader = new String(Base64.getUrlDecoder().decode(header));

			// Use Jackson ObjectMapper to parse the JSON header
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode headerJson = objectMapper.readTree(decodedHeader);

			// Extract the 'kid' value from the header
			String kid = headerJson.get("kid").asText();
			System.out.println(kid);
			return kid;
		} catch (Exception e) {
			throw new RuntimeException("Error extracting 'kid' from JWT", e);
		}
	}

	public PublicKey getPublicKey(String jwt) {
		try {


			String kid = getKidFromJwt(jwt);


			// Fetch JWKS from the endpoint
			RestTemplate restTemplate = new RestTemplate();
			String jwksResponse = restTemplate.getForObject("http://localhost:9000/oauth2/jwks", String.class);
			ObjectMapper objectMapper = new ObjectMapper();
			// Parse JWKS JSON
			JsonNode jwksJson = objectMapper.readTree(jwksResponse);
			JsonNode keys = jwksJson.get("keys");

			// Find the key with the matching `kid`
			for (JsonNode key : keys) {
				if (kid.equals(key.get("kid").asText())) {
					// Extract RSA key parameters
					String n = key.get("n").asText(); // Modulus
					String e = key.get("e").asText(); // Exponent

					// Decode and construct the public key
					byte[] modulusBytes = Base64.getUrlDecoder().decode(n);
					byte[] exponentBytes = Base64.getUrlDecoder().decode(e);
					BigInteger modulus = new BigInteger(1, modulusBytes);
					BigInteger exponent = new BigInteger(1, exponentBytes);

					RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					return keyFactory.generatePublic(spec);
				}
			}
			throw new RuntimeException("Public key with the specified kid not found");
		} catch (Exception e) {
			throw new RuntimeException("Error fetching public key from JWKS", e);
		}

	}

	public boolean verifyJwtSignature(String jwt, PublicKey publicKey) {
		try {
			// Parse JWT and verify signature using the public key
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(publicKey) // Use public key for signature verification
					.build()
					.parseClaimsJws(jwt)    // Parse JWT
					.getBody();             // Get the body (claims)

			// If parsing is successful, the signature is valid
			return true;
		} catch (Exception e) {
			// Invalid signature or other parsing errors
			System.out.println("Invalid JWT signature: " + e.getMessage());
			return false;
		}
	}

	public Map<String, Object> getClaimsFromJwt(String jwt) {
		try {
			// Split the JWT into its three parts: header, payload, and signature
			String[] jwtParts = jwt.split("\\.");

			if (jwtParts.length != 3) {
				throw new IllegalArgumentException("Invalid JWT format. JWT must have three parts.");
			}

			// The second part of the JWT is the payload (Base64Url encoded)
			String payload = jwtParts[1];

			// Decode the payload from Base64Url encoding
			String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));

			// Use Jackson ObjectMapper to parse the JSON payload
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode payloadJson = objectMapper.readTree(decodedPayload);

			// Print the claims in the payload
			System.out.println("Claims: " + payloadJson);

			// If you need the claims as a Map
			return objectMapper.convertValue(payloadJson, Map.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}