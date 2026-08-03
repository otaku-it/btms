<script setup>
import { computed, onMounted, ref } from "vue";
import {
  BarChart3,
  CalendarDays,
  ChevronRight,
  CircleGauge,
  ExternalLink,
  GalleryHorizontal,
  Inbox,
  Landmark,
  ListOrdered,
  LogOut,
  MapPinned,
  Menu,
  Mountain,
  X,
} from "@lucide/vue";
import {
  clearAdminToken,
  fetchAdminContent,
  fetchInquiries,
  getAdminToken,
  logoutAdmin,
  verifyAdmin,
} from "./adminApi";
import AdminCollectionEditor from "./components/admin/AdminCollectionEditor.vue";
import AdminInquiries from "./components/admin/AdminInquiries.vue";
import AdminLogin from "./components/admin/AdminLogin.vue";
import AdminProfileForm from "./components/admin/AdminProfileForm.vue";

const authenticated = ref(Boolean(getAdminToken()));
const username = ref("");
const loading = ref(false);
const loadError = ref("");
const content = ref(null);
const inquiries = ref([]);
const activeSection = ref("overview");
const mobileMenuOpen = ref(false);

const navItems = [
  { id: "overview", label: "概览", icon: CircleGauge },
  { id: "profile", label: "基础资料", icon: Landmark },
  { id: "stats", label: "数据亮点", icon: BarChart3 },
  { id: "journey", label: "游玩路线", icon: MapPinned },
  { id: "seasons", label: "四时风物", icon: CalendarDays },
  { id: "gallery", label: "山水图库", icon: GalleryHorizontal },
  { id: "guides", label: "出行指南", icon: ListOrdered },
  { id: "inquiries", label: "游客留言", icon: Inbox },
];

const sectionTitle = computed(() => navItems.find((item) => item.id === activeSection.value)?.label || "管理后台");
const totalContent = computed(() => content.value
  ? content.value.stats.length + content.value.journey.length + content.value.seasons.length + content.value.gallery.length + content.value.guides.length
  : 0);
const publishedContent = computed(() => content.value
  ? [content.value.profile, ...content.value.stats, ...content.value.journey, ...content.value.seasons, ...content.value.gallery, ...content.value.guides].filter((item) => item.published).length
  : 0);
const newInquiryCount = computed(() => inquiries.value.filter((item) => item.status === "NEW").length);

const editorConfig = {
  stats: {
    kicker: "KEY FIGURES", description: "首页展示的核心数字，拖动顺序以外可使用上下按钮调整。",
    fields: [{ key: "value", label: "展示数值", maxlength: 40 }, { key: "label", label: "说明文字", maxlength: 100 }],
    emptyItem: { value: "", label: "" },
  },
  journey: {
    kicker: "TRAVEL ROUTE", description: "维护半日慢游路线、建议时长和配图。",
    fields: [
      { key: "duration", label: "建议时长", maxlength: 80 }, { key: "title", label: "路线标题", maxlength: 120 },
      { key: "description", label: "路线介绍", type: "textarea", rows: 4, wide: true, maxlength: 5000 },
      { key: "tag", label: "路线标签", maxlength: 80 }, { key: "imageAlt", label: "图片说明", maxlength: 200 },
      { key: "imageUrl", label: "路线配图", type: "image", wide: true },
    ],
    emptyItem: { duration: "建议 60 分钟", title: "", description: "", tag: "", imageUrl: "", imageAlt: "" },
  },
  seasons: {
    kicker: "FOUR SEASONS", description: "维护四季标签、推荐景观和出行提醒。",
    fields: [
      { key: "code", label: "英文代码", maxlength: 20 }, { key: "name", label: "季节简称", maxlength: 20 },
      { key: "period", label: "月份范围", maxlength: 80 }, { key: "title", label: "季节标题", maxlength: 160 },
      { key: "description", label: "季节介绍", type: "textarea", rows: 4, wide: true, maxlength: 5000 },
      { key: "sight", label: "推荐看点", maxlength: 120 }, { key: "note", label: "出行提示", maxlength: 120 },
    ],
    emptyItem: { code: "", name: "", period: "", title: "", description: "", sight: "", note: "" },
  },
  gallery: {
    kicker: "IMAGE GALLERY", description: "管理官网图库。横幅、竖图和标准图会进入不同版位。",
    fields: [
      { key: "title", label: "图片标题", maxlength: 100 }, { key: "scope", label: "拍摄范围", maxlength: 100 },
      { key: "imageAlt", label: "无障碍说明", maxlength: 200 },
      { key: "layout", label: "图库版式", type: "select", options: [{ value: "wide", label: "横向主图" }, { value: "tall", label: "竖向图片" }, { value: "standard", label: "标准图片" }] },
      { key: "imageUrl", label: "图库图片", type: "image", wide: true },
    ],
    emptyItem: { title: "", imageUrl: "", imageAlt: "", scope: "碧潭村实景", layout: "standard" },
  },
  guides: {
    kicker: "TRAVEL GUIDE", description: "维护游客出发前需要了解的交通和准备事项。",
    fields: [{ key: "title", label: "信息标题", maxlength: 80 }, { key: "content", label: "详细说明", type: "textarea", rows: 4, wide: true, maxlength: 5000 }],
    emptyItem: { title: "", content: "" },
  },
};

async function loadDashboard() {
  loading.value = true;
  loadError.value = "";
  try {
    const [adminContent, visitorInquiries] = await Promise.all([fetchAdminContent(), fetchInquiries()]);
    content.value = adminContent;
    inquiries.value = visitorInquiries;
  } catch (error) {
    if (error.status === 401) {
      authenticated.value = false;
      username.value = "";
    } else {
      loadError.value = error.message;
    }
  } finally {
    loading.value = false;
  }
}

async function refreshInquiries() {
  try {
    inquiries.value = await fetchInquiries();
  } catch (error) {
    loadError.value = error.message;
  }
}

function selectSection(id) {
  activeSection.value = id;
  mobileMenuOpen.value = false;
  window.scrollTo({ top: 0, behavior: "smooth" });
}

async function authenticatedHandler(result) {
  username.value = result.username;
  authenticated.value = true;
  await loadDashboard();
}

async function signOut() {
  try { await logoutAdmin(); } catch { /* The local token is cleared either way. */ }
  clearAdminToken();
  authenticated.value = false;
  content.value = null;
  inquiries.value = [];
}

onMounted(async () => {
  if (!authenticated.value) return;
  try {
    const me = await verifyAdmin();
    username.value = me.username;
    await loadDashboard();
  } catch {
    clearAdminToken();
    authenticated.value = false;
  }
});
</script>

<template>
  <AdminLogin v-if="!authenticated" @authenticated="authenticatedHandler" />
  <div v-else class="admin-shell" :class="{ 'menu-open': mobileMenuOpen }">
    <aside class="admin-sidebar">
      <a class="admin-sidebar-brand" href="/"><span><Mountain :size="20" /></span><div><strong>碧潭村</strong><small>内容管理中心</small></div></a>
      <nav aria-label="管理栏目">
        <button v-for="item in navItems" :key="item.id" type="button" :class="{ active: activeSection === item.id }" @click="selectSection(item.id)"><component :is="item.icon" :size="18" /><span>{{ item.label }}</span><ChevronRight :size="15" /></button>
      </nav>
      <div class="admin-sidebar-footer"><div class="admin-user-avatar">{{ username.slice(0, 1).toUpperCase() }}</div><div><strong>{{ username }}</strong><small>管理员</small></div><button type="button" title="退出登录" aria-label="退出登录" @click="signOut"><LogOut :size="17" /></button></div>
    </aside>

    <header class="admin-mobile-header">
      <a href="/"><Mountain :size="19" /><strong>碧潭村管理</strong></a>
      <button type="button" :aria-expanded="mobileMenuOpen" aria-label="切换管理菜单" @click="mobileMenuOpen = !mobileMenuOpen"><X v-if="mobileMenuOpen" :size="22" /><Menu v-else :size="22" /></button>
    </header>

    <main class="admin-main">
      <header class="admin-page-header"><div><p>内容管理中心</p><h1>{{ sectionTitle }}</h1></div><a href="/" target="_blank">查看官网<ExternalLink :size="16" /></a></header>
      <div v-if="loading && !content" class="admin-loading"><span></span><p>正在载入管理数据</p></div>
      <div v-else-if="loadError && !content" class="admin-load-error"><p>{{ loadError }}</p><button type="button" @click="loadDashboard">重新加载</button></div>
      <template v-else-if="content">
        <section v-if="activeSection === 'overview'" class="admin-overview">
          <div class="admin-welcome"><p class="admin-kicker">DASHBOARD</p><h2>今天也在认真记录碧潭。</h2><p>官网内容修改保存后会立即从数据库读取最新版本。</p></div>
          <div class="admin-metrics">
            <article><span>内容条目</span><strong>{{ totalContent + 1 }}</strong><small>包含基础资料</small></article>
            <article><span>已发布</span><strong>{{ publishedContent }}</strong><small>当前官网可见</small></article>
            <article><span>游客留言</span><strong>{{ inquiries.length }}</strong><small>累计收到</small></article>
            <article class="accent"><span>待处理</span><strong>{{ newInquiryCount }}</strong><small>新留言</small></article>
          </div>
          <div class="admin-quick-sections"><h3>快速进入</h3><div><button v-for="item in navItems.slice(1)" :key="item.id" type="button" @click="selectSection(item.id)"><component :is="item.icon" :size="19" /><span>{{ item.label }}</span><ChevronRight :size="16" /></button></div></div>
        </section>

        <AdminProfileForm v-else-if="activeSection === 'profile'" :profile="content.profile" @saved="content = $event" />
        <AdminCollectionEditor v-else-if="editorConfig[activeSection]" :title="sectionTitle" :kicker="editorConfig[activeSection].kicker" :description="editorConfig[activeSection].description" :section="activeSection" :items="content[activeSection]" :fields="editorConfig[activeSection].fields" :empty-item="editorConfig[activeSection].emptyItem" @saved="content = $event" />
        <AdminInquiries v-else-if="activeSection === 'inquiries'" :inquiries="inquiries" @refresh="refreshInquiries" />
      </template>
    </main>
  </div>
</template>
