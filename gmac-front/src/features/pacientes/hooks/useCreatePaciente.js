import { useMutation } from "@tanstack/react-query";
import { createPacienteRequest } from "../services/pacientesApi";
import { useToast } from "@/app/providers/ToastProvider";

export function useCreatePaciente(token) {
  const toast = useToast();
  return useMutation({
    mutationFn: ({ payload, signal }) =>
      createPacienteRequest(payload, { token, signal }),
    onSuccess: () => {
      toast.success("Paciente criado com sucesso!");
    },
    onError: (error) => {
      toast.error(error?.message || "Erro ao criar paciente.");
    }
  });
}
