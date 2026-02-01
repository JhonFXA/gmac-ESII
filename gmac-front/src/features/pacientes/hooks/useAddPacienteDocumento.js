import { useMutation } from "@tanstack/react-query";
import { addPacienteDocumentoRequest } from "../services/pacientesApi";

export function useAddPacienteDocumento(token, cpf) {
  return useMutation({
    mutationFn: ({ payload, signal }) =>
      addPacienteDocumentoRequest(payload, { token, signal, cpf }),
  });
}