export const API_BASE = "http://localhost:8080";

export async function apiRequest(path, method = "GET", body = null) {
  const options = { method, headers: { "Content-Type": "application/json" } };
  if (body) options.body = JSON.stringify(body);

  const response = await fetch(`${API_BASE}${path}`, options);
  if (!response.ok) {
    const err = await response.text();
    throw new Error(err || `Ошибка запроса ${method} ${path}`);
  }
  return response.status === 204 ? null : response.json();
}
