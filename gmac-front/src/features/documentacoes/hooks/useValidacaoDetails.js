import { useQuery } from "@tanstack/react-query";
import { buscarValidacaoDocumentacaoPorId } from "../services/documentacoesApi";

export function useValidacaoDocumentacaoDetails(id, token) {
  return useQuery({
    queryKey: ["documentacao", id],
    enabled: !!id && !!token,
    queryFn: ({ signal }) => buscarValidacaoDocumentacaoPorId(id, { token, signal }),
  });
}
