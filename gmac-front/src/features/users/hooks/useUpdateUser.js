import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateUserRequest } from "../services/usersApi";

export function useUpdateUser(token, cpf) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: ({ payload, signal }) =>
      updateUserRequest(cpf, payload, { token, signal }),

    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["usuario", cpf] });
      qc.invalidateQueries({ queryKey: ["users"] });
    },
  });
}
