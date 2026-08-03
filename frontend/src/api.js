const API_BASE = import.meta.env.VITE_API_BASE_URL || "";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  });

  const data = await response.json().catch(() => null);
  if (!response.ok) {
    const error = new Error(data?.message || "服务暂时不可用，请稍后再试");
    error.status = response.status;
    error.fields = data?.fields || {};
    throw error;
  }
  return data;
}

export const fetchSiteContent = () => request("/api/site", { cache: "no-store" });

export const createInquiry = (payload) =>
  request("/api/inquiries", {
    method: "POST",
    body: JSON.stringify(payload),
  });
