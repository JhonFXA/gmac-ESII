import { http } from "@/services/api/http";

export function visualizarDocumentacao(id, { token, signal } = {}) {
  return http(`/documentacao/url/${encodeURIComponent(id)}`, {
    method: "GET",
    token,
    signal,
  });
}

export function listarDocumentacoesRequest({ token, statusDocumentacao, signal } = {}) {
  const params = new URLSearchParams();

  if (statusDocumentacao) {
    params.append("status", statusDocumentacao);
  }

  const query = params.toString();
  const url = query ? `/documentacao/buscar?${query}` : "/documentacao/buscar";

  return http(url, {
    method: "GET",
    token,
    signal,
  });
}

export function buscarDocumentacaoPorId(id, { token, signal } = {}) {
  return http(`/documentacao/buscar/${encodeURIComponent(id)}`, {
    method: "GET",
    token,
    signal,
  });
}

export function buscarValidacaoDocumentacaoPorId(id, { token, signal } = {}) {
  return http(`/documentacao/buscar/validacao/${encodeURIComponent(id)}`, {
    method: "GET",
    token,
    signal,
  });
}

export function normalizeDocumentacoesResponse(data) {
  const lista = Array.isArray(data) ? data : data?.documentacoes ?? [];
  return lista;
}


