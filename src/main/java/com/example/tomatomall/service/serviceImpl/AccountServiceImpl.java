package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.util.JwtUtil;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");

    @Override
    public Response<AccountVO> getUserByUsername(String username) {
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            AccountVO accountVO = account.toVO();
            // 不返回密码
            accountVO.setPassword(null);
            return Response.buildSuccess(accountVO);
        }
        return Response.buildFailure("用户不存在", "404");
    }

    @Override
    public Response<String> createUser(AccountVO accountVO) {
        // 验证必填字段
        if (accountVO.getUsername() == null || accountVO.getPassword() == null || 
            accountVO.getName() == null || accountVO.getRole() == null) {
            return Response.buildFailure("必填字段不能为空", "400");
        }
        
        // 验证用户名唯一性
        if (accountRepository.existsByUsername(accountVO.getUsername())) {
            return Response.buildFailure("用户名已存在", "400");
        }
        
        // 验证电话号码格式（如果提供）
        if (accountVO.getTelephone() != null && !accountVO.getTelephone().isEmpty() && 
            !PHONE_PATTERN.matcher(accountVO.getTelephone()).matches()) {
            return Response.buildFailure("电话号码格式不正确，必须为1开头的11位数字", "400");
        }
        
        // 验证邮箱格式（如果提供）
//        if (accountVO.getEmail() != null && !accountVO.getEmail().isEmpty() &&
//            !EMAIL_PATTERN.matcher(accountVO.getEmail()).matches()) {
//            return Response.buildFailure("邮箱格式不正确", "400");
//        }
        
        // 创建新用户
        Account account = new Account();
        account.setUsername(accountVO.getUsername());
        account.setPassword(passwordEncoder.encode(accountVO.getPassword()));
        account.setName(accountVO.getName());
        account.setAvatar(accountVO.getAvatar());
        account.setRole(accountVO.getRole());
        account.setTelephone(accountVO.getTelephone());
        account.setEmail(accountVO.getEmail());
        account.setLocation(accountVO.getLocation());
        
        accountRepository.save(account);
        
        return Response.buildSuccess("注册成功");
    }

    @Override
    public Response<String> login(String username, String password) {
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (!accountOpt.isPresent()) {
            return Response.buildFailure("用户不存在", "400");
        }
        
        Account account = accountOpt.get();
        if (!passwordEncoder.matches(password, account.getPassword())) {
            return Response.buildFailure("用户密码错误", "400");
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(username);
        
        return Response.buildSuccess(token);
    }

    @Override
    public Response<String> updateUser(AccountVO accountVO, String username) {
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (!accountOpt.isPresent()) {
            return Response.buildFailure("用户不存在", "404");
        }
        
        Account account = accountOpt.get();
        
        // 用户名不允许修改
        
        // 更新其他字段（如果提供）
        if (accountVO.getName() != null) {
            account.setName(accountVO.getName());
        }
        
        if (accountVO.getAvatar() != null) {
            account.setAvatar(accountVO.getAvatar());
        }
        
        if (accountVO.getRole() != null) {
            account.setRole(accountVO.getRole());
        }
        
        if (accountVO.getTelephone() != null) {
            if (!accountVO.getTelephone().isEmpty() && !PHONE_PATTERN.matcher(accountVO.getTelephone()).matches()) {
                return Response.buildFailure("电话号码格式不正确，必须为1开头的11位数字", "400");
            }
            account.setTelephone(accountVO.getTelephone());
        }
        
        if (accountVO.getEmail() != null) {
            if (!accountVO.getEmail().isEmpty() && !EMAIL_PATTERN.matcher(accountVO.getEmail()).matches()) {
                return Response.buildFailure("邮箱格式不正确", "400");
            }
            account.setEmail(accountVO.getEmail());
        }
        
        if (accountVO.getLocation() != null) {
            account.setLocation(accountVO.getLocation());
        }
        
        // 如果提供了新密码，则更新密码
        if (accountVO.getPassword() != null && !accountVO.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(accountVO.getPassword()));
        }
        
        accountRepository.save(account);
        
        return Response.buildSuccess("更新成功");
    }
}
