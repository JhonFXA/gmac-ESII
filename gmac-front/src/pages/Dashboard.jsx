import { useAuth } from "../context/AuthContext.jsx";
import  Recepcionista from "./dashboards/Recepcionista.jsx";
import Medico from "./dashboards/Medico.jsx";
import Administrador from "./dashboards/Administrador.jsx";
import { Navigate } from "react-router-dom";
import '../css/App.css';

export default function Dashboard() {
  const { perfil } = useAuth();

  switch (perfil) {
    case 'RECEPCIONISTA':
      return <Recepcionista />;
    case 'MEDICO':
      return <Medico />;
    case 'ADMINISTRADOR':
      return <Administrador />;
    default:
      return <Navigate to="/" />;
    }
}