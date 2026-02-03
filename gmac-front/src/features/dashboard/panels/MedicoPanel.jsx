import Header from "@/components/layout/Header.jsx";
import Footer from "@/components/layout/Footer.jsx";

import { useNavigate } from "react-router-dom";

import styles from "../pages/mainPanel.module.css";

export default function MedicoPanel() {
  const navigate = useNavigate();

  return (
    <>
      <div className={styles.buttonCollection}>
        <button className={styles.mainButton}
          onClick={() => navigate("/painel-principal/gerenciar-documentacoes")}>
          <i className="fa-solid fa-clipboard-check"></i>
          <p>Validar Documentação</p>
        </button>
        <button
          className={styles.mainButton}
          onClick={() => navigate("/painel-principal/gerenciar-pericias")}
        >
          <i className="fa-solid fa-stethoscope"></i>
          <p>Gerenciar Perícias</p>
        </button>
      </div>
    </>
  );
}
