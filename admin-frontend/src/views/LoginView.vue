<template>
  <div class="auth-card">
    <AppLogo />
    <div class="auth-title">欢迎使用花之舞</div>
    <div class="auth-subtitle">使用手机号登录</div>

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入您的手机号" maxlength="11" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" :type="showPwd ? 'text' : 'password'" placeholder="请输入您的密码" >
          <template #suffix>
            <el-icon @click="showPwd = !showPwd" style="cursor:pointer;">
              <component :is="showPwd ? Hide : View" />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-button class="brand-btn" type="primary" size="large" round style="width:100%;" :loading="loading" @click="onSubmit">登录</el-button>

      <div class="footer-text">
        <a class="link-pink" href="javascript:void(0)">忘记密码？</a>
      </div>
      <div class="footer-text">
        还没有账户？ <router-link class="link-pink" to="/register">在此注册</router-link>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, Hide } from '@element-plus/icons-vue'
import AppLogo from '../components/AppLogo.vue'
import { login } from '../api/auth'
const DEFAULT_AVATAR = '/default-avatar.svg'

const formRef = ref()
const loading = ref(false)
const showPwd = ref(false)
const form = ref({ phone: '', password: '' })
const route = useRoute()

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: ['blur','change'] },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '长度至少8位', trigger: 'blur' },
  ],
}

const onSubmit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const { data } = await login({ phone: form.value.phone, password: form.value.password })
    ElMessage.success('登录成功')
    console.log('login response', data)
    try {
      // 设置默认头像（后续可被 /api/auth/me 返回的真实头像覆盖）
      localStorage.setItem('userAvatar', DEFAULT_AVATAR)
    } catch (e) {
      // localStorage 写入失败时静默处理
    }
  } catch (e) {
    const status = e?.response?.status
    if (status === 401) ElMessage.error('账号或密码错误')
    else ElMessage.error(e?.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const qp = route.query?.phone
  if (typeof qp === 'string' && qp) {
    form.value.phone = qp
  }
})
</script>

<style scoped>
.el-form { margin-top: 8px; }
.el-form-item { margin-bottom: 18px; }
.el-form-item__label { font-weight: 600; }
</style>