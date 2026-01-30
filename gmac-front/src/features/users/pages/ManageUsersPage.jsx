import { useState } from "react";
import { Link } from "react-router-dom";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";

import UsersScrollList from "../components/UsersScrollList";
import styles from "../style/manage-users.module.css";

export default function ManageUsersPage() {
  const [search, setSearch] = useState("");
  const [filterOpen, setFilterOpen] = useState(false);
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
    <div className={styles.container}>
      <Header />

      <main className={styles.main}>
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="/painel-principal/gerenciar-usuarios">
              Gerenciar Usuários
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
                <div className={styles.attr}>Login</div>
                <div className={`${styles.attr} ${styles.attrPerfil}`}>
                  Perfil
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
                          checked={perfisSelecionados.has("ADMINISTRADOR")}
                          onChange={() => togglePerfil("ADMINISTRADOR")}
                        />
                        Administrador
                      </label>
                      <label className={styles.filterItem}>
                        <input
                          type="checkbox"
                          checked={perfisSelecionados.has("RECEPCIONISTA")}
                          onChange={() => togglePerfil("RECEPCIONISTA")}
                        />
                        Recepcionista
                      </label>
                      <label className={styles.filterItem}>
                        <input
                          type="checkbox"
                          checked={perfisSelecionados.has("MEDICO")}
                          onChange={() => togglePerfil("MEDICO")}
                        />
                        Médico
                      </label>
                      <button
                        type="button"
                        className={styles.clearFilterBtn}
                        onClick={() => setPerfisSelecionados(new Set())}
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

          <UsersScrollList
            search={search}
            perfisSelecionados={perfisSelecionados}
          />
        </div>
      </main>

      <Footer />
    </div>
  );
}
