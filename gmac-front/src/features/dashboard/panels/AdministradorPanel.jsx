
import { useNavigate } from "react-router-dom";

import styles from '../pages/mainPanel.module.css';

export default function AdministradorPanel() {
  const navigate = useNavigate();

  return (
    <>
      <div className={styles.buttonCollection}>
        <button
          className={styles.mainButton}
          onClick={() => navigate("/painel-principal/cadastrar-usuario")}
        >
          <i className="fas fa-user-plus"></i>
          <p>Cadastrar Usuário</p>
        </button>
        <button
          className={styles.mainButton}
          onClick={() => navigate("/painel-principal/gerenciar-usuarios")}
        >
          <i className="fas fa-users"></i>
          <p>Gerenciar Usuários</p>
        </button>
        <button
          className={styles.mainButton}
          onClick={() => navigate("/painel-principal/gerenciar-pericias")}
        >
          <i className="fa-solid fa-stethoscope"></i>
          <p>Gerenciar Perícias</p>
        </button>
        <button
          className={styles.mainButton}
          onClick={() => {
            navigate("/painel-principal/cadastrar-paciente");
          }}
        >
          <i className="fas fa-user-plus"></i>
          <p>Cadastrar Paciente</p>
        </button>
        <button className={styles.mainButton} onClick={() => navigate("/painel-principal/consultar-cadastro")}>
          <i className="fa-solid fa-magnifying-glass"></i>
          <p>Consultar Cadastro</p>
        </button>
        <button
          className={styles.mainButton}
          onClick={() => navigate("/painel-principal/gerar-relatorio")}>
          <i className="fa-solid fa-chart-column"></i>
          <p>Gerar Relatório</p>
        </button>
      </div>
    </>
  );
}
