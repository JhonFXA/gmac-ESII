import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./providers/AuthContext.jsx";
import { QueryProvider } from "./providers/QueryProvider.jsx";
import { ProtectedRoute } from "./routes/ProtectedRoute.jsx";
import ApiAuthBridge from "@/app/providers/ApiAuthBridge";

import LoginPage from "@/features/auth/pages/LoginPage";
import MainPanelPage from "@/features/dashboard/pages/MainPanelPage.jsx";
import CreateUserPage from "@/features/users/pages/CreateUserPage";
import ManageUsersPage from "@/features/users/pages/ManageUsersPage.jsx";
import EditUserPage from "@/features/users/pages/EditUserPage.jsx";
import ManagePericiasPage from "@/features/pericias/pages/ManagePericiasPage.jsx";
import CreatePacientePage from "@/features/pacientes/pages/CreatePacientePage.jsx";
// import CadastrarPaciente from './pages/CadastrarPaciente.jsx';

function App() {
  return (
    <AuthProvider>
      <QueryProvider>
        <BrowserRouter>
          <ApiAuthBridge>
            <Routes>
              <Route path="/" element={<LoginPage />} />

              <Route
                path="/painel-principal"
                element={
                  <ProtectedRoute>
                    <MainPanelPage />
                  </ProtectedRoute>
                }
              />

              <Route
                path="/painel-principal/cadastrar-usuario"
                element={
                  <ProtectedRoute allowedProfiles={["ADMINISTRADOR"]}>
                    <CreateUserPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/painel-principal/gerenciar-usuarios"
                element={
                  <ProtectedRoute allowedProfiles={["ADMINISTRADOR"]}>
                    <ManageUsersPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/painel-principal/gerenciar-usuarios/editar-usuario/:cpf"
                element={
                  <ProtectedRoute allowedProfiles={["ADMINISTRADOR"]}>
                    <EditUserPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/painel-principal/gerenciar-pericias"
                element={
                  <ProtectedRoute allowedProfiles={["ADMINISTRADOR", "MEDICO", "RECEPCIONISTA"]}>
                    <ManagePericiasPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/painel-principal/cadastrar-paciente"
                element={
                  <ProtectedRoute allowedProfiles={["RECEPCIONISTA"]}>
                    <CreatePacientePage />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </ApiAuthBridge>
        </BrowserRouter>
      </QueryProvider>
    </AuthProvider>
  );
}

export default App;
