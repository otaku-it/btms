<script setup>
import { ref, watch } from "vue";
import { ArrowDown, ArrowUp, ImageUp, Plus, Save, Trash2 } from "@lucide/vue";
import { saveContentSection, uploadMedia } from "../../adminApi";

const props = defineProps({
  title: { type: String, required: true },
  kicker: { type: String, required: true },
  description: { type: String, required: true },
  section: { type: String, required: true },
  items: { type: Array, required: true },
  fields: { type: Array, required: true },
  emptyItem: { type: Object, required: true },
});
const emit = defineEmits(["saved"]);
const clone = (value) => JSON.parse(JSON.stringify(value));
const draft = ref(clone(props.items));
const saving = ref(false);
const uploadingIndex = ref(-1);
const status = ref("");

watch(() => props.items, (value) => { draft.value = clone(value); }, { deep: true });

function normalizeOrder() {
  draft.value.forEach((item, index) => { item.sortOrder = index + 1; });
}

function addItem() {
  draft.value.push({ ...clone(props.emptyItem), id: null, sortOrder: draft.value.length + 1, published: true });
}

function removeItem(index) {
  draft.value.splice(index, 1);
  normalizeOrder();
}

function move(index, offset) {
  const target = index + offset;
  if (target < 0 || target >= draft.value.length) return;
  [draft.value[index], draft.value[target]] = [draft.value[target], draft.value[index]];
  normalizeOrder();
}

async function upload(event, index, key) {
  const file = event.target.files?.[0];
  if (!file) return;
  uploadingIndex.value = index;
  status.value = "";
  try {
    const media = await uploadMedia(file);
    draft.value[index][key] = media.url;
    status.value = `图片已上传（${media.width} × ${media.height}）`;
  } catch (error) {
    status.value = error.message;
  } finally {
    uploadingIndex.value = -1;
    event.target.value = "";
  }
}

async function save() {
  saving.value = true;
  status.value = "";
  normalizeOrder();
  try {
    const result = await saveContentSection(props.section, draft.value);
    status.value = `${props.title}已保存`;
    emit("saved", result);
  } catch (error) {
    status.value = error.message;
  } finally {
    saving.value = false;
  }
}
</script>

<template>
  <section class="admin-editor">
    <div class="admin-editor-heading">
      <div><p class="admin-kicker">{{ kicker }}</p><h2>{{ title }}</h2><p>{{ description }}</p></div>
      <button class="admin-secondary-button" type="button" @click="addItem"><Plus :size="17" />新增</button>
    </div>

    <div class="admin-item-list">
      <article v-for="(item, index) in draft" :key="item.id ?? `new-${index}`" class="admin-content-item">
        <header>
          <div><span>{{ String(index + 1).padStart(2, "0") }}</span><strong>{{ item.title || item.label || item.name || "新内容" }}</strong></div>
          <div class="admin-item-actions">
            <label class="admin-publish-toggle compact"><input v-model="item.published" type="checkbox" /><span>{{ item.published ? "已发布" : "已隐藏" }}</span></label>
            <button type="button" title="上移" aria-label="上移" :disabled="index === 0" @click="move(index, -1)"><ArrowUp :size="17" /></button>
            <button type="button" title="下移" aria-label="下移" :disabled="index === draft.length - 1" @click="move(index, 1)"><ArrowDown :size="17" /></button>
            <button class="danger" type="button" title="删除" aria-label="删除" @click="removeItem(index)"><Trash2 :size="17" /></button>
          </div>
        </header>
        <div class="admin-form-grid compact-grid">
          <label v-for="field in fields" :key="field.key" :class="{ 'admin-field-wide': field.wide, 'admin-image-field': field.type === 'image' }">
            <span>{{ field.label }}</span>
            <textarea v-if="field.type === 'textarea'" v-model.trim="item[field.key]" :rows="field.rows || 3" :maxlength="field.maxlength" required></textarea>
            <select v-else-if="field.type === 'select'" v-model="item[field.key]">
              <option v-for="option in field.options" :key="option.value" :value="option.value">{{ option.label }}</option>
            </select>
            <div v-else-if="field.type === 'image'" class="admin-image-control">
              <img v-if="item[field.key]" :src="item[field.key]" alt="图片预览" />
              <div><input v-model.trim="item[field.key]" maxlength="300" required /><span class="admin-upload-button"><ImageUp :size="16" /><span>{{ uploadingIndex === index ? "上传中" : "上传图片" }}</span><input type="file" accept="image/jpeg,image/png" :disabled="uploadingIndex >= 0" @change="upload($event, index, field.key)" /></span></div>
            </div>
            <input v-else v-model.trim="item[field.key]" :type="field.type || 'text'" :maxlength="field.maxlength" required />
          </label>
        </div>
      </article>
      <div v-if="!draft.length" class="admin-empty-state"><p>这里还没有内容</p><button type="button" @click="addItem">添加第一条</button></div>
    </div>

    <div class="admin-save-bar">
      <p :class="{ 'is-error': status && !status.includes('已保存') && !status.includes('已上传') }">{{ status }}</p>
      <button class="admin-primary-button" type="button" :disabled="saving || uploadingIndex >= 0" @click="save"><Save :size="17" /><span>{{ saving ? "保存中" : `保存${title}` }}</span></button>
    </div>
  </section>
</template>
