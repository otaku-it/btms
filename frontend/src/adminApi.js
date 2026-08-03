const API_BASE = import.meta.env.VITE_API_BASE_URL || "";
const TOKEN_KEY = "bitan_admin_token";
export const CONTENT_UPDATED_KEY = "bitan_content_updated";

export const getAdminToken = () => sessionStorage.getItem(TOKEN_KEY) || "";
export const setAdminToken = (token) => sessionStorage.setItem(TOKEN_KEY, token);
export const clearAdminToken = () => sessionStorage.removeItem(TOKEN_KEY);

async function adminRequest(path, options = {}) {
  const headers = { ...options.headers };
  const token = getAdminToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  if (options.body && !(options.body instanceof FormData)) headers["Content-Type"] = "application/json";

  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  const data = await response.json().catch(() => null);
  if (!response.ok) {
    if (response.status === 401) clearAdminToken();
    const fallbackMessage = response.status === 413
      ? "图片过大，请选择 10MB 以内的 JPEG 或 PNG 图片"
      : "管理服务暂时不可用";
    const error = new Error(data?.message || fallbackMessage);
    error.status = response.status;
    error.fields = data?.fields || {};
    throw error;
  }
  return data;
}

function notifyContentUpdated() {
  try {
    localStorage.setItem(CONTENT_UPDATED_KEY, String(Date.now()));
  } catch {
    // Saving content should still succeed when browser storage is unavailable.
  }
}

export async function loginAdmin(credentials) {
  const result = await adminRequest("/api/admin/auth/login", {
    method: "POST",
    body: JSON.stringify(credentials),
  });
  setAdminToken(result.token);
  return result;
}

export const verifyAdmin = () => adminRequest("/api/admin/auth/me");
export const logoutAdmin = () => adminRequest("/api/admin/auth/logout", { method: "POST" });
export const fetchAdminContent = () => adminRequest("/api/admin/content");
export async function saveProfile(profile) {
  const result = await adminRequest("/api/admin/content/profile", { method: "PUT", body: JSON.stringify(profile) });
  notifyContentUpdated();
  return result;
}

export async function saveContentSection(section, items) {
  const result = await adminRequest(`/api/admin/content/${section}`, { method: "PUT", body: JSON.stringify(items) });
  notifyContentUpdated();
  return result;
}
export const fetchInquiries = () => adminRequest("/api/admin/inquiries");
export const updateInquiryStatus = (id, status) => adminRequest(`/api/admin/inquiries/${id}/status`, { method: "PATCH", body: JSON.stringify({ status }) });
export const deleteInquiry = (id) => adminRequest(`/api/admin/inquiries/${id}`, { method: "DELETE" });

export function uploadMedia(file) {
  if (file.size > 10 * 1024 * 1024) {
    const error = new Error("图片不能超过 10MB");
    error.status = 413;
    return Promise.reject(error);
  }
  const body = new FormData();
  body.append("image", file);
  return adminRequest("/api/admin/media", { method: "POST", body });
}
