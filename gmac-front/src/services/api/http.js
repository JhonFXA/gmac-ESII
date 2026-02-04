const API_BASE_URL = import.meta.env.VITE_API_URL;

let unauthorizedHandler = null;
let isHandlingUnauthorized = false;

export function setUnauthorizedHandler(fn) {
  unauthorizedHandler = typeof fn === "function" ? fn : null;
}

export async function http(path, { token, signal, headers, body, ...options } = {}) {
  const isFormData =
    typeof FormData !== "undefined" && body instanceof FormData;

  const mergedHeaders = {
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...headers,
  };

  if (!isFormData && !("Content-Type" in mergedHeaders)) {
    mergedHeaders["Content-Type"] = "application/json";
  }

  const finalBody =
    body == null
      ? undefined
      : isFormData
      ? body
      : typeof body === "string"
      ? body
      : JSON.stringify(body);

  let response;

  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      signal,
      body: finalBody,
      headers: mergedHeaders,
    });
  } catch (err) {
    const isAbort = err?.name === "AbortError";
    if (isAbort) throw err;

    throw new Error("Não foi possível conectar ao servidor. Tente novamente.");
  }

  const text = await response.text();
  const json = text ? safeJson(text) : null;
  const data = json ?? (text || null);

  if (response.status === 401) {
    const shouldAutoLogout = path !== "/auth/login" && !!unauthorizedHandler;

    if (shouldAutoLogout && !isHandlingUnauthorized) {
      isHandlingUnauthorized = true;
      try {
        unauthorizedHandler();
      } finally {
        setTimeout(() => {
          isHandlingUnauthorized = false;
        }, 0);
      }
    }

    const message =
      (json && (json.message || json.error || json.Erro || json.erro)) ||
      "Sessão expirada. Faça login novamente.";
    throw new Error(message);
  }

  if (!response.ok) {
    const message =
      (json && (json.message || json.error || json.Erro || json.erro)) ||
      (typeof data === "string" && data) ||
      `HTTP ${response.status}`;
    throw new Error(message);
  }

  return data;
}


function safeJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}
