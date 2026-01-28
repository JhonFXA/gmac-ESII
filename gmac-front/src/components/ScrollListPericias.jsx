import { useEffect, useMemo, useState } from "react";
import { useAuth } from '../context/AuthContext';
import "../css/ScrollList.css";
import { useNavigate } from 'react-router-dom';

export default function ScrollListPericia() {
    const API_BASE_URL = import.meta.env.VITE_API_URL;
    const { token } = useAuth();
    const navigate = useNavigate();

    const [pericias, setPericias] = useState([]);
    const [loading, setLoading] = useState(true);
    const [erro, setErro] = useState("");
    const [search, setSearch] = useState("");

    // --- ESTADOS DE REMARCAÇÃO (EDITAR) ---
    const [editOpen, setEditOpen] = useState(false);
    const [periciaParaEditar, setPericiaParaEditar] = useState(null);
    const [novaData, setNovaData] = useState("");

    // --- ESTADOS DE CANCELAMENTO ---
    const [cancelOpen, setCancelOpen] = useState(false);
    const [periciaParaCancelar, setPericiaParaCancelar] = useState(null);
    const [confirmText, setConfirmText] = useState("");

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
                        "Authorization": `Bearer ${token}`,
                    },
                });
                if (!response.ok) throw new Error(`Erro: ${response.status}`);
                const data = await response.json();
                if (isMounted) setPericias(data);
            } catch (err) {
                if (isMounted) setErro(err.message || "Falha ao carregar perícias");
            } finally {
                if (isMounted) setLoading(false);
            }
        }
        loadPericias();
        return () => { isMounted = false; };
    }, [API_BASE_URL, token]);

    // LÓGICA DE CANCELAMENTO (PUT /pericia/{id}/cancelar)
    const confirmarCancelamento = async () => {
        if (confirmText !== "cancelar pericia") return;

        try {
            const response = await fetch(`${API_BASE_URL}/pericia/${periciaParaCancelar.id}/cancelar`, {
                method: "PUT",
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (!response.ok) throw new Error("Erro ao cancelar");

            setPericias(prev => prev.map(p => 
                p.id === periciaParaCancelar.id ? { ...p, statusPericia: "CANCELADA" } : p
            ));
            setCancelOpen(false);
        } catch (err) { alert(err.message); }
    };

    // LÓGICA DE REMARCAÇÃO (PUT /pericia/{id}/remarcar)
    const confirmarRemarcacao = async () => {
        try {
            // Formata para "yyyy-MM-dd HH:mm:ss" conforme o DTO
            const dataFormatada = novaData.replace("T", " ") + ":00";

            const response = await fetch(`${API_BASE_URL}/pericia/${periciaParaEditar.id}/remarcar`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ data: dataFormatada })
            });

            if (!response.ok) throw new Error("Erro ao remarcar");

            setPericias(prev => prev.map(p => 
                p.id === periciaParaEditar.id ? { ...p, data: dataFormatada, statusPericia: "AGENDADA" } : p
            ));
            setEditOpen(false);
        } catch (err) { alert(err.message); }
    };

    const periciasFiltradas = useMemo(() => {
        const q = search.trim().toLowerCase();
        return pericias.filter((p) => 
            (p.nomePaciente || "").toLowerCase().includes(q) || 
            (p.nomeMedico || "").toLowerCase().includes(q)
        );
    }, [pericias, search]);

    return (
        <div className="scroll-section">
            {/* MODAL CANCELAR */}
            {cancelOpen && (
                <div className="user_container">
                    <div className="user-header">
                        <p>Cancelar Perícia</p>
                        <button className='user-close-btn' onClick={() => setCancelOpen(false)}><i className="fa-solid fa-xmark"></i></button>
                    </div>
                    <div className="user-info">
                        <p>Digite <strong>cancelar pericia</strong> para confirmar:</p>
                        <input className="confirm-input" value={confirmText} onChange={(e) => setConfirmText(e.target.value)} />
                        <button className="delete-btn" style={{width:'100%'}} onClick={confirmarCancelamento} disabled={confirmText !== "cancelar pericia"}>Confirmar Cancelamento</button>
                    </div>
                </div>
            )}

            {/* MODAL REMARCAR */}
            {editOpen && (
                <div className="user_container">
                    <div className="user-header">
                        <p>Remarcar Perícia</p>
                        <button className='user-close-btn' onClick={() => setEditOpen(false)}><i className="fa-solid fa-xmark"></i></button>
                    </div>
                    <div className="user-info">
                        <input type="datetime-local" className="confirm-input" value={novaData} onChange={(e) => setNovaData(e.target.value)} />
                        <button className="view-btn" style={{width:'100%', marginTop:'10px'}} onClick={confirmarRemarcacao}>Salvar Nova Data</button>
                    </div>
                </div>
            )}

            <div className="scroll-list">
                <div className="search-section">
                    <input className="search-input" type="text" placeholder="Pesquisar..." value={search} onChange={(e) => setSearch(e.target.value)} />
                    <div className="search-attributes search-attributes-pericia">
                        <p>Paciente</p><p>Médico</p><p>Status</p><p>Data</p>
                    </div>
                </div>
                <ul className="list-section">
                    {/* {periciasFiltradas.map((p) => (
                        <li className="list-item" key={p.id}>
                            <div className="item-info grid-layout">
                                <p>{p.nomePaciente}</p>
                                <p>{p.nomeMedico}</p>
                                <p className={`status-${p.statusPericia?.toLowerCase()}`}>{p.statusPericia}</p>
                                <p>{p.data ? new Date(p.data).toLocaleString('pt-BR') : "-"}</p>
                            </div>
                            <div className="item-btns">
                                <button onClick={() => { setPericiaParaEditar(p); setNovaData(p.data?.substring(0,16) || ""); setEditOpen(true); }} className="edit-btn"><i className="fa-solid fa-pen"></i></button>
                                <button onClick={() => { setPericiaParaCancelar(p); setConfirmText(""); setCancelOpen(true); }} className="delete-btn"><i className="fa-solid fa-circle-xmark"></i></button>
                            </div>
                        </li>
                    ))} */}
                       {/* <li className="list-item" key={p.id}> */}
                       <li className="list-item">
                            <div className="item-info">
                                <p>Josefastos Mirios</p>
                                <p>Asfolto Farias</p>
                                <p>Pendente</p>
                                <p>12/09/2026 15:25</p>
                            </div>
                            <div className="item-btns">
                                <button onClick={() => {setEditOpen(true); }} className="edit-btn"><i className="fa-solid fa-pen"></i></button>
                                <button onClick={() => {setConfirmText(""); setCancelOpen(true); }} className="delete-btn"><i className="fa-solid fa-circle-xmark"></i></button>
                            </div>
                        </li>
                </ul>
            </div>
        </div>
    );
}