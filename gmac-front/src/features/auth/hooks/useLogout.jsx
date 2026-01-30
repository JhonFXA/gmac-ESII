import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useAuth } from "@/app/providers/AuthContext";

export function useLogout({ redirectTo = "/" } = {}) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { logout } = useAuth();

  return useCallback(() => {
    // 1) limpa todo cache do React Query
    queryClient.clear();

    // 2) limpa token/perfil do AuthContext + localStorage
    logout();

    // 3) redireciona para login
    navigate(redirectTo, { replace: true });
  }, [queryClient, logout, navigate, redirectTo]);
}
