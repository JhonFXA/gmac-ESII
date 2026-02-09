import { useMutation } from "@tanstack/react-query";
import { validarPericiaRequest } from "../services/periciasApi";
import { useToast } from "@/app/providers/ToastProvider";

export function useValidarPericia(token) {
  const toast = useToast();

  return useMutation({
    mutationFn: ({ payload, signal }) =>
      validarPericiaRequest(payload, { token, signal }),

    onSuccess: () => {
      toast.success("Perícia validada com sucesso!");
    },

    onError: (error) => {
      toast.error(
        error?.message ||
        "Erro ao validar a perícia."
      );
    }
  });
}
