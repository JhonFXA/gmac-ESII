import { http } from "@/services/api/http";

export function visualizarDocumentacao(id, { token, signal } = {}) {
  return http(`/documentacao/url/${encodeURIComponent(id)}`, {
    method: "GET",
    token,
    signal,
  });
}

export function listarDocumentacoesRequest({ token, signal } = {}) {
  return http(`/documentacao/buscar`, {
    method: "GET",
    token,
    signal,
  });
}

export function normalizeDocumentacoesResponse(data) {
  const lista = Array.isArray(data) ? data : data?.documentacoes ?? [];
  return lista;
}


