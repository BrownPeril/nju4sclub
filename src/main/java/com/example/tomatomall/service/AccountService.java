package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.Response;

public interface AccountService {
    Response<AccountVO> getUserByUsername(String username);
    Response<String> createUser(AccountVO accountVO);
    Response<String> login(String username, String password);
    Response<String> updateUser(AccountVO accountVO, String username);
}
