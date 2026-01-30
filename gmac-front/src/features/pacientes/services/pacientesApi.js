import { http } from "@/services/api/http";

export function createPacienteRequest(payload, { token, signal } = {}) {
  return http("/paciente/cadastrar", {
    method: "POST",
    token,
    signal,
    body: JSON.stringify(payload),
  });
}
