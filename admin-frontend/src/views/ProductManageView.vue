<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { listFlowers, deleteFlower, updateFlowerStatus, createFlower, updateFlower } from '../api/flowers'

const query = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const rows = ref([])

// 新增/编辑弹窗
const showDialog = ref(false)
const editing = ref(false)
const form = ref({ id: null, name: '', categoryId: null, price: 0, image: '', description: '', status: 1, inventory: 0 })
const uploadList = ref([])

function toCurrency(v) { return `¥${v}` }

// 处理图片路径：如果是相对路径则添加 / 前缀，如果已经是完整URL则直接使用
function getImageUrl(imagePath) {
  if (!imagePath) return '/default-avatar.svg' // 默认图片
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://') || imagePath.startsWith('/')) {
    return imagePath
  }
  return `/${imagePath}` // 添加 / 前缀以访问 public 目录
}

async function fetchList() {
  loading.value = true
  try {
    const { data } = await listFlowers({ q: query.value || undefined, page: page.value, size: size.value })
    // MyBatis-Plus Page<T> 结构：records, total, size, current
    // 前端以字符串存储主键，避免超出 JS 安全整数范围导致精度丢失
    rows.value = (data?.records || []).map(r => ({
      id: String(r.id), name: r.name, categoryId: r.categoryId, price: r.price, inventory: r.inventory, status: r.status, image: r.image
    }))
    total.value = data?.total || 0
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  page.value = 1
  fetchList()
}

function onPageChange(p) {
  page.value = p
  fetchList()
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除「${row.name}」吗？`, '提示', { type: 'warning' })
    await deleteFlower(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.response?.data?.message || '删除失败')
  }
}

async function onToggleStatus(row) {
  const target = row.status === 1 ? 0 : 1
  try {
    await updateFlowerStatus(row.id, target)
    ElMessage.success(target === 1 ? '已上架' : '已下架')
    fetchList()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '操作失败')
  }
}

function openCreate() {
  editing.value = false
  form.value = { id: null, name: '', categoryId: null, price: 0, image: '', description: '', status: 1, inventory: 0 }
  uploadList.value = []
  showDialog.value = true
}

function openEdit(row) {
  editing.value = true
  form.value = { id: row.id, name: row.name, categoryId: row.categoryId, price: row.price, image: row.image, description: row.description, status: row.status, inventory: row.inventory }
  uploadList.value = form.value.image ? [{ name: form.value.image, url: getImageUrl(form.value.image) }] : []
  showDialog.value = true
}

function onImageChange(file, fileList) {
  const latest = fileList.slice(-1)
  uploadList.value = latest
}

function onImageRemove() {
  uploadList.value = []
  form.value.image = ''
}

// 上传成功：后端返回 { url, name, size, type }
function onUploadSuccess(response, file, fileList) {
  const url = response?.url || ''
  form.value.image = url
  uploadList.value = url ? [{ name: file?.name || response?.name || 'image', url: getImageUrl(url) }] : []
  ElMessage.success('图片上传成功')
}

function onUploadError(err, file, fileList) {
  ElMessage.error('图片上传失败')
}

async function onSubmit() {
  try {
    // 安全数值转换：避免 ''/null 被转成 NaN 从而序列化为 null 触发后端 400
    const toNum = (v) => (v === '' || v === null || v === undefined ? null : Number(v))
    const name = (form.value.name || '').trim()
    const categoryId = toNum(form.value.categoryId)
    const price = toNum(form.value.price)
    const status = toNum(form.value.status)
    const inventory = toNum(form.value.inventory)

    // 前端必填校验，拦截无效值
    if (!name) { ElMessage.warning('请填写名称'); return }
    if (categoryId === null || categoryId < 1) { ElMessage.warning('请填写有效的分类ID'); return }
    if (price === null || isNaN(price) || price < 0) { ElMessage.warning('请填写有效的价格'); return }

    const payload = {
      name,
      categoryId,
      price,
      image: form.value.image || '',
      description: form.value.description || '',
      status: status === null ? 1 : status,
      inventory: inventory === null ? 0 : inventory,
    }
    if (editing.value) await updateFlower(form.value.id, payload)
    else await createFlower(payload)
    ElMessage.success(editing.value ? '保存成功' : '创建成功')
    showDialog.value = false
    fetchList()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.response?.data || '提交失败'
    ElMessage.error(msg)
  }
}

onMounted(fetchList)
</script>

<template>
  <div class="page">
    <div class="header">
      <div class="title">商品管理</div>
      <el-button type="primary" class="add-btn" round @click="openCreate">新增鲜花</el-button>
    </div>

    <div class="tip">在这里查看、编辑、删除鲜花信息，上传商品图片，调整库存与价格。</div>

    <div class="search-bar">
      <el-input v-model="query" placeholder="按名称搜索鲜花" clearable @keyup.enter="onSearch">
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <div class="card">
      <el-table :data="rows" border stripe class="table" :loading="loading">
        <el-table-column label="商品图片" width="120">
          <template #default="{ row }">
            <img :src="getImageUrl(row.image)" class="thumb" alt="flower" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="280" />
        <el-table-column label="分类" min-width="120">
          <template #default="{ row }">{{ row.categoryId }}</template>
        </el-table-column>
        <el-table-column label="价格" min-width="120">
          <template #default="{ row }">{{ toCurrency(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="inventory" label="库存" min-width="100" />
        <el-table-column label="状态" min-width="160">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" round style="margin-right:8px;">{{ row.status === 1 ? '在售' : '下架' }}</el-tag>
            <el-button size="small" round @click="onToggleStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="160">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="prev, pager, next"
          :page-size="size"
          :current-page="page"
          :total="total"
          @current-change="onPageChange"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="showDialog" :title="editing ? '编辑鲜花' : '新增鲜花'" width="520px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" maxlength="32" show-word-limit />
        </el-form-item>
        <el-form-item label="分类ID">
          <el-input-number v-model="form.categoryId" :min="1" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.inventory" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio-button :label="1">在售</el-radio-button>
            <el-radio-button :label="0">下架</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            action="/api/files"
            name="file"
            :auto-upload="true"
            :limit="1"
            :file-list="uploadList"
            :on-change="onImageChange"
            :on-remove="onImageRemove"
            :on-success="onUploadSuccess"
            :on-error="onUploadError"
            accept="image/*"
            list-type="picture"
          >
            <el-button type="primary" plain>选择图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" maxlength="255" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog=false">取消</el-button>
        <el-button type="primary" class="brand-btn" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { display:flex; flex-direction:column; gap: 12px; max-width: 1200px; margin: 0 auto; }
.header { display:flex; align-items:center; justify-content:space-between; }
.title { font-size:28px; font-weight:800; }
.tip { background:#f2e6ec; border-radius:16px; padding:12px 16px; color:#7a6d74; }
.search-bar { background:#f2e6ec; border-radius:18px; padding:8px 12px; }
.add-btn { background:#f06292; border-color:#f06292; }
.card { background:#fff; border-radius:18px; padding:12px; box-shadow: 0 1px 2px rgba(0,0,0,.06); }
.table :deep(.el-table__header-wrapper) th { background: #fff; }
.table { width: 100%; }
.thumb { width:64px; height:64px; border-radius:50%; object-fit:cover; box-shadow:0 0 0 4px #f7eaf0 inset; }
.pager { display:flex; justify-content:center; padding:14px; }
</style>