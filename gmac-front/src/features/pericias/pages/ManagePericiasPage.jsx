import { useState } from "react";
import { Link } from "react-router-dom";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";

import PericiasList from "../components/PericiasList";
import styles from "../style/manage-pericias.module.css";

export default function ManagePericiasPage() {
  const [search, setSearch] = useState("");
  const [statusOpen, setStatusOpen] = useState(false);

  const [statusSelecionado, setStatusSelecionado] = useState("AGENDADA");




  return (
    <div className={styles.container}>
      <Header />

      <main className={styles.main}>
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="">Gerenciar Perícias</Link>
          </p>
        </div>

        <div className={styles.scrollSection}>
          <div className={styles.searchSection}>
            <input
              className={styles.searchInput}
              type="text"
              placeholder="Pesquisar por paciente ou médico..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />

            <div className={styles.searchAttributes}>
              <div className={styles.attrList}>
                <div className={styles.attr}>Paciente</div>
                <div className={styles.attr}>Médico</div>
                <div className={`${styles.attr} ${styles.attrPerfil}`}>
                  Status
                  <button
                    type="button"
                    className={styles.filterBtn}
                    onClick={() => setStatusOpen((v) => !v)}
                  >
                    <i
                      className={`fa-solid ${statusOpen ? "fa-xmark" : "fa-angle-down"
                        } ${styles.filterIcon} ${statusOpen ? styles.filterIconOpen : ""
                        }`}
                    />
                  </button>

                  {statusOpen && (
                    <div className={styles.filterMenu}>
                      {["AGENDADA", "FINALIZADA", "CANCELADA"].map((status) => (
                        <label key={status} className={styles.filterItem}>
                          <input
                            type="radio"
                            name="statusPericia"
                            checked={statusSelecionado === status}
                            onChange={() => {
                              setStatusSelecionado(status);
                              setStatusOpen(false);
                            }}
                          />
                          {status.charAt(0) + status.slice(1).toLowerCase()}
                        </label>
                      ))}

                      <button
                        type="button"
                        className={styles.clearFilterBtn}
                        onClick={() => setStatusSelecionado("AGENDADA")}
                      >
                        Padrão
                      </button>
                    </div>
                  )}

                </div>

                <div className={styles.attr}>Data</div>
              </div>
              <div className={styles.emptySpace} />
            </div>
          </div>

          <PericiasList search={search}
            statusPericia={statusSelecionado}
          />
        </div>
      </main>

      <Footer />
    </div>
  );
}
