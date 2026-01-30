import { useQuery } from "@tanstack/react-query";
import { listUsersRequest, normalizeUsuariosResponse } from "../services/usersApi";

export function useUsers(token) {
  return useQuery({
    queryKey: ["users"],
    queryFn: ({ signal }) => listUsersRequest({ token, signal }),
    enabled: !!token,
    staleTime: 30_000,
    select: normalizeUsuariosResponse, // ✅ já chega array
  });
}
