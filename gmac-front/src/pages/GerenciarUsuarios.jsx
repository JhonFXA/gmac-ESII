import Header from "../components/Header.jsx";
import Footer from "../components/Footer.jsx";
import ScrollList from "../components/ScrollList.jsx";
import "../css/ScrollList.css";
import { useState } from "react";
import { Link } from "react-router-dom";

export default function GerenciarUsuarios() {
  const [search, setSearch] = useState("");
  const [filterOpen, setFilterOpen] = useState(false);

  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");

  const [perfisSelecionados, setPerfisSelecionados] = useState(new Set());

  function togglePerfil(perfil) {
    setPerfisSelecionados((prev) => {
      const next = new Set(prev);
      if (next.has(perfil)) next.delete(perfil);
      else next.add(perfil);
      return next;
    });
  }

  return (
    <div className="container">
      <Header />

      <main className="main-nocentered-container">
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="/painel-principal/gerenciar-usuarios">
              Gerenciar Usuários
            </Link>
          </p>
        </div>

        <div className="scroll-section">
          <div className="search-section">
            <input
              className="search-input"
              type="text"
              placeholder="Pesquisar..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />

            <div className="search-attributes">
              <div className="attr-list">
                <div className="attr">Login</div>
                <div className="attr perfil-attr">
                  Perfil
                  <button
                    type="button"
                    className="filter-btn"
                    onClick={() => setFilterOpen((v) => !v)}
                  >
                    <i
                      className={`fa-solid ${filterOpen ? "fa-xmark" : "fa-angle-down"}`}
                    ></i>
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
                      <button
                        type="button"
                        className="clear-filter-btn"
                        onClick={() => setPerfisSelecionados(new Set())}
                      >
                        Limpar
                      </button>
                    </div>
                  )}
                </div>
                <div className="attr">CPF</div>
              </div>
              <div className="empty-space">
                
              </div>
            </div>

            {loading && <p>Carregando...</p>}
            {erro && <p className="error-message">Erro: {erro}</p>}
          </div>

          <ScrollList
            search={search}
            perfisSelecionados={perfisSelecionados}
            setLoading={setLoading}
            setErro={setErro}
            loading={loading}
            erro={erro}
          />
        </div>
      </main>

      <Footer />
    </div>
  );
}
