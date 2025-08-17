package br.com.famper.events.web.auth;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtEncoder jwtEncoder;
  private final long expiration;

  public AuthController(
      AuthenticationManager authManager,
      JwtEncoder jwtEncoder,
      @Value("${app.security.jwt.expiration-seconds:3600}") long expiration) {
    this.authManager = authManager;
    this.jwtEncoder = jwtEncoder;
    this.expiration = expiration;
  }

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody LoginRequest body) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(body.username(), body.password()));

    String scope = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(" "));

    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("self")
        .issuedAt(now)
        .expiresAt(now.plusSeconds(expiration))
        .subject(auth.getName())
        .claim("scope", scope) // Resource Server espera "scope"/"scp"
        .build();

    String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    return Map.of("access_token", token, "token_type", "Bearer", "expires_in", expiration);
  }

  public record LoginRequest(String username, String password) {}
}
