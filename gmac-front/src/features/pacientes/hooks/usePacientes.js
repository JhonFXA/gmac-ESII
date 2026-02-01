import { useQuery } from "@tanstack/react-query";
import { listPacientesRequest } from "../services/pacientesApi";
import { normalizeUsuariosResponse } from "@/features/users/services/usersApi";

export function usePacientes(token) {
  return useQuery({
    queryKey: ["pacientes"],
    queryFn: ({ signal }) => listPacientesRequest({ token, signal }),
    enabled: !!token,
    staleTime: 30_000,
    select: normalizeUsuariosResponse, // ✅ já chega array
  });
}
