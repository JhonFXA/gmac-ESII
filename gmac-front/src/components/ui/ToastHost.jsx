import styles from "./toast-host.module.css";
import { useToast } from "@/app/providers/ToastProvider";

export default function ToastHost() {
  const { toasts, remove, removeNow } = useToast();

  return (
    <div className={styles.container} aria-live="polite" aria-relevant="additions">
      {toasts.map((t) => {
        const isClosing = !!t.closing;

        return (
          <div
            key={t.id}
            className={`${styles.toast} ${styles[t.type]} ${
              isClosing ? styles.exit : styles.enter
            }`}
            onAnimationEnd={() => {
              if (isClosing) removeNow(t.id);
            }}
          >
            <div className={styles.text}>
              {t.type === "success" && <i className="fas fa-check"></i>}
              {t.type === "error" && <i className="fas fa-exclamation-triangle"></i>}
              {t.type === "info" && <i className="fas fa-info-circle"></i>}
              {t.type === "warning" && <i className="fas fa-exclamation-circle"></i>}
              {t.message}
            </div>

            <button
              type="button"
              className={styles.close}
              onClick={() => remove(t.id)}
              aria-label="Fechar"
              title="Fechar"
              disabled={isClosing}
            >
              ×
            </button>
          </div>
        );
      })}
    </div>
  );
}
