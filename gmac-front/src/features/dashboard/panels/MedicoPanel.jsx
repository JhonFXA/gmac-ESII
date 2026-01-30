import Header from '@/components/layout/Header.jsx';
import Footer from '@/components/layout/Footer.jsx';

import styles from '../pages/mainPanel.module.css';

export default function MedicoPanel() {
  return (
    <>
      <div className={styles.buttonCollection}>
        <button className={styles.mainButton}>
          <i class="fa-solid fa-clipboard-check"></i>
          <p>Validar Documentação</p>
        </button>
        <button className={styles.mainButton}>
          <i class="fa-regular fa-calendar"></i>
          <p>Gerenciar Agenda</p>
        </button>
        <button className={styles.mainButton}>
          <i className="fas fa-bell"></i>
          <p>Gerar Notificação</p>
        </button>
      </div>
    </>
  )
}