import { useMutation } from "@tanstack/react-query";
import { validarDocumentacaoRequest } from "../services/documentacoesApi";
import { useToast } from "@/app/providers/ToastProvider";

export function useValidarDocumentacao(token) {
  const toast = useToast();

  return useMutation({
    mutationFn: ({ payload, signal }) =>
      validarDocumentacaoRequest(payload, { token, signal }),

    onSuccess: () => {
      toast.success("Documentação validada com sucesso!");
    },

    onError: (error) => {
      toast.error(
        error?.message ||
        "Erro ao validar a documentação."
      );
    }
  });
}
