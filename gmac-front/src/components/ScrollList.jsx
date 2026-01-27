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
}, [API_BASE_URL]);

const usuariosFiltrados = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return usuarios;

    return usuarios.filter((u) => {
        const login = (u.login ?? u.username ?? "").toLowerCase();
        const perfil = (u.perfil ?? u.role ?? "").toLowerCase();
        const cpf = (u.cpf ?? "").toLowerCase();
        return login.includes(q) || perfil.includes(q) || cpf.includes(q);
    });
}, [usuarios, search]);

return (
    <div className="scroll-section">
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
            <p>Perfil</p>
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
                <button className="view-btn" type="button">
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