import styles from "./toast-host.module.css";

export default function ToastPreview() {

  let type = "success";
  return (
    <div className={styles.container}>
      <div className={`${styles.toast} ${styles[type]}`}>
        <div className={styles.text}>
          {type === "success" && <i className="fas fa-check"></i>}
          {type === "error" && <i className="fas fa-exclamation-triangle"></i>}
          {type === "info" && <i className="fas fa-info-circle"></i>}
          {type === "warning" && <i className="fas fa-exclamation-circle"></i>}
          Mensagem de teste!
        </div>
        <button
          type="button"
          className={styles.close}
          aria-label="Fechar"
        >
          ×
        </button>
      </div>
    </div>
  );
}
