<script setup>
import { ref } from "vue";
import { ArrowRight, LockKeyhole, MapPin } from "@lucide/vue";
import { loginAdmin } from "../../adminApi";

const emit = defineEmits(["authenticated"]);
const username = ref("admin");
const password = ref("");
const error = ref("");
const submitting = ref(false);

async function submit() {
  error.value = "";
  submitting.value = true;
  try {
    const result = await loginAdmin({ username: username.value, password: password.value });
    emit("authenticated", result);
  } catch (requestError) {
    error.value = requestError.message;
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <main class="admin-login-page">
    <section class="admin-login-brand" aria-label="碧潭村管理后台">
      <div class="admin-login-mark"><MapPin :size="24" /></div>
      <p>碧潭村 · 家乡宣传站</p>
      <h1>把家乡的山水，<br />认真地讲给更多人听。</h1>
      <span>内容、影像与游客留言统一管理</span>
    </section>
    <section class="admin-login-panel">
      <form class="admin-login-form" @submit.prevent="submit">
        <div class="admin-login-icon"><LockKeyhole :size="22" /></div>
        <p class="admin-kicker">ADMIN CONSOLE</p>
        <h2>登录管理后台</h2>
        <p class="admin-login-help">使用管理员账号进入内容工作台。</p>
        <label>
          <span>用户名</span>
          <input v-model.trim="username" autocomplete="username" maxlength="60" required />
        </label>
        <label>
          <span>密码</span>
          <input v-model="password" type="password" autocomplete="current-password" maxlength="200" required autofocus />
        </label>
        <p v-if="error" class="admin-form-error" role="alert">{{ error }}</p>
        <button class="admin-primary-button admin-login-button" type="submit" :disabled="submitting">
          <span>{{ submitting ? "正在登录" : "进入工作台" }}</span><ArrowRight :size="18" />
        </button>
        <a class="admin-back-link" href="/">返回旅游官网</a>
      </form>
    </section>
  </main>
</template>
