<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from "vue";
import InquiryForm from "./components/InquiryForm.vue";
import { fetchSiteContent } from "./api";

const CONTENT_UPDATED_KEY = "bitan_content_updated";

const content = ref(null);
const loading = ref(true);
const loadError = ref("");
const menuOpen = ref(false);
const headerScrolled = ref(false);
const activeSeasonCode = ref("");
const activeGalleryIndex = ref(0);
const lightbox = ref(null);
let revealObserver;
let refreshTimer;

const activeSeason = computed(() =>
  content.value?.seasons.find((season) => season.code === activeSeasonCode.value) || content.value?.seasons[0]
);

const activeGalleryItem = computed(() => content.value?.gallery[activeGalleryIndex.value]);

const installRevealObserver = () => {
  revealObserver?.disconnect();
  revealObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        entry.target.classList.add("is-visible");
        revealObserver.unobserve(entry.target);
      });
    },
    { threshold: 0.12 }
  );
  document.querySelectorAll(".reveal:not(.is-visible)").forEach((element) => revealObserver.observe(element));
};

const loadSite = async ({ silent = false } = {}) => {
  if (!silent) {
    loading.value = true;
    loadError.value = "";
  }
  try {
    const previousSeason = activeSeasonCode.value;
    const latestContent = await fetchSiteContent();
    content.value = latestContent;
    activeSeasonCode.value = latestContent.seasons.some((season) => season.code === previousSeason)
      ? previousSeason
      : latestContent.seasons[0]?.code || "";
    if (!silent) loading.value = false;
    await nextTick();
    installRevealObserver();
  } catch (error) {
    if (!silent) loadError.value = error.message;
  } finally {
    if (!silent) loading.value = false;
  }
};

const scheduleContentRefresh = () => {
  window.clearTimeout(refreshTimer);
  refreshTimer = window.setTimeout(() => loadSite({ silent: true }), 120);
};

const handleStorage = (event) => {
  if (event.key === CONTENT_UPDATED_KEY) scheduleContentRefresh();
};

const handleVisibilityChange = () => {
  if (document.visibilityState === "visible") scheduleContentRefresh();
};

const updateHeader = () => {
  headerScrolled.value = window.scrollY > 28;
};

const closeMenu = () => {
  menuOpen.value = false;
  document.body.classList.remove("menu-open");
};

const toggleMenu = () => {
  menuOpen.value = !menuOpen.value;
  document.body.classList.toggle("menu-open", menuOpen.value);
};

const openGallery = (index) => {
  activeGalleryIndex.value = index;
  lightbox.value?.showModal();
};

const closeGallery = () => lightbox.value?.close();

const moveGallery = (direction) => {
  const total = content.value?.gallery.length || 0;
  if (!total) return;
  activeGalleryIndex.value = (activeGalleryIndex.value + direction + total) % total;
};

const handleKeydown = (event) => {
  if (!lightbox.value?.open) return;
  if (event.key === "ArrowLeft") moveGallery(-1);
  if (event.key === "ArrowRight") moveGallery(1);
};

onMounted(() => {
  loadSite();
  updateHeader();
  window.addEventListener("scroll", updateHeader, { passive: true });
  window.addEventListener("keydown", handleKeydown);
  window.addEventListener("storage", handleStorage);
  window.addEventListener("focus", scheduleContentRefresh);
  document.addEventListener("visibilitychange", handleVisibilityChange);
});

onBeforeUnmount(() => {
  revealObserver?.disconnect();
  window.clearTimeout(refreshTimer);
  window.removeEventListener("scroll", updateHeader);
  window.removeEventListener("keydown", handleKeydown);
  window.removeEventListener("storage", handleStorage);
  window.removeEventListener("focus", scheduleContentRefresh);
  document.removeEventListener("visibilitychange", handleVisibilityChange);
});
</script>

<template>
  <a class="skip-link" href="#main">跳到主要内容</a>

  <header class="site-header" :class="{ scrolled: headerScrolled, 'menu-active': menuOpen }">
    <a class="brand" href="#top" aria-label="碧潭村首页" @click="closeMenu">
      <span class="brand-mark">碧潭</span>
      <span class="brand-place">安徽 · 石台</span>
    </a>
    <nav class="desktop-nav" aria-label="主导航">
      <a href="#story">村落印象</a><a href="#journey">游玩路线</a><a href="#seasons">四时风物</a><a href="#guide">出行指南</a>
    </nav>
    <a class="nav-cta" href="#message">写下留言 <span aria-hidden="true">↗</span></a>
    <button class="menu-button" type="button" :aria-label="menuOpen ? '关闭导航菜单' : '打开导航菜单'" :aria-expanded="menuOpen" aria-controls="mobile-menu" @click="toggleMenu">
      <span></span><span></span>
    </button>
  </header>

  <nav id="mobile-menu" class="mobile-menu" :class="{ open: menuOpen }" aria-label="移动端导航">
    <a href="#story" @click="closeMenu">村落印象</a><a href="#journey" @click="closeMenu">游玩路线</a><a href="#seasons" @click="closeMenu">四时风物</a><a href="#guide" @click="closeMenu">出行指南</a><a href="#message" @click="closeMenu">写下留言</a>
  </nav>

  <main id="main">
    <section v-if="loading" class="app-state" aria-live="polite">
      <div class="loader-mark">碧潭</div>
      <p>正在从村落资料库取回山水故事</p>
    </section>

    <section v-else-if="loadError" class="app-state app-error" aria-live="assertive">
      <div class="loader-mark">碧潭</div>
      <h1>暂时没能连接到村落资料库</h1>
      <p>{{ loadError }}</p>
      <button type="button" @click="loadSite">重新加载</button>
    </section>

    <template v-else-if="content">
      <section class="hero" id="top" :aria-labelledby="'hero-title'">
        <img class="hero-image" :src="content.profile.heroImageUrl || '/assets/hero-village.jpg'" :alt="`${content.profile.name}与田野全景`" fetchpriority="high" />
        <div class="hero-shade"></div>
        <div class="hero-content reveal is-visible">
          <p class="eyebrow light">{{ content.profile.eyebrow }}</p>
          <h1 id="hero-title">{{ content.profile.name }}</h1>
          <p class="hero-subtitle">{{ content.profile.slogan }}</p>
          <div class="hero-actions">
            <a class="button button-primary" href="#journey">开始漫游 <span aria-hidden="true">↓</span></a>
            <a class="button button-ghost" :href="content.profile.mapUrl" target="_blank" rel="noreferrer">地图导航 <span aria-hidden="true">↗</span></a>
          </div>
        </div>
        <div class="hero-facts" aria-label="碧潭村概况">
          <div v-for="stat in content.stats" :key="stat.id"><strong>{{ stat.value }}</strong><span>{{ stat.label }}</span></div>
        </div>
      </section>

      <section class="intro section" id="story">
        <div class="section-heading reveal"><p class="eyebrow">一村 · 一水 · 一方山</p><h2>名字里，藏着这里最初的风景</h2></div>
        <div class="intro-grid">
          <div class="intro-copy reveal">
            <p class="lead">{{ content.profile.introLead }}</p><p>{{ content.profile.introBody }}</p>
            <a class="text-link" href="#gallery">看见碧潭实景 <span aria-hidden="true">→</span></a>
          </div>
          <figure class="feature-image reveal"><img :src="content.profile.introImageUrl || '/assets/riverwalk.webp'" alt="碧潭村公信河畔整洁的亲水步道" loading="lazy" /><figcaption><span>01</span> 公信河畔 · 碧潭村实景</figcaption></figure>
        </div>
      </section>

      <section class="baike-band" id="encyclopedia" aria-labelledby="baike-title">
        <div class="baike-inner section">
          <div class="baike-heading reveal">
            <p class="eyebrow">村落百科</p>
            <h2 id="baike-title">{{ content.profile.baikeTitle }}</h2>
            <a :href="content.profile.baikeSourceUrl" target="_blank" rel="noreferrer">查看百度百科词条 <span aria-hidden="true">↗</span></a>
          </div>
          <div class="baike-content reveal">
            <p>{{ content.profile.baikeSummary }}</p>
            <dl>
              <div v-for="stat in content.stats" :key="`baike-${stat.id}`"><dt>{{ stat.value }}</dt><dd>{{ stat.label }}</dd></div>
            </dl>
          </div>
        </div>
      </section>

      <section class="land-band" aria-label="碧潭村自然资源">
        <div class="land-image reveal"><img src="/assets/tea-garden.webp" alt="云雾山脚下的村舍和茶园" loading="lazy" /></div>
        <div class="land-copy reveal">
          <p class="eyebrow light">云上茶乡</p><h2>{{ content.profile.landTitle }}</h2><p>{{ content.profile.landDescription }}</p>
          <dl class="land-data"><div v-for="stat in content.stats" :key="`land-${stat.id}`"><dt>{{ stat.label }}</dt><dd>{{ stat.value }}</dd></div></dl>
        </div>
      </section>

      <section class="journey section" id="journey">
        <div class="section-heading split-heading reveal">
          <div><p class="eyebrow">半日慢游路线</p><h2>顺着水，走进村子里</h2></div>
          <p>没有匆忙的打卡清单。沿河、入村、上山，给自己半天时间，感受碧潭最自然的生活尺度。</p>
        </div>
        <ol class="route-list">
          <li v-for="(stop, index) in content.journey" :key="stop.id" class="route-stop reveal">
            <div class="route-number">{{ String(index + 1).padStart(2, "0") }}</div>
            <div class="route-content"><p class="route-time">{{ stop.duration }}</p><h3>{{ stop.title }}</h3><p>{{ stop.description }}</p><span class="route-tag">{{ stop.tag }}</span></div>
            <img :src="stop.imageUrl" :alt="stop.imageAlt" loading="lazy" />
          </li>
        </ol>
      </section>

      <section class="seasons" id="seasons">
        <div class="seasons-inner section">
          <div class="section-heading reveal"><p class="eyebrow light">四时风物</p><h2>山里的每一季，都有自己的回信</h2></div>
          <div class="season-tabs reveal" role="tablist" aria-label="选择季节">
            <button v-for="season in content.seasons" :key="season.id" type="button" role="tab" :aria-selected="activeSeasonCode === season.code" aria-controls="season-panel" @click="activeSeasonCode = season.code">{{ season.name }}</button>
          </div>
          <div v-if="activeSeason" id="season-panel" class="season-panel reveal" role="tabpanel" aria-live="polite">
            <p class="season-kicker">{{ activeSeason.period }}</p><h3>{{ activeSeason.title }}</h3><p>{{ activeSeason.description }}</p>
            <div class="season-meta"><span>{{ activeSeason.sight }}</span><span>{{ activeSeason.note }}</span></div>
          </div>
        </div>
      </section>

      <section class="gallery section" id="gallery">
        <div class="section-heading split-heading reveal">
          <div><p class="eyebrow">山水影像</p><h2>看见真实的碧潭</h2></div><p>碧潭村实景与仙寓山周边风光均在图片说明中标明范围。</p>
        </div>
        <div class="gallery-grid reveal">
          <button v-for="(item, index) in content.gallery" :key="item.id" class="gallery-item" :class="`gallery-${item.layout}`" type="button" :aria-label="`查看${item.title}大图`" @click="openGallery(index)">
            <img :src="item.imageUrl" :alt="item.imageAlt" loading="lazy" /><span>{{ item.title }} · {{ item.scope }}</span>
          </button>
        </div>
      </section>

      <section class="guide" id="guide">
        <div class="guide-inner section">
          <div class="guide-title reveal"><p class="eyebrow">出发之前</p><h2>把时间留给山水</h2></div>
          <div class="guide-details reveal"><div v-for="(guide, index) in content.guides" :key="guide.id"><span class="guide-index">{{ String(index + 1).padStart(2, "0") }}</span><h3>{{ guide.title }}</h3><p>{{ guide.content }}</p></div></div>
          <a class="guide-map-link reveal" :href="content.profile.mapUrl" target="_blank" rel="noreferrer"><span>在百度地图中查看</span><span class="map-arrow" aria-hidden="true">↗</span></a>
          <InquiryForm />
        </div>
      </section>
    </template>
  </main>

  <footer v-if="content" class="site-footer">
    <div class="footer-brand"><span>{{ content.profile.name }}</span><p>山有清风，水有回声。<br />我们在皖南等你。</p></div>
    <div class="footer-meta"><p>{{ content.profile.location }}</p><p>资料参考：百度百科、石台县文明网及公开报道</p><p>© {{ new Date().getFullYear() }} 碧潭村家乡宣传站</p></div>
  </footer>

  <dialog ref="lightbox" class="lightbox" aria-label="大图预览" @click.self="closeGallery">
    <button class="lightbox-close" type="button" aria-label="关闭大图" title="关闭" @click="closeGallery">×</button>
    <button class="lightbox-nav lightbox-prev" type="button" aria-label="上一张" title="上一张" @click="moveGallery(-1)">‹</button>
    <figure v-if="activeGalleryItem"><img :src="activeGalleryItem.imageUrl" :alt="activeGalleryItem.imageAlt" /><figcaption>{{ activeGalleryItem.title }} · {{ activeGalleryItem.scope }}</figcaption></figure>
    <button class="lightbox-nav lightbox-next" type="button" aria-label="下一张" title="下一张" @click="moveGallery(1)">›</button>
  </dialog>
</template>
