<script setup>
import { ref, watch } from "vue";
import { ImageUp, Save } from "@lucide/vue";
import { saveProfile, uploadMedia } from "../../adminApi";

const props = defineProps({ profile: { type: Object, required: true } });
const emit = defineEmits(["saved"]);
const clone = (value) => JSON.parse(JSON.stringify(value));
const form = ref(clone(props.profile));
const saving = ref(false);
const uploading = ref("");
const status = ref("");

watch(() => props.profile, (value) => { form.value = clone(value); }, { deep: true });

async function uploadImage(event, field, label) {
  const file = event.target.files?.[0];
  if (!file) return;
  uploading.value = field;
  status.value = "";
  try {
    const media = await uploadMedia(file);
    form.value[field] = media.url;
    status.value = `${label}已上传（${media.width} × ${media.height}），保存后生效`;
  } catch (error) {
    status.value = error.message;
  } finally {
    uploading.value = "";
    event.target.value = "";
  }
}

async function submit() {
  saving.value = true;
  status.value = "";
  try {
    const result = await saveProfile(form.value);
    status.value = "基础资料已保存";
    emit("saved", result);
  } catch (error) {
    status.value = error.message;
  } finally {
    saving.value = false;
  }
}
</script>

<template>
  <form class="admin-editor" @submit.prevent="submit">
    <div class="admin-editor-heading">
      <div><p class="admin-kicker">SITE PROFILE</p><h2>基础资料</h2><p>管理首页主标题、村落简介和地图入口。</p></div>
    </div>
    <div class="admin-form-grid">
      <label><span>村名</span><input v-model.trim="form.name" maxlength="80" required /></label>
      <label><span>所在位置</span><input v-model.trim="form.location" maxlength="160" required /></label>
      <label><span>首屏短标</span><input v-model.trim="form.eyebrow" maxlength="120" required /></label>
      <label><span>首页宣传语</span><input v-model.trim="form.slogan" maxlength="200" required /></label>
      <label class="admin-field-wide"><span>简介引言</span><textarea v-model.trim="form.introLead" rows="3" required></textarea></label>
      <label class="admin-field-wide"><span>村落介绍</span><textarea v-model.trim="form.introBody" rows="5" required></textarea></label>
      <label><span>茶乡板块标题</span><input v-model.trim="form.landTitle" maxlength="160" required /></label>
      <label><span>百度地图地址</span><input v-model.trim="form.mapUrl" maxlength="500" required /></label>
      <div class="admin-field-wide admin-profile-hero">
        <span>首页横幅</span>
        <div class="admin-image-control">
          <img v-if="form.heroImageUrl" :src="form.heroImageUrl" alt="首页横幅预览" />
          <div><input v-model.trim="form.heroImageUrl" maxlength="300" required /><label class="admin-upload-button"><ImageUp :size="16" /><span>{{ uploading === 'heroImageUrl' ? "上传中" : "上传横幅" }}</span><input type="file" accept="image/jpeg,image/png" :disabled="Boolean(uploading)" @change="uploadImage($event, 'heroImageUrl', '横幅')" /></label></div>
        </div>
      </div>
      <div class="admin-field-wide admin-profile-hero">
        <span>村落介绍配图</span>
        <div class="admin-image-control">
          <img v-if="form.introImageUrl" :src="form.introImageUrl" alt="村落介绍配图预览" />
          <div><input v-model.trim="form.introImageUrl" maxlength="300" required /><label class="admin-upload-button"><ImageUp :size="16" /><span>{{ uploading === 'introImageUrl' ? "上传中" : "上传配图" }}</span><input type="file" accept="image/jpeg,image/png" :disabled="Boolean(uploading)" @change="uploadImage($event, 'introImageUrl', '村落介绍配图')" /></label></div>
        </div>
      </div>
      <label><span>百科板块标题</span><input v-model.trim="form.baikeTitle" maxlength="160" required /></label>
      <label><span>百度百科词条地址</span><input v-model.trim="form.baikeSourceUrl" maxlength="500" required /></label>
      <label class="admin-field-wide"><span>百科简介</span><textarea v-model.trim="form.baikeSummary" rows="5" maxlength="5000" required></textarea></label>
      <label class="admin-field-wide"><span>茶乡板块介绍</span><textarea v-model.trim="form.landDescription" rows="4" required></textarea></label>
    </div>
    <div class="admin-save-bar">
      <p :class="{ 'is-error': status && !status.includes('已保存') }">{{ status }}</p>
      <button class="admin-primary-button" type="submit" :disabled="saving"><Save :size="17" /><span>{{ saving ? "保存中" : "保存资料" }}</span></button>
    </div>
  </form>
</template>
