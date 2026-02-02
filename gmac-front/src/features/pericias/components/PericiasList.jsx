import { useMemo, useState } from "react";
import { useAuth } from "@/app/providers/AuthContext";

import { usePericias } from "../hooks/usePericias";
import { useCancelPericia } from "../hooks/useCancelPericia";
import { useRemarcarPericia } from "../hooks/useRemarcarPericia";
import { useGerarUrlDocumentacao } from "../../documentacoes/hooks/useGerarUrlDocumentacao";


import styles from "../style/pericias-list.module.css";

function formatarDataHoraBR(dataISO) {
  if (!dataISO) return "-";
  const d = new Date(dataISO);
  if (Number.isNaN(d.getTime())) return "-";
  return d.toLocaleString("pt-BR");
}

const MOCK_PERICIAS = [
  {
    id: 1,
    nomePaciente: "João Silva",
    nomeMedico: "Dra. Mariana Costa",
    statusPericia: "AGENDADA",
    data: "2026-09-12T15:25:00",
  },
  {
    id: 2,
    nomePaciente: "Carla Souza",
    nomeMedico: "Dr. Roberto Lima",
    statusPericia: "PENDENTE",
    data: "2026-09-13T10:00:00",
  },
  {
    id: 3,
    nomePaciente: "Lucas Ferreira",
    nomeMedico: "Dr. André Gomes",
    statusPericia: "CANCELADA",
    data: "2026-09-14T09:40:00",
  },
];

export default function PericiasList({ search }) {
  const { token,perfil } = useAuth();

  const { data: pericias = [], isLoading, error } = usePericias(token);
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

  const periciasFiltradas = useMemo(() => {
    const q = (search ?? "").trim().toLowerCase();

    const source = pericias.length > 0 ? pericias : MOCK_PERICIAS;

    return source.filter((p) => {
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

            {cancelMutation.isError && (
              <p className={styles.errorText}>{cancelMutation.error.message}</p>
            )}
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

          {remarcarMutation.isError && (
            <p className={styles.errorText}>{remarcarMutation.error.message}</p>
          )}
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
                {perfil === "MEDICO" && (
                <button
                  type="button"
                  className={styles.viewBtn}
                  title="Ver Documentação"
                  disabled={visualizarMutation.isPending}
                  onClick={() => visualizarMutation.mutate({ id: p.idDocumentacao })}
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
              </div>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}
