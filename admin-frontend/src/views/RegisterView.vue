<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <h2>管理端注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少8位，含大小写与数字" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">注册</el-button>
          <el-button type="default" @click="toLogin" link>已有账号？去登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
  <div class="result" v-if="user">
    <el-alert type="success" title="注册成功" :closable="false">
      <template #default>
        <div>欢迎，{{ user.name }}（{{ user.phone }}）</div>
      </template>
    </el-alert>
  </div>
  <div class="result" v-if="error">
    <el-alert type="error" :title="error" show-icon />
  </div>
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
  name: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== form.value.password) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度8-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const onSubmit = () => {
  error.value = ''
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const { data } = await client.post('/auth/register', {
        name: form.value.name,
        phone: form.value.phone,
        password: form.value.password
      })
      user.value = data
      ElMessage.success('注册成功')
    } catch (e) {
      const msg = e?.response?.data?.message || '注册失败'
      error.value = msg
      ElMessage.error(msg)
    } finally {
      loading.value = false
    }
  })
}

const toLogin = () => router.push('/login')
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  margin-top: 80px;
}
.auth-card {
  width: 520px;
}
.result {
  max-width: 600px;
  margin: 20px auto;
}
</style>