import { useMutation } from "@tanstack/react-query";
import { visualizarDocumentacao } from "../services/periciasApi";

export function useGerarUrlDocumentacao(token) {
  return useMutation({
    mutationFn: ({ id, signal }) =>
      visualizarDocumentacao(id, { token, signal }),
    onSuccess: (data) => {
      // Aqui você pode fazer algo com a URL sem abrir a página
      console.log("URL recebida com sucesso:", data);
    },
    onError: (error) => {
      console.error("Erro ao obter URL da documentação:", error);
      alert("Não foi possível carregar o documento.");
    },
  });
}