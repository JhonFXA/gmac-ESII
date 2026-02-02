import { useQuery } from "@tanstack/react-query";
import { listarDocumentacoesRequest, normalizeDocumentacoesResponse } from "../services/documentacoesApi";

export function useListarDocumentacao(token) {
  return useQuery({
    queryKey: ["documentacoes"],
    queryFn: ({ signal }) => listarDocumentacoesRequest({ token, signal }),
    enabled: !!token,
    staleTime: 30_000,
    select: normalizeDocumentacoesResponse,
  });
}
