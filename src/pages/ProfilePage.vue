<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { getUserInfo, updateUserInfo } from '@/api/user.js';

const router = useRouter();
const loading = ref(false);
const userInfoLoading = ref(true);

const userForm = reactive({
  username: '',
  name: '',
  role: '',
  avatar: '',
  telephone: '',
  email: '',
  location: ''
});

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  telephone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
};

const userFormRef = ref(null);

onMounted(async () => {
  await fetchUserInfo();
});

const fetchUserInfo = async () => {
  userInfoLoading.value = true;
  try {
    const username = localStorage.getItem('username');
    if (!username) {
      ElMessage.error('未登录或登录已过期');
      router.push('/login');
      return;
    }
    
    const res = await getUserInfo(username);
    if (res.code === '200') {
      Object.assign(userForm, res.data);
    } else {
      ElMessage.error(res.msg || '获取用户信息失败');
    }
  } catch (error) {
    console.error(error);
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录');
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      router.push('/login');
    } else {
      ElMessage.error('获取用户信息失败，请稍后重试');
    }
  } finally {
    userInfoLoading.value = false;
  }
};

const handleUpdate = async () => {
  if (!userFormRef.value) return;
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res = await updateUserInfo(userForm);
        if (res.code === '200') {
          ElMessage.success('更新成功');
          await fetchUserInfo();
        } else {
          ElMessage.error(res.msg || '更新失败');
        }
      } catch (error) {
        console.error(error);
        ElMessage.error('更新失败，请稍后重试');
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  ElMessage.success('退出登录成功');
  router.push('/login');
};
</script>

<template>
  <div class="profile-container">
    <el-card class="profile-card" v-loading="userInfoLoading">
      <template #header>
        <div class="card-header">
          <h2>个人信息</h2>
        </div>
      </template>
      
      <div v-if="userForm.avatar" class="avatar-container">
        <el-avatar :size="100" :src="userForm.avatar"></el-avatar>
      </div>
      
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="身份">
          <el-input v-model="userForm.role" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name"></el-input>
        </el-form-item>
        <el-form-item label="头像链接">
          <el-input v-model="userForm.avatar"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="telephone">
          <el-input v-model="userForm.telephone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
        <el-form-item label="所在地">
          <el-input v-model="userForm.location"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleUpdate">更新信息</el-button>
          <el-button type="danger" @click="handleLogout">退出登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  margin-top: 60px;
  padding: 20px 0;
}

.profile-card {
  width: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

h2 {
  margin: 0;
}
</style> 