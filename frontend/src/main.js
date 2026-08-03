import { createApp } from "vue";
import App from "./App.vue";
import AdminApp from "./AdminApp.vue";
import "./style.css";
import "./admin.css";

const rootComponent = window.location.pathname.startsWith("/admin") ? AdminApp : App;
if (rootComponent === AdminApp) document.title = "碧潭村管理后台";
createApp(rootComponent).mount("#app");
