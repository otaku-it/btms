<script setup>
import { computed, reactive, ref } from "vue";
import { createInquiry } from "../api";

const form = reactive({
  name: "",
  email: "",
  visitDate: "",
  partySize: 2,
  message: "",
});

const submitting = ref(false);
const submitError = ref("");
const fieldErrors = ref({});
const confirmation = ref(null);
const minDate = computed(() => new Date().toISOString().slice(0, 10));

const submit = async () => {
  submitting.value = true;
  submitError.value = "";
  fieldErrors.value = {};
  confirmation.value = null;

  try {
    confirmation.value = await createInquiry({
      ...form,
      visitDate: form.visitDate || null,
      partySize: form.partySize ? Number(form.partySize) : null,
    });
    form.name = "";
    form.email = "";
    form.visitDate = "";
    form.partySize = 2;
    form.message = "";
  } catch (error) {
    submitError.value = error.message;
    fieldErrors.value = error.fields || {};
  } finally {
    submitting.value = false;
  }
};
</script>

<template>
  <section class="inquiry-section" id="message" aria-labelledby="inquiry-title">
    <div class="inquiry-intro">
      <p class="eyebrow light">写给碧潭</p>
      <h2 id="inquiry-title">把到访的念头，先留在这里</h2>
      <p>计划来走走，或有想了解的事情，都可以写下来。留言会安全保存在网站数据库中。</p>
    </div>

    <form class="inquiry-form" @submit.prevent="submit" novalidate>
      <div class="field-row">
        <label>
          <span>你的称呼</span>
          <input v-model.trim="form.name" name="name" autocomplete="name" maxlength="40" required placeholder="怎么称呼你" />
          <small v-if="fieldErrors.name">{{ fieldErrors.name }}</small>
        </label>
        <label>
          <span>联系邮箱</span>
          <input v-model.trim="form.email" name="email" type="email" autocomplete="email" maxlength="120" required placeholder="name@example.com" />
          <small v-if="fieldErrors.email">{{ fieldErrors.email }}</small>
        </label>
      </div>

      <div class="field-row">
        <label>
          <span>预计到访</span>
          <input v-model="form.visitDate" name="visitDate" type="date" :min="minDate" />
          <small v-if="fieldErrors.visitDate">{{ fieldErrors.visitDate }}</small>
        </label>
        <label>
          <span>同行人数</span>
          <input v-model="form.partySize" name="partySize" type="number" min="1" max="30" inputmode="numeric" />
          <small v-if="fieldErrors.partySize">{{ fieldErrors.partySize }}</small>
        </label>
      </div>

      <label>
        <span>想说的话</span>
        <textarea v-model.trim="form.message" name="message" maxlength="1000" rows="5" required placeholder="关于路线、季节或村里的故事……"></textarea>
        <small v-if="fieldErrors.message">{{ fieldErrors.message }}</small>
      </label>

      <div class="form-footer">
        <p class="form-status" aria-live="polite">
          <template v-if="confirmation">已收到，留言编号 #{{ confirmation.id }}</template>
          <template v-else-if="submitError">{{ submitError }}</template>
          <template v-else>提交即表示同意仅将信息用于本次到访沟通。</template>
        </p>
        <button class="submit-button" type="submit" :disabled="submitting">
          {{ submitting ? "正在提交" : "提交留言" }} <span aria-hidden="true">→</span>
        </button>
      </div>
    </form>
  </section>
</template>
