import { http } from "@/services/api/http";

export function relatorioRequest({ ano, tipo, valor, token, signal } = {}) {
  const params = new URLSearchParams({
    ano,
    tipo,
    valor,
  });

  return http(`/relatorio/dashboard?${params.toString()}`, {
    method: "GET",
    token,
    signal,
  });
}
