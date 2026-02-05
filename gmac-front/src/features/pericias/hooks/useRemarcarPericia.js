import { useMutation, useQueryClient } from "@tanstack/react-query";
import { remarcarPericiaRequest } from "../services/periciasApi";
import { useToast } from "@/app/providers/ToastProvider";

export function useRemarcarPericia(token) {
  const qc = useQueryClient();
  const toast = useToast();

  return useMutation({
    mutationFn: ({ id, novaDataISO, signal }) =>
      remarcarPericiaRequest(id, novaDataISO, { token, signal }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pericias"] });
      toast.success("Perícia remarcada com sucesso!");
    },
    onError: (error) => {
      toast.error(error?.message || "Erro ao remarcar perícia.");
    }
  });
}
