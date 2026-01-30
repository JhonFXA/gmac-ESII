import { useQuery } from "@tanstack/react-query";
import { buscarUsuarioPorCpf } from "../services/usersApi";

export function useUserDetails(cpf, token) {
  return useQuery({
    queryKey: ["usuario", cpf],
    queryFn: ({ signal }) => buscarUsuarioPorCpf(cpf, { token, signal }),
    enabled: !!cpf && !!token,
  });
}
