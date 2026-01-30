import { useMutation, useQueryClient } from "@tanstack/react-query";
import { cancelarPericiaRequest } from "../services/periciasApi";

export function useCancelPericia(token) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: ({ id, signal }) => cancelarPericiaRequest(id, { token, signal }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pericias"] });
    },
  });
}
