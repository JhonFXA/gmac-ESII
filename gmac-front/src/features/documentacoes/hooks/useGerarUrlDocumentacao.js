import { useMutation } from "@tanstack/react-query";
import { visualizarDocumentacao } from "../../documentacoes/services/documentacoesApi";

export function useGerarUrlDocumentacao(token) {
  return useMutation({
    mutationFn: ({ id, signal }) =>
      visualizarDocumentacao(id, { token, signal }),
  }
);
}
