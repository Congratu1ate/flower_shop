<template>
  <div class="auth-card">
    <AppLogo />
    <div class="auth-title">创建您的账户</div>
    <div class="auth-subtitle">欢迎来到我们的花店</div>

    <!-- 注册成功提示与倒计时 -->
    <el-alert v-if="success" type="success" :closable="false" title="注册成功" class="mb-16"
      :description="`将于 ${countdown}s 后跳转至登录页面，并为您自动填充手机号。`" />

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入您的姓名" maxlength="32" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入您的手机号" maxlength="11">
          <template #prepend>+86</template>
        </el-input>
      </el-form-item>

      <el-form-item label="设置密码" prop="password">
        <el-input v-model="form.password" :type="showPwd ? 'text' : 'password'" placeholder="请输入您的密码">
          <template #suffix>
            <el-icon @click="showPwd = !showPwd" style="cursor:pointer;">
              <component :is="showPwd ? Hide : View" />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="确认密码" prop="confirm">
        <el-input v-model="form.confirm" :type="showConfirm ? 'text' : 'password'" placeholder="请再次输入您的密码">
          <template #suffix>
            <el-icon @click="showConfirm = !showConfirm" style="cursor:pointer;">
              <component :is="showConfirm ? Hide : View" />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="性别">
        <el-radio-group v-model="form.sex">
          <el-radio-button label="男" />
          <el-radio-button label="女" />
          <el-radio-button label="未知" />
        </el-radio-group>
      </el-form-item>

      <el-button class="brand-btn" type="primary" size="large" round style="width:100%;" :loading="loading" :disabled="success" @click="onSubmit">注册</el-button>

      <div class="footer-text">已经有账户了？ <router-link class="link-pink" to="/login">登录</router-link></div>
      <div class="footer-text" style="opacity:.7;">创建账户即表示您同意我们的服务条款与隐私政策。</div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, Hide } from '@element-plus/icons-vue'
import AppLogo from '../components/AppLogo.vue'
import { register } from '../api/auth'

const formRef = ref()
const loading = ref(false)
const showPwd = ref(false)
const showConfirm = ref(false)
const form = ref({ name: '', phone: '', password: '', confirm: '', sex: '未知' })

// 注册成功后的倒计时与跳转
const success = ref(false)
const countdown = ref(5)
const timerId = ref(null)
const router = useRouter()

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 32, message: '姓名长度 2-32 个字符', trigger: ['blur','change'] },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: ['blur','change'] },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '长度至少8位', trigger: 'blur' },
  ],
  confirm: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: (_, v, cb) => { v === form.value.password ? cb() : cb(new Error('两次输入不一致')) }, trigger: ['blur','change'] },
  ],
}

const onSubmit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const payload = { name: form.value.name.trim(), phone: form.value.phone, password: form.value.password, sex: form.value.sex }
    const { data } = await register(payload)
    ElMessage.success('注册成功')
    console.log('register response', data)
    success.value = true
    countdown.value = 5
    // 启动 5 秒倒计时，结束跳转到登录并携带手机号
    timerId.value = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timerId.value)
        router.push({ path: '/login', query: { phone: form.value.phone } })
      }
    }, 1000)
  } catch (e) {
    const status = e?.response?.status
    if (status === 409) ElMessage.error('手机号已存在')
    else ElMessage.error(e?.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}

onUnmounted(() => {
  if (timerId.value) clearInterval(timerId.value)
})
</script>

<style scoped>
.el-form { margin-top: 8px; }
.el-form-item { margin-bottom: 18px; }
.el-form-item__label { font-weight: 600; }
.mb-16 { margin-bottom: 16px; }
</style>