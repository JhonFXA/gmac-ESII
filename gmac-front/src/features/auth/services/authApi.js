import { http } from "@/services/api/http";

export function loginRequest({ login, senha }, { signal } = {}) {
  return http("/auth/login", {
    method: "POST",
    body: JSON.stringify({ login, senha }),
    signal,
  });
}
