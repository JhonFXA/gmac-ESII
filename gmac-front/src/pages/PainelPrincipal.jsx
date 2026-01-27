import { useAuth } from "../context/AuthContext.jsx";
import { Navigate } from "react-router-dom";

import Recepcionista from "./panels/Recepcionista.jsx";
import Medico from "./panels/Medico.jsx";
import Administrador from "./panels/Administrador.jsx";

import '../css/App.css';
import '../css/main-panel.css';

export default function PainelPrincipal() {
  const { perfil, token } = useAuth();

  console.log(token);

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