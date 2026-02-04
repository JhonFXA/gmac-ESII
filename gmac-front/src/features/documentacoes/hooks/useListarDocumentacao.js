import { useQuery } from "@tanstack/react-query";
import { listarDocumentacoesRequest, normalizeDocumentacoesResponse } from "../services/documentacoesApi";

export function useListarDocumentacao({ token, statusDocumentacao }) {
  return useQuery({
    queryKey: ["documentacoes", statusDocumentacao],
    queryFn: ({ signal }) =>
      listarDocumentacoesRequest({ token, statusDocumentacao, signal }),
    enabled: !!token,
    staleTime: 30_000,
    select: normalizeDocumentacoesResponse,
  });
}

