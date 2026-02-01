import { useState } from "react";
import { Link } from "react-router-dom";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";

import PericiasList from "../components/PericiasList";
import styles from "../style/manage-pericias.module.css";

export default function ManagePericiasPage() {
  const [search, setSearch] = useState("");

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
                <div className={styles.attr}>Status</div>
                <div className={styles.attr}>Data</div>
              </div>
              <div className={styles.emptySpace} />
            </div>
          </div>

          <PericiasList search={search} />
        </div>
      </main>

      <Footer />
    </div>
  );
}
