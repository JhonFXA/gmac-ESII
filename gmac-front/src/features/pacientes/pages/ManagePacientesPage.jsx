import { useState } from "react";
import { Link } from "react-router-dom";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";

import PacientesScrollList from "../components/PacientesScrollList";
import styles from "@/features/users/style/manage-users.module.css";

export default function ManagePacientesPage() {
  const [ordemNome, setOrdemNome] = useState(null);
  const [search, setSearch] = useState("");
  const [filterOpen, setFilterOpen] = useState(false);
  const [pacientesSelecionados, setPacientesSelecionados] = useState(new Set());

  function toggleStatus(status) {
    setPacientesSelecionados((prev) => {
      const next = new Set(prev);
      if (next.has(status)) next.delete(status);
      else next.add(status);
      return next;
    });
  }
  function toggleOrdenacaoNome() {
    setOrdemNome((prev) => {
      if (prev === null) return "ASC";
      if (prev === "ASC") return "DESC";
      return null;
    });
  }

  return (
    <div className={styles.container}>
      <Header />

      <main className={styles.main}>
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="">
              Consultar Cadastro
            </Link>
          </p>
        </div>

        <div className={styles.scrollSection}>
          <div className={styles.searchSection}>
            <input
              className={styles.searchInput}
              type="text"
              placeholder="Pesquisar..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />

            <div className={styles.searchAttributes}>
              <div className={styles.attrList}>
                 <div
                                  className={`${styles.attr} ${styles.clickableAttr}`}
                                  onClick={toggleOrdenacaoNome}
                                >
                                  Nome
                                  <i
                                    className={`fa-solid ${ordemNome === "ASC"
                                        ? "fa-arrow-up"
                                        : ordemNome === "DESC"
                                          ? "fa-arrow-down"
                                          : ""
                                      } ${styles.sortIcon}`}
                                  />
                                </div>
                <div className={`${styles.attr} ${styles.attrPerfil}`}>
                  Status
                  <button
                    type="button"
                    className={styles.filterBtn}
                    onClick={() => setFilterOpen((v) => !v)}
                  >
                    <i
                      className={`fa-solid ${filterOpen ? "fa-xmark" : "fa-angle-down"} ${
                        styles.filterIcon
                      } ${filterOpen ? styles.filterIconOpen : ""}`}
                    />
                  </button>
                  {filterOpen && (
                    <div className={styles.filterMenu}>
                      <label className={styles.filterItem}>
                        <input
                          type="checkbox"
                          checked={pacientesSelecionados.has("PENDENTE")}
                          onChange={() => toggleStatus("PENDENTE")}
                        />
                        Pendente
                      </label>
                      <label className={styles.filterItem}>
                        <input
                          type="checkbox"
                          checked={pacientesSelecionados.has("FINALIZADA")}
                          onChange={() => toggleStatus("FINALIZADA")}
                        />
                        Finalizada
                      </label>
                      <button
                        type="button"
                        className={styles.clearFilterBtn}
                        onClick={() => setPacientesSelecionados(new Set())}
                      >
                        Limpar
                      </button>
                    </div>
                  )}
                </div>
                <div className={styles.attr}>CPF</div>
              </div>
              <div className={styles.emptySpace}></div>
            </div>
          </div>

          <PacientesScrollList
            search={search}
            pacientesSelecionados={pacientesSelecionados}
            ordemNome={ordemNome}
          />
        </div>
      </main>

      <Footer />
    </div>
  );
}
