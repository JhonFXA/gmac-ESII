import { useMutation } from "@tanstack/react-query";
import { updatePacienteRequest } from "../services/pacientesApi";

export function useUpdatePaciente(token, cpfAtual) {
  return useMutation({
    mutationFn: ({ payload, signal }) =>
      updatePacienteRequest(payload, { token, signal, cpfAtual }),
  });
}