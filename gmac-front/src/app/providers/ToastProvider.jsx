import { createContext, useCallback, useContext, useMemo, useRef, useState } from "react";

const ToastContext = createContext(null);

function uid() {
  return Math.random().toString(16).slice(2) + Date.now().toString(16);
}

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const timeoutsRef = useRef(new Map());

  const removeNow = useCallback((id) => {
    const t = timeoutsRef.current.get(id);
    if (t) clearTimeout(t);
    timeoutsRef.current.delete(id);

    setToasts((prev) => prev.filter((x) => x.id !== id));
  }, []);

  const remove = useCallback((id) => {
    setToasts((prev) =>
      prev.map((t) => (t.id === id ? { ...t, closing: true } : t))
    );
  }, []);

  const push = useCallback(
    ({ type = "info", message = "", duration = 5000 } = {}) => {
      const id = uid();

      setToasts((prev) => [
        ...prev,
        { id, type, message, closing: false },
      ]);

      if (duration !== Infinity && duration > 0) {
        const timeoutId = setTimeout(() => {
          // dispara animação de saída (não remove direto)
          remove(id);
        }, duration);

        timeoutsRef.current.set(id, timeoutId);
      }

      return id;
    },
    [remove]
  );

  const api = useMemo(
    () => ({
      toasts,
      push,
      remove,     // inicia saída (closing=true)
      removeNow,  // remove (após animação)
      success: (message, opts) => push({ type: "success", message, ...opts }),
      error: (message, opts) => push({ type: "error", message, ...opts }),
      info: (message, opts) => push({ type: "info", message, ...opts }),
      warning: (message, opts) => push({ type: "warning", message, ...opts }),
    }),
    [toasts, push, remove, removeNow]
  );

  return <ToastContext.Provider value={api}>{children}</ToastContext.Provider>;
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) {
    throw new Error("useToast deve ser usado dentro de <ToastProvider>");
  }
  return ctx;
}
