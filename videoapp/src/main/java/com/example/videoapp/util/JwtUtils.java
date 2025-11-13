package com.example.videoapp.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

// 注：本类请学习
public class JwtUtils {
    // 过期时间： 2小时
    private static final long EXPIRATION_TIME = 2 * 60 * 60 * 1000;
    // 密钥
    private static final String SECRET_KEY = "myFirstVideoProjectFireflyNumberKeyIs123456";

    /**
     * 生成JWT token（用用户id当唯一标识）
     * @param userId 数据库中的用户id（Long类型）
     * @return 生成的token字符串
     */
    public static String createToken(Long userId) {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKey secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
                .setSubject(String.valueOf(userId))  // 存储用户id（转为字符串）
                .setIssuedAt(new Date()) //token生成时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //设定过期时间
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token中解析出用户id（Long类型）
     * @param token 前端传递的token字符串
     * @return 解析成功返回userId，失败返回null（token无效/过期）
     */
    public static Long getUserId(String token){
        try {
            // 1. 构建密钥（和生成token时一致）
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
            SecretKey secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());

            // 2. 解析token，获取存储的userId字符串，转成Long
            String userIdStr = Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 设置签名密钥（必须和生成时一致）
                    .build()
                    .parseClaimsJws(token) // 解析token（无效/过期会抛异常）
                    .getBody()
                    .getSubject(); // 取出存储的userId

            return Long.valueOf(userIdStr);
        } catch (Exception e) {
            // 捕获所有解析异常（过期、签名错误、格式错误等），返回null表示无效
            return null;
        }
    }

    /**
     * 验证token是否有效
     * @param token 前端传递的token字符串
     * @return true=有效，false=无效/过期
     */
    public static boolean isTokenValid(String token) {
        // 只要能解析出userId，就说明token有效
        return getUserId(token) != null;
    }
}
