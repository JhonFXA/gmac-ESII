import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createUserRequest } from "../services/usersApi";
import { useToast } from "@/app/providers/ToastProvider";

export function useCreateUser(token) {
  const qc = useQueryClient();
  const toast = useToast();

  return useMutation({
    mutationFn: ({ payload, signal }) =>
      createUserRequest(payload, { token, signal }),

    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["users"] });
      toast.success("Usuário criado com sucesso!");
    },

    onError: (error) => {
      toast.error(error?.message || "Erro ao criar usuário.");
    },
  });
}
