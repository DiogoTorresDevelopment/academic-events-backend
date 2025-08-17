package br.com.famper.events.tenancy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String slug = req.getHeader("X-Tenant");
    if (slug == null || slug.isBlank()) slug = extractFromSubdomain(req.getServerName());
    TenantContext.set(slug);
    try { chain.doFilter(req, res); }
    finally { TenantContext.clear(); }
  }
  private String extractFromSubdomain(String host) { return null; }
}