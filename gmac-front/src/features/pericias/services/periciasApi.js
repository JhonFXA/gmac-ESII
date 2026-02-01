import { http } from "@/services/api/http";

export function listPericiasRequest({ token, signal } = {}) {
  return http("/pericia/listar", { method: "GET", token, signal });
}

export function cancelarPericiaRequest(id, { token, signal } = {}) {
  return http(`/pericia/${encodeURIComponent(id)}/cancelar`, {
    method: "PUT",
    token,
    signal,
  });
}

export function remarcarPericiaRequest(id, novaDataISO, { token, signal } = {}) {
  const dataFormatada = novaDataISO.replace("T", " ") + ":00"; // "yyyy-MM-dd HH:mm:ss"

  return http(`/pericia/${encodeURIComponent(id)}/remarcar`, {
    method: "PUT",
    token,
    signal,
    body: JSON.stringify({ data: dataFormatada }),
  });
}

export function visualizarDocumentacao(id, { token, signal } = {}) {
  return http(`/documentacao/url/${encodeURIComponent(id)}`, {
    method: "GET",
    token,
    signal,
  });
}

export function normalizePericiasResponse(data) {
  const lista = Array.isArray(data) ? data : data?.pericias ?? [];
  return lista;
}
