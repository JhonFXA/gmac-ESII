import { useState } from "react";
import { Link } from "react-router-dom";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";

import DocumentacoesList from "../components/DocumentacoesList";
import styles from "@/features/pericias/style/manage-pericias.module.css";

export default function ManageDocumentacoesPage() {
  const [search, setSearch] = useState("");
  const [statusOpen, setStatusOpen] = useState(false);

  const [statusDocumentacao, setStatusDocumentacao] = useState("PENDENTE");

  const statusOptions = ["PENDENTE", "APROVADA", "PERICIA", "REPROVADA"];

  return (
    <div className={styles.container}>
      <Header />

      <main className={styles.main}>
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="">Validar Documentações</Link>
          </p>
        </div>

        <div className={styles.scrollSection}>
          <div className={styles.searchSection}>
            <input
              className={styles.searchInput}
              type="text"
              placeholder="Pesquisar por paciente..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />

            <div className={styles.searchAttributes}>
              <div className={styles.attrList}>
                <div className={styles.attr}>Paciente</div>
                <div className={styles.attr}>CPF</div>

                {/* STATUS */}
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
                      {statusOptions.map((status) => (
                        <label key={status} className={styles.filterItem}>
                          <input
                            type="radio"
                            name="statusDocumentacao"
                            checked={statusDocumentacao === status}
                            onChange={() => {
                              setStatusDocumentacao(status);
                              setStatusOpen(false);
                            }}
                          />
                          {status.charAt(0) +
                            status.slice(1).toLowerCase()}
                        </label>
                      ))}

                      <button
                        type="button"
                        className={styles.clearFilterBtn}
                        onClick={() => setStatusDocumentacao("PENDENTE")}
                      >
                        Padrão
                      </button>
                    </div>
                  )}
                </div>

                <div className={styles.attr}>Data do Envio</div>
              </div>

              <div className={styles.emptySpace} />
            </div>
          </div>

          <DocumentacoesList
            search={search}
            statusDocumentacao={statusDocumentacao}
          />
        </div>
      </main>

      <Footer />
    </div>
  );
}
