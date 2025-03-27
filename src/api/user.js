import axios from 'axios';

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 5000
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从本地存储获取token并添加到请求头
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['token'] = token;
    }
    return config;
  },
  error => {
    console.log(error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    console.log('err', error);
    return Promise.reject(error);
  }
);

// 用户登录
export function login(data) {
  return service({
    url: '/accounts/login',
    method: 'post',
    data
  });
}

// 用户注册
export function register(data) {
  return service({
    url: '/accounts',
    method: 'post',
    data
  });
}

// 获取用户信息
export function getUserInfo(username) {
  return service({
    url: `/accounts/${username}`,
    method: 'get'
  });
}

// 更新用户信息
export function updateUserInfo(data) {
  return service({
    url: '/accounts',
    method: 'put',
    data
  });
} 