import { useQuery } from "@tanstack/react-query";
import { listPericiasRequest, normalizePericiasResponse } from "../services/periciasApi";

export function usePericias(token) {
  return useQuery({
    queryKey: ["pericias"],
    queryFn: ({ signal }) => listPericiasRequest({ token, signal }),
    enabled: !!token,
    staleTime: 30_000,
    select: normalizePericiasResponse,
  });
}
