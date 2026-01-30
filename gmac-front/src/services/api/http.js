const API_BASE_URL = import.meta.env.VITE_API_URL;

let unauthorizedHandler = null;
let isHandlingUnauthorized = false;

/**
 * Registra uma função que será chamada quando o backend retornar 401
 * (ex: limpar cache + logout + redirect).
 */
export function setUnauthorizedHandler(fn) {
  unauthorizedHandler = typeof fn === "function" ? fn : null;
}

export async function http(path, { token, signal, headers, ...options } = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    signal,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...headers,
    },
  });

  const text = await response.text();
  const json = text ? safeJson(text) : null;
  const data = json ?? (text || null);

  // 401: sessão expirada / token inválido
  // - não dispara auto-logout no login
  // - evita disparar várias vezes seguidas
  if (response.status === 401) {
    const shouldAutoLogout =
      path !== "/auth/login" && !!unauthorizedHandler;

    if (shouldAutoLogout && !isHandlingUnauthorized) {
      isHandlingUnauthorized = true;
      try {
        unauthorizedHandler();
      } catch {
        // não quebra o app se o handler falhar
      } finally {
        // libera em breve pra não “travar” pra sempre
        setTimeout(() => {
          isHandlingUnauthorized = false;
        }, 0);
      }
    }

    // mensagem amigável (ou do backend)
    const message =
      (json && (json.message || json.error || json.Erro)) ||
      "Sessão expirada. Faça login novamente.";
    throw new Error(message);
  }

  if (!response.ok) {
    const message =
      (json && (json.message || json.error || json.Erro)) ||
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
