import { useMutation, useQueryClient } from "@tanstack/react-query";
import { cancelarPericiaRequest } from "../services/periciasApi";
import { useToast } from "@/app/providers/ToastProvider";

export function useCancelPericia(token) {
  const qc = useQueryClient();
  const toast = useToast();

  return useMutation({
    mutationFn: ({ id, signal }) => cancelarPericiaRequest(id, { token, signal }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pericias"] });
      toast.success("Perícia cancelada com sucesso!");
    },
    onError: (error) => {
      toast.error(error?.message || "Erro ao cancelar perícia.");
    }
  });
}
