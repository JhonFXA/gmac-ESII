import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createUserRequest } from "../services/usersApi";

export function useCreateUser(token) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: ({ payload, signal }) => createUserRequest(payload, { token, signal }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["users"] });
    },
  });
}
