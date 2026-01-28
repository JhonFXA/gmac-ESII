import Header from "../components/Header.jsx";
import Footer from "../components/Footer.jsx";
import ScrollListPericia from "../components/ScrollListPericias.jsx";
import "../css/ScrollList.css";
import { Link } from "react-router-dom";
import { useState } from "react";

export default function GerenciarPericias() {
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");

  return (
    <div className="container">
      <Header />

      <main className="main-nocentered-container">
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="">Gerenciar Perícias</Link>
          </p>
        </div>

        <div className="scroll-section">
          <div className="search-section">
            <input
              className="search-input"
              type="text"
              placeholder="Pesquisar por paciente ou médico..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />

            <div className="search-attributes search-attributes-pericia">
              <div className="attr-list">
                <div className="attr">Paciente</div>
                <div className="attr">Médico</div>
                <div className="attr">Status</div>
                <div className="attr">Data</div>
              </div>
              <div className="empty-space">
                
              </div>
            </div>

            {loading && <p>Carregando...</p>}
            {erro && <p className="error-message">Erro: {erro}</p>}
          </div>

          <ScrollListPericia
            search={search}
            loading={loading}
            setLoading={setLoading}
            erro={erro}
            setErro={setErro}
          />
        </div>
      </main>

      <Footer />
    </div>
  );
}
