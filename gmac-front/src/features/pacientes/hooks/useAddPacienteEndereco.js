import { useMutation } from "@tanstack/react-query";
import { addPacienteEnderecoRequest } from "../services/pacientesApi";

export function useAddPacienteEndereco(token, cpf) {
  return useMutation({
    mutationFn: ({ payload, signal }) =>
      addPacienteEnderecoRequest(payload, { token, signal, cpf }),
  });
}