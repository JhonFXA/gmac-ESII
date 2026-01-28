import { useEffect, useMemo, useState } from "react";
import { useAuth } from '../context/AuthContext';
import "../css/ScrollList.css";

export default function ScrollList() {
    const API_BASE_URL = import.meta.env.VITE_API_URL;
    const { token } = useAuth();

    const [usuarios, setUsuarios] = useState([]);
    const [loading, setLoading] = useState(true);
    const [erro, setErro] = useState("");

    const [search, setSearch] = useState("");

    const [userOpen, setUserOpen] = useState(false);
    const [usuarioSelecionado, setUsuarioSelecionado] = useState(null);

    const [filterOpen, setFilterOpen] = useState(false);
    const [perfisSelecionados, setPerfisSelecionados] = useState(new Set());

    useEffect(() => {
        let isMounted = true;

        async function loadUsuarios() {
            try {
                setErro("");
                setLoading(true);

                const response = await fetch(`${API_BASE_URL}/usuario/listar`, {
                    method: "GET",
                    headers: { 
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    console.log(token)
                    throw new Error(`HTTP ${response.status}`);
                }

                const data = await response.json();

                if (isMounted) setUsuarios(Array.isArray(data) ? data : (data?.usuarios ?? []));
            } catch (err) {
                if (isMounted) setErro(err.message || "Falha ao carregar usuários");
            } finally {
                if (isMounted) setLoading(false);
            }
        }

        loadUsuarios();

        return () => {
           isMounted = false;
        };
    }, [API_BASE_URL, token]);

    const usuariosFiltrados = useMemo(() => {
        const q = search.trim().toLowerCase();

        return usuarios.filter((u) => {
            const login = (u.login ?? u.username ?? "").toLowerCase();
            const perfil = (u.perfil ?? u.role ?? "").toUpperCase();
            const cpf = (u.cpf ?? "").toLowerCase();

            const matchTexto =
            !q || login.includes(q) || cpf.includes(q);

            const matchPerfil =
            perfisSelecionados.size === 0 || perfisSelecionados.has(perfil);

            return matchTexto && matchPerfil;
        });
    }, [usuarios, search, perfisSelecionados]);



    const listarUsuario = async (cpf) => {
        try {
            const response = await fetch(`${API_BASE_URL}/usuario/buscar/${cpf}`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                },
            });
    
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
    
            const data = await response.json();

            setUsuarioSelecionado(data);
            setUserOpen(true);
        } catch (err){
            console.log(err);
        }

    }

    function togglePerfil(perfil) {
        setPerfisSelecionados(prev => {
            const next = new Set(prev);
            if (next.has(perfil)) next.delete(perfil);
            else next.add(perfil);
            return next;
        });
    }

    return (
        <div className="scroll-section">
            {userOpen && (
                <div className="user_container">
                    <div className="user-header">
                        <p>Informações Adicionais</p>
                        <button className='user-close-btn' onClick={()=> setUserOpen(false)}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                    <div className="user-info">
                        <p><strong>Nome Completo:</strong> {usuarioSelecionado?.nome ?? "-"}</p>
                        <p><strong>Login:</strong> {usuarioSelecionado?.login ?? usuarioSelecionado?.username ?? "-"}</p>
                        <p><strong>Email:</strong> {usuarioSelecionado?.email ?? "-"}</p>
                        <p><strong>CPF:</strong> {usuarioSelecionado?.cpf ?? "-"}</p>
                        <p><strong>Perfil:</strong> {usuarioSelecionado?.perfil ?? usuarioSelecionado?.role ?? "-"}</p>
                    </div>
                </div>
            )}
            <div className="scroll-list">
                <div className="search-section">
                <input
                    className="search-input"
                    type="text"
                    placeholder="Pesquisar..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />

                <div className="search-attributes">
                    <p>Login</p>
                    <p className="perfil-filter">
                        Perfil
                    <button type="button" className="filter-btn" onClick={() => setFilterOpen(v => !v)} >
                        <i className={`fa-solid ${filterOpen ? "fa-xmark" : "fa-angle-down"}`}></i>
                    </button>


                    {filterOpen && (
                        <div className="filter-menu">
                        <label className="filter-item">
                            <input
                            type="checkbox"
                            checked={perfisSelecionados.has("ADMINISTRADOR")}
                            onChange={() => togglePerfil("ADMINISTRADOR")}
                            />
                            Administrador
                        </label>

                        <label className="filter-item">
                            <input
                            type="checkbox"
                            checked={perfisSelecionados.has("RECEPCIONISTA")}
                            onChange={() => togglePerfil("RECEPCIONISTA")}
                            />
                            Recepcionista
                        </label>

                        <label className="filter-item">
                            <input
                            type="checkbox"
                            checked={perfisSelecionados.has("MEDICO")}
                            onChange={() => togglePerfil("MEDICO")}
                            />
                            Médico
                        </label>

                            <div className="filter-actions">
                                <button className="clear-filter-btn" onClick={() => setPerfisSelecionados(new Set())}>
                                    Limpar
                                </button>
                            </div>
                        </div>
                    )}
                    </p>

                    <p>CPF</p>
                </div>

                {loading && <p>Carregando...</p>}
                {erro && <p className="error-message">Erro: {erro}</p>}
                </div>

                <ul className="list-section">
                {!loading && !erro && usuariosFiltrados.length === 0 && (
                    <li className="list-item">
                    <div className="item-info">
                        <p>Nenhum usuário encontrado.</p>
                    </div>
                    </li>
                )}

                {usuariosFiltrados.map((u) => (
                    <li className="list-item" key={u.id ?? u.login ?? u.username ?? crypto.randomUUID()}>
                    <div className="item-info">
                        <p>{u.login ?? u.username ?? "-"}</p>
                        <p>{u.perfil ?? u.role ?? "-"}</p>
                        <p>{u.cpf ?? "-"}</p>
                    </div>

                    <div className="item-btns">
                        <button className="edit-btn" type="button">
                            <i className="fa-solid fa-pen-to-square"></i>
                        </button>
                        <button onClick={() => listarUsuario(u.cpf)} className="view-btn" type="button">
                            <i className="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </div>
                    </li>
                ))}
                </ul>
            </div>
        </div>
    );
}