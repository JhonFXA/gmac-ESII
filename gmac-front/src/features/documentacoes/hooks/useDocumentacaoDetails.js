import { useQuery } from "@tanstack/react-query";
import { buscarDocumentacaoPorId } from "../services/documentacoesApi";

export function useDocumentacaoDetails(id, token) {
  return useQuery({
    queryKey: ["documentacao", id],
    enabled: !!id && !!token,
    queryFn: ({ signal }) => buscarDocumentacaoPorId(id, { token, signal }),
  });
}
