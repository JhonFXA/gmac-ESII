import { useQuery } from "@tanstack/react-query";
import {
  listPericiasRequest,
  normalizePericiasResponse,
} from "../services/periciasApi";

export function usePericias({ token, statusPericia }) {
  return useQuery({
    queryKey: ["pericias", statusPericia], 
    queryFn: ({ signal }) =>
      listPericiasRequest({
        token,
        statusPericia,
        signal,
      }),
    enabled: !!token,
    staleTime: 30_000,
    select: normalizePericiasResponse,
  });
}
