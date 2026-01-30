import { Navigate } from "react-router-dom";
import { useAuth } from "@/app/providers/AuthContext";

import Header from "@/components/layout/Header.jsx";
import Footer from "@/components/layout/Footer.jsx";

import RecepcionistaPanel from "../panels/RecepcionistaPanel.jsx";
import MedicoPanel from "../panels/MedicoPanel.jsx";
import AdministradorPanel from "../panels/AdministradorPanel.jsx";

import styles from './mainPanel.module.css'

const PANEL_BY_ROLE = {
  RECEPCIONISTA: RecepcionistaPanel,
  MEDICO: MedicoPanel,
  ADMINISTRADOR: AdministradorPanel,
};

export default function MainPanelPage() {
  const { perfil, token } = useAuth();

  if (!token) return <Navigate to="/" replace />;

  const Panel = PANEL_BY_ROLE[perfil];
  if (!Panel) return <Navigate to="/" replace />;

  return (
    <div className={styles.container}>
      <Header />
      <div className={styles.main}>
        <Panel />
      </div>
      <Footer />
    </div>
  )
}
