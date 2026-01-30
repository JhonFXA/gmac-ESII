import { useMutation } from "@tanstack/react-query";
import { loginRequest } from "../services/authApi";

export function useLogin() {
  return useMutation({
    mutationFn: ({ login, senha }) => loginRequest({ login, senha }),
  });
}