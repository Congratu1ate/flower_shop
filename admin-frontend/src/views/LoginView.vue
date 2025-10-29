<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <h2>管理端登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.rememberMe">记住我</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">登录</el-button>
          <el-button type="default" @click="toRegister" link>没有账号？去注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
  <div class="result" v-if="user">
    <el-alert type="success" title="登录成功" :closable="false">
      <template #default>
        <div>欢迎，{{ user.name }}（{{ user.phone }}）</div>
      </template>
    </el-alert>
  </div>
  <div class="result" v-if="error">
    <el-alert type="error" :title="error" show-icon />
  </div>
  <el-divider />
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import client from '../api/client'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const user = ref(null)
const error = ref('')

const form = ref({
  phone: '',
  password: '',
  rememberMe: false
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度8-20位', trigger: 'blur' }
  ]
}

const onSubmit = () => {
  error.value = ''
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const { data } = await client.post('/auth/login', {
        phone: form.value.phone,
        password: form.value.password,
        rememberMe: form.value.rememberMe
      })
      // 后端返回 { token, expiresIn, user }
      user.value = data.user
      ElMessage.success('登录成功')
      // 可选：跳转到管理端首页（占位）
      // router.push('/dashboard')
    } catch (e) {
      const msg = e?.response?.data?.message || '登录失败'
      error.value = msg
      ElMessage.error(msg)
    } finally {
      loading.value = false
    }
  })
}

const toRegister = () => router.push('/register')
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  margin-top: 80px;
}
.auth-card {
  width: 420px;
}
.result {
  max-width: 600px;
  margin: 20px auto;
}
</style>