package com.six.landing.config.security.filter;

import com.alibaba.fastjson.JSON;
import com.six.landing.common.SpringUtils;
import com.six.landing.config.exception.ConstantKey;
import com.six.landing.config.security.data.ResponseBody;
import com.six.landing.dao.UsersDao;
import com.six.landing.domian.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 自定义JWT认证过滤器
 * 该类继承自BasicAuthenticationFilter，在doFilterInternal方法中，
 * 从http头的Authorization 项读取token数据，然后用Jwts包提供的方法校验token的合法性。
 * 如果校验通过，就认为这是一个取得授权的合法请求
 * @author zhaoxinguo on 2017/9/13.
 */
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    ResponseBody responseBody=new ResponseBody();

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
           // chain.doFilter(request, response);
            responseBody.setStatus("300");
            responseBody.setMsg("token为空 ");
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(responseBody));
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request,response);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //验证通过走
        if(authentication!=null){
            chain.doFilter(request, response);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,HttpServletResponse response)throws IOException, ServletException {
        long start = System.currentTimeMillis();
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            responseBody.setStatus("300");
            responseBody.setMsg("token为空 ");
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(responseBody));
           // throw new TokenException("Token为空",response);
        }
        // parse the token.
        String user = null;
        try {
            user = Jwts.parser()
                    .setSigningKey(ConstantKey.SIGNING_KEY)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody()
                    .getSubject();
            long end = System.currentTimeMillis();
            logger.info("执行时间: {}", (end - start) + " 毫秒");
            if (user != null) {
                String[] split = user.split("-")[1].split(",");
                ArrayList<GrantedAuthority> authorities = new ArrayList<>();
                for (int i=0; i < split.length; i++) {
                    authorities.add(new GrantedAuthorityImpl(split[i]));
                }
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }

        } catch (ExpiredJwtException e) {
            logger.error("Token已过期: {} " + e);
            responseBody.setStatus("301");
            responseBody.setMsg("token过期 ");
        } catch (UnsupportedJwtException e) {
            responseBody.setStatus("302");
            responseBody.setMsg("token格式错误");
            logger.error("Token格式错误: {} " + e);
        } catch (MalformedJwtException e) {
            responseBody.setStatus("303");
            responseBody.setMsg("token错误");
            logger.error("Token没有被正确构造: {} " + e);
        } catch (SignatureException e) {
            responseBody.setStatus("304");
            responseBody.setMsg("token签名失败 ");
            logger.error("签名失败: {} " + e);

        } catch (IllegalArgumentException e) {
            responseBody.setStatus("305");
            responseBody.setMsg("token参数不正确 ");
            logger.error("非法参数异常: {} " + e);
        }
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(responseBody));
        return null;
    }

}
