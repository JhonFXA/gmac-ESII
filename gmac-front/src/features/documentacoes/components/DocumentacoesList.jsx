import { useMemo, useState } from "react";
import { useAuth } from "@/app/providers/AuthContext";
import { useNavigate } from "react-router-dom";

import { useListarDocumentacao } from "../hooks/useListarDocumentacao";
import { useGerarUrlDocumentacao } from "../hooks/useGerarUrlDocumentacao";
import { useValidacaoDocumentacaoDetails } from "@/features/documentacoes/hooks/useValidacaoDetails";

import styles from "@/features/pericias/style/pericias-list.module.css";

export default function DocumentacoesList({ search, statusDocumentacao }) {
  const { token } = useAuth();

  const navigate = useNavigate();

  const {
    data: documentacoes = [],
    isLoading,
    error,
  } = useListarDocumentacao({ token, statusDocumentacao, });

  const visualizarMutation = useGerarUrlDocumentacao(token);
function formatarDataBR(data) {
  if (!data) return "-";

  const [ano, mes, dia] = data.split("T")[0].split("-");
  return `${dia}/${mes}/${ano}`;
}


  // modal info
  const [infoOpen, setInfoOpen] = useState(false);
  const [documentacaoId, setDocumentacaoId] = useState(null);

  const {
    data: validacao,
    isLoading: infoLoading,
    error: infoError,
  } = useValidacaoDocumentacaoDetails(documentacaoId, token);

  const documentacoesFiltradas = useMemo(() => {
    const q = (search ?? "").trim().toLowerCase();

    return documentacoes.filter((d) => {
      const nome = (d.nome ?? "").toLowerCase();
      const cpf = (d.cpf ?? "");
      return !q || nome.includes(q) || cpf.includes(q);
    });
  }, [documentacoes, search]);

  return (
    <>
      {/* MODAL INFO */}
      {infoOpen && (
        <div className={`${styles.popupContainer} ${styles.infoContainer}`}>
          <div className={styles.popupHeader}>
            <p className={styles.popupTitle}>Detalhes da Documentação</p>

            <button
              type="button"
              className={styles.popupCloseBtn}
              onClick={() => {
                setInfoOpen(false);
                setDocumentacaoId(null);
              }}
            >
              <i className="fa-solid fa-xmark"></i>
            </button>
          </div>

          <div className={styles.infoBody}>
            {infoLoading && <p>Carregando informações...</p>}

            {infoError && (
              <p className={styles.errorText}>
                Erro ao carregar: {infoError.message}
              </p>
            )}

            {validacao && (
              <ul className={styles.infoList}>
                <li>
                  <strong>Usuário:</strong> {validacao.usuario}
                </li>
                <li>
                  <strong>Paciente:</strong> {validacao.paciente}
                </li>
                <li>
                  <strong>Status da Documentação:</strong>{" "}
                  {validacao.statusDocumentacao}
                </li>
                <li>
                  <strong>Status da Validação:</strong>{" "}
                  {validacao.statusValidacaoDocumentacao}
                </li>
                <li>
                  <strong>Observação:</strong>{" "}
                  {validacao.observacao}
                </li>
                <li>
                  <strong>Data:</strong>{" "}
                  {formatarDataBR(validacao.data)}
                </li>
              </ul>
            )}
          </div>
        </div>
      )}

      <div className={styles.scrollList}>
        <ul className={styles.listSection}>
          {isLoading && (
            <li className={styles.emptyRow}>Carregando...</li>
          )}

          {error && (
            <li className={styles.emptyRow}>
              Erro: {error.message}
            </li>
          )}

          {!isLoading && !error && documentacoesFiltradas.length === 0 && (
            <li className={styles.listItem}>
              <div className={styles.itemInfo}>
                <p>Nenhuma documentação encontrada.</p>
              </div>
            </li>
          )}

          {documentacoesFiltradas.map((d) => (
            <li className={styles.listItem} key={d.id}>
              <div className={styles.itemInfo}>
                <p>{d.nome ?? "-"}</p>
                <p>{d.cpf ?? "-"}</p>
                <p
                  className={
                    styles[`status${(d.status ?? "").toLowerCase()}`]
                  }
                >
                  {d.status ?? "-"}
                </p>
                <p>
                  {d.dataEnvio
                    ? new Date(d.dataEnvio).toLocaleDateString("pt-BR")
                    : "-"}
                </p>
              </div>

              <div className={styles.itemBtns}>
                {d.status === "PENDENTE" ? (
                  <button
                    type="button"
                    className={styles.viewBtn}
                    title="Validar Documentação"
                    disabled={visualizarMutation.isPending}
                    onClick={() =>
                      navigate(`/painel-principal/validar-documentacoes/${d.id}`)
                    }
                  >
                    <i className="fa-solid fa-magnifying-glass"></i>
                  </button>
                ) : (
                  <button
                    type="button"
                    className={styles.infoBtn}
                    title="Ver detalhes da documentação"
                    onClick={() => {
                      setDocumentacaoId(d.id);
                      setInfoOpen(true);
                    }}
                  >
                    <i className="fa-solid fa-circle-info"></i>
                  </button>
                )}
              </div>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}
