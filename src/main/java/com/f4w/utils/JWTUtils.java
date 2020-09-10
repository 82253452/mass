package com.f4w.utils;

import com.f4w.entity.SysUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.collections4.map.HashedMap;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JWTUtils {
    @Value("${jwt.key}")
    private String key;
    @Value("${jwt.exp}")
    private Integer exp;

    public String creatKey(Map map) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key keySpec = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
        JwtBuilder builder = Jwts.builder();
        builder.setClaims(map);
        builder.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        builder.setExpiration(DateTime.now().plusHours(exp).toDate());
        builder.setIssuedAt(new Date());
        builder.setNotBefore(new Date());
        builder.signWith(keySpec);
        String jws = builder.compact();
        return jws;
    }

    public String creatKey(SysUser sysUser) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key keySpec = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
        JwtBuilder builder = Jwts.builder();
        Map map = new HashedMap();
        map.put("uid", sysUser.getId());
        builder.setClaims(map);
        builder.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        builder.setExpiration(DateTime.now().plusHours(exp).toDate());
        builder.setIssuedAt(new Date());
        builder.setNotBefore(new Date());
        builder.signWith(keySpec);
        String jws = builder.compact();
        sysUser.setToken(jws);
        return jws;
    }


    public Claims parseBody(String jwsString) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwsString).getBody();
        return claims;
    }
}
