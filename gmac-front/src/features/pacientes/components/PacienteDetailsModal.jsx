import { useAuth } from "@/app/providers/AuthContext";
import { usePacienteDetails } from "../hooks/usePacienteDetails";
import {
  calcularIdade,
  formatarDataBR,
} from "@/features/users/utils/userFormatters";

import styles from "@/features/users/style/user-details-modal.module.css";

export default function PacienteDetailsModal({ cpf, onClose }) {
  const { token } = useAuth();

  if (!cpf) return null;

  const { data: paciente, isLoading, error } = usePacienteDetails(cpf, token);

  const enderecoPrincipal = paciente?.enderecos?.[0]; // primeiro endereço

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
              <strong>Nome Completo:</strong> {paciente?.nome ?? "-"}
            </p>
            <p>
              <strong>Email:</strong> {paciente?.email ?? "-"}
            </p>
            <p>
              <strong>CPF:</strong> {paciente?.cpf ?? "-"}
            </p>
            <p>
              <strong>Idade:</strong>{" "}
              {paciente?.dataNascimento
                ? `${calcularIdade(paciente.dataNascimento)} anos`
                : "-"}
            </p>
            <p>
              <strong>Data de Nascimento:</strong>{" "}
              {paciente?.dataNascimento
                ? formatarDataBR(paciente.dataNascimento)
                : "-"}
            </p>
            <p>
              <strong>Telefone:</strong> {paciente?.telefone ?? "-"}
            </p>
            <p>
              <strong>Status da Solicitação:</strong>{" "}
              {paciente?.statusSolicitacao ?? "-"}
            </p>
            <p>
              <strong>Sexo:</strong> {paciente?.sexo ?? "-"}
            </p>
            <p>
              <strong>Estado Civil:</strong> {paciente?.estadoCivil ?? "-"}
            </p>

            {/* 📍 ENDEREÇO */}
            <div className={styles.addressSection}>
              <p className={styles.sectionTitle}>
                <strong>Endereço</strong>
              </p>

              {enderecoPrincipal ? (
                <>
                  <p>
                    <strong>Logradouro:</strong>{" "}
                    {enderecoPrincipal.logradouro}, {enderecoPrincipal.numero}
                  </p>

                  {enderecoPrincipal.complemento && (
                    <p>
                      <strong>Complemento:</strong>{" "}
                      {enderecoPrincipal.complemento}
                    </p>
                  )}

                  <p>
                    <strong>Bairro:</strong>{" "}
                    {enderecoPrincipal.bairro}
                  </p>
                  <p>
                    <strong>Cidade / UF:</strong>{" "}
                    {enderecoPrincipal.cidade} /{" "}
                    {enderecoPrincipal.estado}
                  </p>
                  <p>
                    <strong>CEP:</strong> {enderecoPrincipal.cep}
                  </p>
                </>
              ) : (
                <p>- Endereço não informado -</p>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}
