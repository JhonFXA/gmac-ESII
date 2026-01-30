import { useAuth } from "@/app/providers/AuthContext";
import { useUserDetails } from "../hooks/useUserDetails";
import { calcularIdade, formatarDataBR } from "../utils/userFormatters";

import styles from "../style/user-details-modal.module.css";

export default function UserDetailsModal({ cpf, onClose }) {
  const { token } = useAuth();

  if (!cpf) return null;

  const { data: usuario, isLoading, error } = useUserDetails(cpf, token);

  return (
    <div className={styles.popupContainer} role="dialog" aria-modal="true">
      <div className={styles.popupHeader}>
        <p>Informações Adicionais</p>

        <button
          type="button"
          className={styles.popupCloseBtn}
          onClick={onClose}
          aria-label="Fechar"
        >
          <span aria-hidden="true">×</span>
        </button>
      </div>

      <div className={styles.userInfo}>
        {isLoading && <p>Carregando...</p>}

        {error && <p className={styles.errorText}>Erro: {error.message}</p>}

        {!isLoading && !error && (
          <>
            <p>
              <strong>Nome Completo:</strong> {usuario?.nome ?? "-"}
            </p>
            <p>
              <strong>Login:</strong> {usuario?.login ?? "-"}
            </p>
            <p>
              <strong>Email:</strong> {usuario?.email ?? "-"}
            </p>
            <p>
              <strong>CPF:</strong> {usuario?.cpf ?? "-"}
            </p>
            <p>
              <strong>Idade:</strong>{" "}
              {usuario?.dataNascimento ? `${calcularIdade(usuario.dataNascimento)} anos` : "-"}
            </p>
            <p>
              <strong>Data de Nascimento:</strong>{" "}
              {usuario?.dataNascimento ? formatarDataBR(usuario.dataNascimento) : "-"}
            </p>
          </>
        )}
      </div>
    </div>
  );
}
