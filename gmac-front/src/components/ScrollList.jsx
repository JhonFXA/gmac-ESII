import { useEffect, useMemo, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function ScrollList({
  search,
  perfisSelecionados,
  setLoading,
  setErro,
  loading,
  erro,
}) {
  const API_BASE_URL = import.meta.env.VITE_API_URL;
  const { token } = useAuth();

  const [usuarios, setUsuarios] = useState([]);

  const [userOpen, setUserOpen] = useState(false);
  const [usuarioSelecionado, setUsuarioSelecionado] = useState(null);

  const navigate = useNavigate();

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
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const data = await response.json();
        const lista = Array.isArray(data) ? data : data?.usuarios ?? [];

        if (isMounted) setUsuarios(lista);
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
  }, [API_BASE_URL, token, setErro, setLoading]);

  const usuariosFiltrados = useMemo(() => {
    const q = search.trim().toLowerCase();

    return usuarios.filter((u) => {
      const login = (u.login ?? u.username ?? "").toLowerCase();
      const perfil = (u.perfil ?? u.role ?? "").toUpperCase();
      const cpf = (u.cpf ?? "").toLowerCase();

      const matchTexto = !q || login.includes(q) || cpf.includes(q);
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
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) throw new Error(`HTTP ${response.status}`);

      const data = await response.json();
      setUsuarioSelecionado(data);
      setUserOpen(true);
    } catch (err) {
      console.log(err);
    }
  };

  function calcularIdade(dataNascimento) {
    if (!dataNascimento) return null;

    const nascimento = new Date(dataNascimento);
    const hoje = new Date();

    let idade = hoje.getFullYear() - nascimento.getFullYear();
    const mesAtual = hoje.getMonth();
    const mesNascimento = nascimento.getMonth();

    if (
      mesAtual < mesNascimento ||
      (mesAtual === mesNascimento && hoje.getDate() < nascimento.getDate())
    ) {
      idade--;
    }

    return idade;
  }

  function formatarDataBR(dataISO) {
    if (!dataISO) return null;
    const [ano, mes, dia] = dataISO.slice(0, 10).split("-");
    return `${dia}/${mes}/${ano}`;
  }

  return (
    <>
      {userOpen && (
        <div className="user_container">
          <div className="user-header">
            <p>Informações Adicionais</p>
            <button className="user-close-btn" onClick={() => setUserOpen(false)}>
              <i className="fa-solid fa-xmark"></i>
            </button>
          </div>

          <div className="user-info">
            <p><strong>Nome Completo:</strong> {usuarioSelecionado?.nome ?? "-"}</p>
            <p><strong>Login:</strong> {usuarioSelecionado?.login ?? usuarioSelecionado?.username ?? "-"}</p>
            <p><strong>Email:</strong> {usuarioSelecionado?.email ?? "-"}</p>
            <p><strong>CPF:</strong> {usuarioSelecionado?.cpf ?? "-"}</p>
            <p>
              <strong>Idade:</strong>{" "}
              {usuarioSelecionado?.dataNascimento
                ? `${calcularIdade(usuarioSelecionado.dataNascimento)} anos`
                : "-"}
            </p>
            <p>
              <strong>Data de Nascimento:</strong>{" "}
              {usuarioSelecionado?.dataNascimento
                ? formatarDataBR(usuarioSelecionado.dataNascimento)
                : "-"}
            </p>
            <p><strong>Perfil:</strong> {usuarioSelecionado?.perfil ?? "-"}</p>
            {usuarioSelecionado?.especializacao && (
              <p><strong>Especialização:</strong> {usuarioSelecionado.especializacao}</p>
            )}
          </div>
        </div>
      )}

      <div className="scroll-list">
        <ul className="list-section">
          {!loading && !erro && usuariosFiltrados.length === 0 && (
            <li className="list-item">
              <div className="item-info">
                <p>Nenhum usuário encontrado.</p>
              </div>
            </li>
          )}

          {usuariosFiltrados.map((u) => (
            <li className="list-item" key={u.id ?? u.login ?? u.username ?? u.cpf}>
              <div className="item-info">
                <p>{u.login ?? u.username ?? "-"}</p>
                <p>{u.perfil ?? u.role ?? "-"}</p>
                <p>{u.cpf ?? "-"}</p>
              </div>

              <div className="item-btns">
                <button
                  onClick={() =>
                    navigate(`/painel-principal/gerenciar-usuarios/editar-usuario/${u.cpf}`)
                  }
                  className="edit-btn"
                  type="button"
                >
                  <i className="fa-solid fa-pen-to-square"></i>
                </button>

                <button
                  onClick={() => listarUsuario(u.cpf)}
                  className="view-btn"
                  type="button"
                >
                  <i className="fa-solid fa-magnifying-glass"></i>
                </button>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}
