import { useEffect, useMemo, useState } from "react";
import { useAuth } from "../context/AuthContext";
import "../css/ScrollList.css";

export default function ScrollListPericia({
  search,
  loading,
  setLoading,
  erro,
  setErro,
}) {
  const API_BASE_URL = import.meta.env.VITE_API_URL;
  const { token } = useAuth();

  const [pericias, setPericias] = useState([]);

  // --- ESTADOS DE REMARCAÇÃO (EDITAR) ---
  const [editOpen, setEditOpen] = useState(false);
  const [periciaParaEditar, setPericiaParaEditar] = useState(null);
  const [novaData, setNovaData] = useState("");

  // --- ESTADOS DE CANCELAMENTO ---
  const [cancelOpen, setCancelOpen] = useState(false);
  const [periciaParaCancelar, setPericiaParaCancelar] = useState(null);
  const [confirmText, setConfirmText] = useState("");

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
    {
      id: 4,
      nomePaciente: "Beatriz Alves",
      nomeMedico: "Dra. Paula Ribeiro",
      statusPericia: "AGENDADA",
      data: "2026-09-15T14:10:00",
    }
  ];

  useEffect(() => {
    let isMounted = true;

    async function loadPericias() {
      try {
        setErro("");
        setLoading(true);

        const response = await fetch(`${API_BASE_URL}/pericia/listar`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) throw new Error(`Erro: ${response.status}`);

        const data = await response.json();

        let lista = Array.isArray(data) ? data : (data?.pericias ?? []);

        if (lista.length === 0) {
          lista = MOCK_PERICIAS;
        }

        if (isMounted) setPericias(lista);
      } catch (err) {
        if (isMounted) setErro(err.message || "Falha ao carregar perícias");
      } finally {
        if (isMounted) setLoading(false);
      }
    }

    loadPericias();

    return () => {
      isMounted = false;
    };
  }, [API_BASE_URL, token, setErro, setLoading]);

  // PUT /pericia/{id}/cancelar
  const confirmarCancelamento = async () => {
    if (confirmText !== "CANCELAR PERÍCIA" || !periciaParaCancelar) return;

    try {
      const response = await fetch(
        `${API_BASE_URL}/pericia/${periciaParaCancelar.id}/cancelar`,
        {
          method: "PUT",
          headers: { Authorization: `Bearer ${token}` },
        },
      );

      if (!response.ok) throw new Error("Erro ao cancelar");

      setPericias((prev) =>
        prev.map((p) =>
          p.id === periciaParaCancelar.id
            ? { ...p, statusPericia: "CANCELADA" }
            : p,
        ),
      );

      setCancelOpen(false);
    } catch (err) {
      alert(err.message);
    }
  };

  // PUT /pericia/{id}/remarcar
  const confirmarRemarcacao = async () => {
    if (!periciaParaEditar || !novaData) return;

    try {
      // "yyyy-MM-dd HH:mm:ss"
      const dataFormatada = novaData.replace("T", " ") + ":00";

      const response = await fetch(
        `${API_BASE_URL}/pericia/${periciaParaEditar.id}/remarcar`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ data: dataFormatada }),
        },
      );

      if (!response.ok) throw new Error("Erro ao remarcar");

      setPericias((prev) =>
        prev.map((p) =>
          p.id === periciaParaEditar.id
            ? { ...p, data: dataFormatada, statusPericia: "AGENDADA" }
            : p,
        ),
      );

      setEditOpen(false);
    } catch (err) {
      alert(err.message);
    }
  };

  const periciasFiltradas = useMemo(() => {
    const q = (search ?? "").trim().toLowerCase();

    return pericias.filter((p) => {
      const paciente = (p.nomePaciente ?? "").toLowerCase();
      const medico = (p.nomeMedico ?? "").toLowerCase();
      return !q || paciente.includes(q) || medico.includes(q);
    });
  }, [pericias, search]);

  const formatarDataHoraBR = (dataISO) => {
    if (!dataISO) return "-";
    const d = new Date(dataISO);
    if (Number.isNaN(d.getTime())) return "-";
    return d.toLocaleString("pt-BR");
  };

  return (
    <>
      {/* MODAL CANCELAR */}
      {cancelOpen && (
        <div className="popup-container cancel-pericia-container">
          <div className="popup-header">
            <p>Cancelar Perícia</p>
            <button
              className="popup-close-btn"
              onClick={() => setCancelOpen(false)}
            >
              <i className="fa-solid fa-xmark"></i>
            </button>
          </div>

          <div className="cancel-pericia">
            <p>
              Digite <strong>CANCELAR PERÍCIA</strong> para confirmar:
            </p>
            <input
              className="confirm-input"
              value={confirmText}
              onChange={(e) => setConfirmText(e.target.value)}
            />
            <button
              className="confirm-delete-btn"
              onClick={confirmarCancelamento}
              disabled={confirmText !== "CANCELAR PERÍCIA"}
            >
              Confirmar Cancelamento
            </button>
          </div>
        </div>
      )}

      {/* MODAL REMARCAR */}
      {editOpen && (
        <div className="popup-container edit-pericia-container">
          <div className="popup-header">
            <p>Remarcar Perícia</p>
            <button
              className="popup-close-btn"
              onClick={() => setEditOpen(false)}
            >
              <i className="fa-solid fa-xmark"></i>
            </button>
          </div>

          <div className="pericia-info">
            <input
              type="datetime-local"
              className="new-date-input"
              value={novaData}
              onChange={(e) => setNovaData(e.target.value)}
            />
            <button
              className="confirm-date-btn"
              onClick={confirmarRemarcacao}
            >
              Salvar Nova Data
            </button>
          </div>
        </div>
      )}

      <div className="scroll-list">
        <ul className="list-section">
          {!loading && !erro && periciasFiltradas.length === 0 && (
            <li className="list-item">
              <div className="item-info">
                <p>Nenhuma perícia encontrada.</p>
              </div>
            </li>
          )}

          {periciasFiltradas.map((p) => (
            <li className="list-item" key={p.id}>
              <div className="item-info">
                <p>{p.nomePaciente ?? "-"}</p>
                <p>{p.nomeMedico ?? "-"}</p>
                <p className={`status-${p.statusPericia?.toLowerCase()}`}>
                  {p.statusPericia ?? "-"}
                </p>
                <p>{formatarDataHoraBR(p.data)}</p>
              </div>

              <div className="item-btns">
                <button
                  onClick={() => {
                    setPericiaParaEditar(p);
                    setNovaData(p.data?.substring(0, 16) || "");
                    setEditOpen(true);
                  }}
                  className="edit-btn"
                  type="button"
                >
                  <i className="fa-solid fa-pen"></i>
                </button>

                <button
                  onClick={() => {
                    setPericiaParaCancelar(p);
                    setConfirmText("");
                    setCancelOpen(true);
                  }}
                  className="delete-btn"
                  type="button"
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
