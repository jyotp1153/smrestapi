package com.shyamSofttech.studentManagement.authUtils;

import com.shyamSofttech.studentManagement.constant.ApiErrorCodes;
import com.shyamSofttech.studentManagement.constant.UserRole;
import com.shyamSofttech.studentManagement.entities.UserEntity;
import com.shyamSofttech.studentManagement.exception.NoSuchElementFoundException;
import com.shyamSofttech.studentManagement.repositories.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Component
public class JwtHelper {


    @Autowired
    private UserRepo userRepo;

    @Value("${jwt.tokenValidityInSeconds}")
    public long JWT_TOKEN_VALIDITY ;

    private static final String secret = "ABCDEFGHIJfVIVEKghklKLMNNDHDNDO01234persisjpandeydcjsdcknsjdt5PQRSUVWXYZabcdemnouvwxyz664565665178-_"; // secret code
    static byte[] secretKeyBytes = secret.getBytes();
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes)).build().parseClaimsJws(token).getBody();
    }
    public List<UserRole> getUserRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes)).build().parseClaimsJws(token).getBody();
        List<String> roles = claims.get("userRole", List.class);
        return roles.stream()
                .map(UserRole::valueOf)
                .collect(Collectors.toList());
    }
    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes)).build().parseClaimsJws(token).getBody();
        return claims.get("id", Long.class);
    }
    private Boolean isTokenExpired(String token) {                                      // checking expire
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()),SignatureAlgorithm.HS512).compact();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Optional<UserEntity> optionalUserEntity = userRepo.findByUserName(userDetails.getUsername());
        if(optionalUserEntity.isEmpty()){
            throw new NoSuchElementFoundException(ApiErrorCodes.USER_NOT_FOUND.getErrorCode(), ApiErrorCodes.USER_NOT_FOUND.getErrorMessage());
        }
        UserEntity user = optionalUserEntity.get();
        claims.put("name",user.getName());
        claims.put("email",user.getEmail());
        claims.put("status",user.getStatus());
        claims.put("id", user.getId());
        claims.put("userRole", user.getUserRoleList());
        return doGenerateToken(claims, userDetails.getUsername());
    }    private String doGenerateClientToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()),SignatureAlgorithm.HS512).compact();
    }

    private String doGenerateClientFmcgToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()),SignatureAlgorithm.HS512).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public Boolean validateOnlyToken(String token) {
        return isTokenExpired(token);
    }
}
