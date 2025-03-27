package com.example.tomatomall.controller;

import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.util.JwtUtil;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Resource
    AccountService accountService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取用户详情
     */
    @GetMapping("/{username}")
    public Response getUser(@PathVariable String username, HttpServletRequest request) {
        // 验证token
        String token = request.getHeader("token");
        if (token == null || !jwtUtil.validateToken(token)) {
            return Response.buildFailure("未授权", "401");
        }
        
        return accountService.getUserByUsername(username);
    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response createUser(@RequestBody AccountVO accountVO) {
        return accountService.createUser(accountVO);
    }

    /**
     * 更新用户信息
     */
    @PutMapping()
    public Response updateUser(@RequestBody AccountVO accountVO, HttpServletRequest request) {
        // 验证token
        String token = request.getHeader("token");
        if (token == null || !jwtUtil.validateToken(token)) {
            return Response.buildFailure("未授权", "401");
        }
        
        // 从token中获取用户名
        String username = jwtUtil.extractUsername(token);
        
        return accountService.updateUser(accountVO, username);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response login(@RequestBody AccountVO accountVO) {
        return accountService.login(accountVO.getUsername(), accountVO.getPassword());
    }
}
