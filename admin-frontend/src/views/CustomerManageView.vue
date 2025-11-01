<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View as IconView } from '@element-plus/icons-vue'
import { listUsers, getUser, createUser, updateUser, deleteUser } from '../api/users'

const loading = ref(false)
const rows = ref([])
const pager = reactive({ page: 1, size: 10, total: 0 })
const q = ref('')
const hoverPhoneId = ref(null)

const showDialog = ref(false)
const editing = ref(false)
const form = ref({ id: null, name: '', phone: '', sex: '男', type: 0, avatar: '' })
const uploadList = ref([])

function maskPhone(p) {
  if (typeof p !== 'string') return '—'
  const last4 = p.slice(-4)
  return `*******${last4}`
}

function typeLabel(t) {
  return Number(t) === 1 ? '管理员' : '普通用户'
}

async function fetchList() {
  loading.value = true
  try {
    const { data } = await listUsers({ q: q.value || undefined, page: pager.page, size: pager.size })
    rows.value = (data?.records || []).map(r => ({
      id: String(r.id), name: r.name, phone: r.phone, type: r.type, avatar: r.avatar, createTime: r.createTime,
    }))
    pager.total = data?.total || 0
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pager.page = 1
  fetchList()
}

function onReset() {
  q.value = ''
  pager.page = 1
  fetchList()
}

function onPageChange(p) {
  pager.page = p
  fetchList()
}

function onPhoneEnter(row) {
  hoverPhoneId.value = row.id
}

function onPhoneLeave() {
  hoverPhoneId.value = null
}

function openCreate() {
  editing.value = false
  form.value = { id: null, name: '', phone: '', sex: '男', type: 0, avatar: '' }
  uploadList.value = []
  showDialog.value = true
}

async function openEdit(row) {
  editing.value = true
  try {
    const { data } = await getUser(row.id)
    form.value = { id: String(data.id), name: data.name || '', phone: data.phone || '', sex: data.sex || '男', type: Number(data.type) || 0, avatar: data.avatar || '' }
    uploadList.value = form.value.avatar ? [{ name: 'avatar', url: form.value.avatar.startsWith('/') ? form.value.avatar : `/${form.value.avatar}` }] : []
    showDialog.value = true
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '加载用户失败')
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除「${row.name}」吗？`, '提示', { type: 'warning' })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.response?.data?.message || '删除失败')
  }
}

async function onSave() {
  try {
    // 基本校验
    if (!form.value.name) throw new Error('请输入昵称')
    if (!/^1[3-9]\d{9}$/.test(form.value.phone)) throw new Error('手机号格式不正确')
    const payload = { name: form.value.name, phone: form.value.phone, sex: form.value.sex, type: Number(form.value.type), avatar: form.value.avatar || null, password: editing.value ? undefined : '12345678' }
    if (editing.value && form.value.id) {
      await updateUser(form.value.id, payload)
      ElMessage.success('更新成功')
    } else {
      const { data } = await createUser(payload)
      form.value.id = String(data.id)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    fetchList()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e.message || '保存失败')
  }
}

function onUploadSuccess(resp, file) {
  const url = (resp?.data?.path || resp?.data)
  form.value.avatar = typeof url === 'string' ? (url.startsWith('/') ? url : `/${url}`) : ''
}

onMounted(() => {
  fetchList()
})
</script>

<template>
  <div class="customers-page">
    <div class="page-title">客户管理</div>

    <div class="toolbar">
      <el-input v-model="q" placeholder="姓名/手机号" clearable style="width: 240px" />
      <el-button type="primary" style="margin-left:8px" @click="onSearch">搜索</el-button>
      <el-button style="margin-left:4px" @click="onReset">重置</el-button>
      <el-button type="success" style="margin-left:auto" @click="openCreate">新增客户</el-button>
    </div>

    <el-table :data="rows" v-loading="loading" class="customers-table" stripe>
      <el-table-column label="头像" width="80" align="center">
        <template #default="{ row }">
          <img :src="row.avatar || '/default-avatar.svg'" alt="avatar" class="avatar" />
        </template>
      </el-table-column>
      <el-table-column label="昵称" prop="name" min-width="160" />
      <el-table-column label="手机号" min-width="200">
        <template #default="{ row }">
          <div class="phone-cell">
            <span>{{ hoverPhoneId === row.id ? (row.phone || '—') : maskPhone(row.phone) }}</span>
            <el-icon class="phone-eye" @mouseenter="onPhoneEnter(row)" @mouseleave="onPhoneLeave"><icon-view /></el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="角色" width="120">
        <template #default="{ row }">{{ typeLabel(row.type) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ row.createTime ? new Date(row.createTime).toLocaleString() : '—' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="240" align="center">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" plain @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="pager.total"
        :page-size="pager.size"
        :current-page="pager.page"
        @current-change="onPageChange"
      />
    </div>

    <el-dialog v-model="showDialog" :title="editing ? '编辑客户' : '新增客户'" width="520px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="昵称"><el-input v-model="form.name" maxlength="32" show-word-limit /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" maxlength="11" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio-button label="男">男</el-radio-button>
            <el-radio-button label="女">女</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.type">
            <el-radio-button :label="0">普通用户</el-radio-button>
            <el-radio-button :label="1">管理员</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            action="/api/files"
            name="file"
            :auto-upload="true"
            :limit="1"
            :file-list="uploadList"
            :on-success="onUploadSuccess"
            :on-remove="() => { form.avatar = ''; uploadList = [] }"
          >
            <el-button type="primary">上传头像</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="text-align:right">
          <el-button @click="showDialog = false">取消</el-button>
          <el-button type="primary" @click="onSave">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.customers-page { background:#fff; border-radius:16px; padding:16px 24px; width: 100%; max-width: none; margin: 0; }
.page-title { font-size:20px; font-weight:700; margin-bottom:12px; }
.toolbar { display:flex; align-items:center; gap:8px; padding:10px; background:#f8f4f6; border-radius:12px; }
.customers-table { margin-top:12px; }
.avatar { width:36px; height:36px; border-radius:50%; object-fit:cover; }
.pagination { display:flex; justify-content:center; margin-top:12px; }
.phone-cell { display:flex; align-items:center; gap:6px; }
.phone-eye { cursor:pointer; color:#999; }
.phone-eye:hover { color:#409eff; }
</style>