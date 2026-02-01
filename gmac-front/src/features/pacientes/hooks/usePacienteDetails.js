import { useQuery } from "@tanstack/react-query";
import { searchPaciente } from "../services/pacientesApi";

export function usePacienteDetails(cpf, token) {
  return useQuery({
    queryKey: ["paciente", cpf],
    queryFn: ({ signal }) => searchPaciente(cpf, { token, signal }),
    enabled: !!cpf && !!token,
  });
}
