<script setup>
import { computed, ref } from "vue";
import { Archive, Check, Mail, RefreshCw, Trash2 } from "@lucide/vue";
import { deleteInquiry, updateInquiryStatus } from "../../adminApi";

const props = defineProps({ inquiries: { type: Array, required: true } });
const emit = defineEmits(["refresh"]);
const filter = ref("ALL");
const workingId = ref(null);
const error = ref("");
const filtered = computed(() => filter.value === "ALL" ? props.inquiries : props.inquiries.filter((item) => item.status === filter.value));
const statusLabel = { NEW: "新留言", READ: "已读", ARCHIVED: "已归档" };

const formatDate = (value) => new Intl.DateTimeFormat("zh-CN", { dateStyle: "medium", timeStyle: "short" }).format(new Date(value));

async function setStatus(item, status) {
  workingId.value = item.id;
  error.value = "";
  try {
    await updateInquiryStatus(item.id, status);
    emit("refresh");
  } catch (requestError) {
    error.value = requestError.message;
  } finally {
    workingId.value = null;
  }
}

async function remove(item) {
  if (!window.confirm(`确定删除 ${item.name} 的留言吗？此操作不能撤销。`)) return;
  workingId.value = item.id;
  error.value = "";
  try {
    await deleteInquiry(item.id);
    emit("refresh");
  } catch (requestError) {
    error.value = requestError.message;
  } finally {
    workingId.value = null;
  }
}
</script>

<template>
  <section class="admin-editor admin-inquiries">
    <div class="admin-editor-heading">
      <div><p class="admin-kicker">VISITOR INBOX</p><h2>游客留言</h2><p>查看到访意向，并按处理进度归档。</p></div>
      <button class="admin-secondary-button" type="button" @click="emit('refresh')"><RefreshCw :size="16" />刷新</button>
    </div>
    <div class="admin-filter-tabs" role="tablist" aria-label="筛选留言">
      <button v-for="item in [{value:'ALL',label:'全部'},{value:'NEW',label:'新留言'},{value:'READ',label:'已读'},{value:'ARCHIVED',label:'已归档'}]" :key="item.value" type="button" :class="{ active: filter === item.value }" @click="filter = item.value">{{ item.label }}</button>
    </div>
    <p v-if="error" class="admin-form-error">{{ error }}</p>
    <div class="admin-inquiry-list">
      <article v-for="item in filtered" :key="item.id" class="admin-inquiry-item">
        <div class="admin-inquiry-meta"><span :class="`status-${item.status.toLowerCase()}`">{{ statusLabel[item.status] }}</span><time>{{ formatDate(item.createdAt) }}</time></div>
        <div class="admin-inquiry-person"><div><strong>{{ item.name }}</strong><a :href="`mailto:${item.email}`"><Mail :size="14" />{{ item.email }}</a></div><p>{{ item.visitDate || "日期待定" }}<span>·</span>{{ item.partySize ? `${item.partySize} 人` : "人数待定" }}</p></div>
        <p class="admin-inquiry-message">{{ item.message }}</p>
        <div class="admin-inquiry-actions">
          <button v-if="item.status !== 'READ'" type="button" :disabled="workingId === item.id" @click="setStatus(item, 'READ')"><Check :size="16" />标记已读</button>
          <button v-if="item.status !== 'ARCHIVED'" type="button" :disabled="workingId === item.id" @click="setStatus(item, 'ARCHIVED')"><Archive :size="16" />归档</button>
          <button v-if="item.status !== 'NEW'" type="button" :disabled="workingId === item.id" @click="setStatus(item, 'NEW')">恢复为新留言</button>
          <button class="danger" type="button" :disabled="workingId === item.id" @click="remove(item)"><Trash2 :size="16" />删除</button>
        </div>
      </article>
      <div v-if="!filtered.length" class="admin-empty-state"><p>当前筛选下没有留言</p></div>
    </div>
  </section>
</template>
