import { useMutation } from "@tanstack/react-query";
import { updatePacienteRequest } from "../services/pacientesApi";
import { useToast } from "@/app/providers/ToastProvider";

export function useUpdatePaciente(token, cpfAtual) {
  const toast = useToast();
  return useMutation({
    mutationFn: ({ payload, signal }) =>
      updatePacienteRequest(payload, { token, signal, cpfAtual }),
    onSuccess: () => {
      toast.success("Paciente editado com sucesso!");
    },
    onError: (error) => {
      toast.error(error?.message || "Erro ao editar paciente.");
    }
  });
}