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

  const enderecos = paciente?.enderecos ?? [];

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

            <div className={styles.addressSection}>
              <p className={styles.sectionTitle}>
                <strong>Endereços</strong>
              </p>

              {enderecos.length > 0 ? (
                enderecos.map((end, idx) => (
                  <div
                    key={`${end.cep}-${end.logradouro}-${end.numero}-${idx}`}
                    className={styles.addressCard ?? undefined}
                    style={{
                      padding: 10,
                      border: "1px solid rgba(0,0,0,0.15)",
                      borderRadius: 8,
                      marginTop: 10,
                      background: "rgba(255,255,255,0.6)",
                    }}
                  >
                    <p>
                      <strong>Endereço {idx + 1}</strong>
                    </p>

                    <p>
                      <strong>Logradouro:</strong> {end.logradouro}, {end.numero}
                    </p>

                    {end.complemento && (
                      <p>
                        <strong>Complemento:</strong> {end.complemento}
                      </p>
                    )}

                    <p>
                      <strong>Bairro:</strong> {end.bairro}
                    </p>

                    <p>
                      <strong>Cidade / UF:</strong> {end.cidade} / {end.estado}
                    </p>

                    <p>
                      <strong>CEP:</strong> {end.cep}
                    </p>
                  </div>
                ))
              ) : (
                <p>- Nenhum endereço informado -</p>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}
