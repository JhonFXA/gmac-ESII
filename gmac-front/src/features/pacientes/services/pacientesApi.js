import { http } from "@/services/api/http";

export function createPacienteRequest(formData, { token, signal } = {}) {
  return http("/paciente/cadastrar", {
    method: "POST",
    token,
    signal,
    body: formData,
  });
}

export function listPacientesRequest({ token, signal } = {}) {
  return http("/paciente/listar", { method: "GET", token, signal });
}

export function searchPaciente(cpf, { token, signal } = {}) {
  return http(`/paciente/buscar/${encodeURIComponent(cpf)}`, {
    method: "GET",
    token,
    signal,
  });
}

export function updatePacienteRequest(payload, { token, signal, cpfAtual } = {}) {
  return http(`/paciente/alterar/${cpfAtual}`, {
    method: "PUT",
    token,
    signal,
    body: payload,
  });
}

export function addPacienteEnderecoRequest(payload, { token, signal, cpf } = {}) {
  return http(`/paciente/${cpf}/endereco`, {
    method: "POST",
    token,
    signal,
    body: payload,
  });
}

export function addPacienteDocumentoRequest(formData, { token, signal, cpf } = {}) {
  return http(`/paciente/${cpf}/documento`, {
    method: "POST",
    token,
    signal,
    body: formData,
  });
}