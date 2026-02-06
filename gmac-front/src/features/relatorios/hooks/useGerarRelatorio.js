import { useQuery } from "@tanstack/react-query";
import { relatorioRequest } from "../services/relatorioApi";

export function useRelatorioDashboard({ ano, tipo, valor, token }) {
  return useQuery({
    queryKey: ["relatorio-dashboard", ano, tipo, valor],
    queryFn: ({ signal }) =>
      relatorioRequest({ ano, tipo, valor, token, signal }),
    enabled: !!token && !!ano && !!tipo && valor !== undefined,
    staleTime: 30_000,
    select: (data) => {
      return data;
    },
  });
}
