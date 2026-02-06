import { useMemo, useState } from "react";
import { useAuth } from "@/app/providers/AuthContext";

import { usePericias } from "../hooks/usePericias";
import { useCancelPericia } from "../hooks/useCancelPericia";
import { useRemarcarPericia } from "../hooks/useRemarcarPericia";
import { useGerarUrlDocumentacao } from "../../documentacoes/hooks/useGerarUrlDocumentacao";
import { useValidacaoDocumentacaoDetails } from "@/features/documentacoes/hooks/useValidacaoDetails";

import { useNavigate } from "react-router-dom";

import styles from "../style/pericias-list.module.css";

function formatarDataHoraBR(dataISO) {
  if (!dataISO) return "-";
  const d = new Date(dataISO);
  if (Number.isNaN(d.getTime())) return "-";
  return d.toLocaleString("pt-BR");
}

export default function PericiasList({ search, statusPericia }) {
  const { token, perfil } = useAuth();

    const navigate = useNavigate();

  const { data: pericias = [], isLoading, error } = usePericias({
    token,
    statusPericia,
  });

  const cancelMutation = useCancelPericia(token);
  const remarcarMutation = useRemarcarPericia(token);
  const visualizarMutation = useGerarUrlDocumentacao(token);

  // modal remarcar
  const [editOpen, setEditOpen] = useState(false);
  const [periciaParaEditar, setPericiaParaEditar] = useState(null);
  const [novaData, setNovaData] = useState("");

  // modal cancelar
  const [cancelOpen, setCancelOpen] = useState(false);
  const [periciaParaCancelar, setPericiaParaCancelar] = useState(null);
  const [confirmText, setConfirmText] = useState("");

  // modal info 
  const [infoOpen, setInfoOpen] = useState(false);
  const [documentacaoId, setDocumentacaoId] = useState(null);


  const {
    data: validacao,
    isLoading: infoLoading,
    error: infoError,
  } = useValidacaoDocumentacaoDetails(documentacaoId, token);

  const periciasFiltradas = useMemo(() => {
    const q = (search ?? "").trim().toLowerCase();
    
    return pericias.filter((p) => {
      const paciente = (p.nomePaciente ?? "").toLowerCase();
      const medico = (p.nomeMedico ?? "").toLowerCase();

      return !q || paciente.includes(q) || medico.includes(q);
    });
  }, [pericias, search]);

  async function confirmarCancelamento() {
    if (confirmText !== "CANCELAR PERÍCIA" || !periciaParaCancelar) return;
    try {
      await cancelMutation.mutateAsync({ id: periciaParaCancelar.id });
      setCancelOpen(false);
    } catch {
      // erro já fica em cancelMutation.error
    }
  }

  async function confirmarRemarcacao() {
    if (!periciaParaEditar || !novaData) return;
    try {
      await remarcarMutation.mutateAsync({
        id: periciaParaEditar.id,
        novaDataISO: novaData,
      });
      setEditOpen(false);
    } catch {
      // erro já fica em remarcarMutation.error
    }
  }

  return (
    <>

      {/* MODAL CANCELAR */}
      {cancelOpen && (
        <div className={`${styles.popupContainer} ${styles.cancelContainer}`}>
          <div className={styles.popupHeader}>
            <p className={styles.popupTitle}>Cancelar Perícia</p>

            <button
              type="button"
              className={styles.popupCloseBtn}
              onClick={() => setCancelOpen(false)}
            >
              <i className="fa-solid fa-xmark"></i>
            </button>
          </div>

          <div className={styles.cancelBody}>
            <p>
              Digite <strong>CANCELAR PERÍCIA</strong> para confirmar:
            </p>

            <input
              className={styles.confirmInput}
              value={confirmText}
              onChange={(e) => setConfirmText(e.target.value)}
            />

            <button
              type="button"
              className={styles.confirmDeleteBtn}
              onClick={confirmarCancelamento}
              disabled={
                confirmText !== "CANCELAR PERÍCIA" || cancelMutation.isPending
              }
            >
              {cancelMutation.isPending
                ? "Cancelando..."
                : "Confirmar Cancelamento"}
            </button>


          </div>
        </div>
      )}

      {/* MODAL REMARCAR */}
      {editOpen && (
        <div className={`${styles.popupContainer} ${styles.editContainer}`}>
          <div className={styles.popupHeader}>
            <p className={styles.popupTitle}>Remarcar Perícia</p>

            <button
              type="button"
              className={styles.popupCloseBtn}
              onClick={() => setEditOpen(false)}
            >
              <i className="fa-solid fa-xmark"></i>
            </button>
          </div>

          <div className={styles.periciaInfo}>
            <input
              type="datetime-local"
              className={styles.newDateInput}
              value={novaData}
              onChange={(e) => setNovaData(e.target.value)}
            />

            <button
              type="button"
              className={styles.confirmDateBtn}
              onClick={confirmarRemarcacao}
              disabled={!novaData || remarcarMutation.isPending}
            >
              {remarcarMutation.isPending ? "Salvando..." : "Salvar Nova Data"}
            </button>
          </div>
        </div>
      )}
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
                  <strong>Status da Validação:</strong>{" "}
                  {validacao.statusValidacaoDocumentacao}
                </li>
                <li>
                  <strong>Status da Documentação:</strong>{" "}
                  {validacao.statusDocumentacao}
                </li>
                <li>
                  <strong>Observação:</strong>{" "}
                  {validacao.observacao || "-"}
                </li>
                <li>
                  <strong>Data:</strong>{" "}
                  {validacao.data
                    ? new Date(validacao.data).toLocaleDateString("pt-BR")
                    : "-"}
                </li>
              </ul>
            )}
          </div>
        </div>
      )}


      <div className={styles.scrollList}>
        <ul className={styles.listSection}>
          {isLoading && <li className={styles.emptyRow}>Carregando...</li>}

          {error && <li className={styles.emptyRow}>Erro: {error.message}</li>}

          {!isLoading && !error && periciasFiltradas.length === 0 && (
            <li className={styles.listItem}>
              <div className={styles.itemInfo}>
                <p>Nenhuma perícia encontrada.</p>
              </div>
            </li>
          )}

          {periciasFiltradas.map((p) => (
            <li className={styles.listItem} key={p.id}>
              <div className={styles.itemInfo}>
                <p>{p.nomePaciente ?? "-"}</p>
                <p>{p.nomeMedico ?? "-"}</p>
                <p
                  className={
                    styles[`status${(p.statusPericia ?? "").toLowerCase()}`]
                  }
                >
                  {p.statusPericia ?? "-"}
                </p>
                <p>{formatarDataHoraBR(p.data)}</p>
              </div>

              <div className={styles.itemBtns}>
                

                {p.statusPericia === "AGENDADA" ? (
                  <>
                  {perfil === "MEDICO" && (
                  <button
                    type="button"
                    className={styles.viewBtn}
                    title="Ver Documentação"
                    disabled={visualizarMutation.isPending}
                    onClick={() =>
                      navigate(`/painel-principal/validar-documentacoes/${p.idDocumentacao}`)
                    }
                  >
                    <i className="fa-solid fa-magnifying-glass"></i>
                  </button>
                )}
                    <button
                      type="button"
                      className={styles.editBtn}
                      title="Remarcar"
                      onClick={() => {
                        setPericiaParaEditar(p);
                        setNovaData(p.data?.substring(0, 16) || "");
                        setEditOpen(true);
                      }}
                    >
                      <i className="fa-solid fa-pen"></i>
                    </button>

                    <button
                      type="button"
                      className={styles.deleteBtn}
                      title="Cancelar"
                      onClick={() => {
                        setPericiaParaCancelar(p);
                        setConfirmText("");
                        setCancelOpen(true);
                      }}
                    >
                      <i className="fa-solid fa-circle-xmark"></i>
                    </button>
                  </>
                ) : (
                  <button
                    type="button"
                    className={styles.infoBtn}
                    title="Ver detalhes da perícia"
                    onClick={() => {
                      setDocumentacaoId(p.idDocumentacao);
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
