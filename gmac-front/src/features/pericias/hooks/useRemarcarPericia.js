import { useMutation, useQueryClient } from "@tanstack/react-query";
import { remarcarPericiaRequest } from "../services/periciasApi";

export function useRemarcarPericia(token) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: ({ id, novaDataISO, signal }) =>
      remarcarPericiaRequest(id, novaDataISO, { token, signal }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pericias"] });
    },
  });
}
