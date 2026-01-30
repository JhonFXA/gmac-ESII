import { useEffect } from "react";
import { setUnauthorizedHandler } from "@/services/api/http";
import { useLogout } from "@/features/auth/hooks/useLogout";

export default function ApiAuthBridge({ children }) {
  const logout = useLogout({ redirectTo: "/" });

  useEffect(() => {
    setUnauthorizedHandler(logout);
    return () => setUnauthorizedHandler(null);
  }, [logout]);

  return children;
}
