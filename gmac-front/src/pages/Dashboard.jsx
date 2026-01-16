import { useAuth } from "../context/AuthContext.jsx";
import  Recepcionista from "./dashboards/Recepcionista.jsx";
import '../css/App.css';

export default function Dashboard() {
  const { perfil } = useAuth();

  switch (perfil) {
    case 'RECEPCIONISTA':
      return <Recepcionista />;
    default:
        return <p>Perfil não autorizado.</p>;
    }
}