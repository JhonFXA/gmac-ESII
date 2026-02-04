import { useMutation } from "@tanstack/react-query";
import { validarDocumentacaoRequest } from "../services/documentacoesApi";

export function useValidarDocumentacao(token) {
  return useMutation({
    mutationFn: ({ payload, signal }) =>
      validarDocumentacaoRequest(payload, { token, signal }),
  });
}