import { useNavigate } from 'react-router-dom';

import styles from '../pages/mainPanel.module.css';

export default function RecepcionistaPanel() {
  const navigate = useNavigate();

  return (
    <>
      <div className={styles.buttonCollection}>
        <button className={styles.mainButton} onClick={()=> {navigate('/painel-principal/cadastrar-paciente')}}>
          <i className="fas fa-user-plus"></i>
          <p>Cadastrar Paciente</p>
          </button>
        <button className={styles.mainButton}>
          <i className="fa-solid fa-magnifying-glass"></i>
          <p>Consultar Cadastro</p>
          </button>
        <button className={styles.mainButton}>
          <i className="fas fa-user-edit"></i>
          <p>Atualizar Cadastro</p>
          </button>
        <button className={styles.mainButton}>
          <i className="fas fa-bell"></i>
          <p>Gerar Notificação</p>
          </button>
      </div>
    </>
  )
}