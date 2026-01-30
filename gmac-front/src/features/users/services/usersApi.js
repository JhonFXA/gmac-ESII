import { http } from "@/services/api/http";

export function listUsersRequest({ token, signal } = {}) {
  return http("/usuario/listar", { method: "GET", token, signal });
}

export function buscarUsuarioPorCpf(cpf, { token, signal } = {}) {
  return http(`/usuario/buscar/${encodeURIComponent(cpf)}`, {
    method: "GET",
    token,
    signal,
  });
}

export function createUserRequest(payload, { token, signal } = {}) {
  return http("/usuario/registro", {
    method: "POST",
    token,
    body: JSON.stringify(payload),
    signal,
  });
}

export function updateUserRequest(cpf, payload, { token, signal } = {}) {
  return http(`/usuario/alterar/${encodeURIComponent(cpf)}`, {
    method: "PUT",
    token,
    signal,
    body: JSON.stringify(payload),
  });
}

export function normalizeUsuariosResponse(data) {
  return Array.isArray(data) ? data : data?.usuarios ?? [];
}