import { useMutation } from "@tanstack/react-query";
import { createPacienteRequest } from "../services/pacientesApi";

export function useCreatePaciente(token) {
  return useMutation({
    mutationFn: ({ payload, signal }) =>
      createPacienteRequest(payload, { token, signal }),
  });
}
