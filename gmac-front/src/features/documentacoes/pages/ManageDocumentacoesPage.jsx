import { useState } from "react";
import { Link } from "react-router-dom";
import DocumentacoesList from "../components/DocumentacoesList";


import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import styles from "@/features/pericias/style/manage-pericias.module.css";

export default function ManageDocumentacoesPage(){
    const [search, setSearch] = useState("");

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
                <div className={styles.attr}>Status</div>
                <div className={styles.attr}>Data do Envio</div>
              </div>
              <div className={styles.emptySpace} />
            </div>
          </div>

          <DocumentacoesList search={search} />
        </div>
      </main>

      <Footer />
    </div>
  );
}