import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateUserRequest } from "../services/usersApi";
import { useToast } from "@/app/providers/ToastProvider";

export function useUpdateUser(token, cpf) {
  const qc = useQueryClient();
  const toast = useToast();

  return useMutation({
    mutationFn: ({ payload, signal }) =>
      updateUserRequest(cpf, payload, { token, signal }),

    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["usuario", cpf] });
      qc.invalidateQueries({ queryKey: ["users"] });

      toast.success("Usuário editado com sucesso!");
    },

    onError: (error) => {
      toast.error(error.message || "Erro ao editar usuário");
    },
  });
}
