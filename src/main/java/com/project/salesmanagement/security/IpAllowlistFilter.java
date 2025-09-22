package com.project.salesmanagement.security;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(SecurityProperties.BASIC_AUTH_ORDER - 1)
public class IpAllowlistFilter implements Filter {

  private final List<String> allowed;

  public IpAllowlistFilter(@Value("${security.allowed-ips}") String ips) {
    this.allowed = List.of(ips.split("\\s*,\\s*"));
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String ip = req.getRemoteAddr();

    if (req.getRequestURI().startsWith("/swagger-ui") || req.getRequestURI().startsWith("/v3/api-docs")) {
      chain.doFilter(request, response);
      return;
    }

    boolean ok = allowed.stream().anyMatch(allow -> matches(allow, ip));
    if (!ok) {
      ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "IP not allowed");
      return;
    }
    chain.doFilter(request, response);
  }

  private boolean matches(String pattern, String ip) {
    if (pattern.contains("/")) {
      // naive CIDR check (IPv4 only) – replace with a robust CIDR lib if needed
      String[] parts = pattern.split("/");
      String base = parts[0];
      int prefix = Integer.parseInt(parts[1]);
      return cidrMatch(base, ip, prefix);
    }
    return pattern.equals(ip);
  }

  private boolean cidrMatch(String base, String ip, int prefix) {
    try {
      byte[] b1 = InetAddress.getByName(base).getAddress();
      byte[] b2 = InetAddress.getByName(ip).getAddress();
      int bits = prefix;
      for (int i = 0; i < b1.length && bits > 0; i++) {
        int mask = bits >= 8 ? 0xFF : (~(0xFF >> bits)) & 0xFF;
        if ((b1[i] & mask) != (b2[i] & mask))
          return false;
        bits -= 8;
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
