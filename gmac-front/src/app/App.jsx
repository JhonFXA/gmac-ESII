import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./providers/AuthContext.jsx";
import { QueryProvider } from "./providers/QueryProvider.jsx";
import { ProtectedRoute } from "./routes/ProtectedRoute.jsx";
import ApiAuthBridge from "@/app/providers/ApiAuthBridge";
import { ToastProvider } from "./providers/ToastProvider.jsx";
import ToastHost from "@/components/ui/ToastHost.jsx";

import LoginPage from "@/features/auth/pages/LoginPage";
import MainPanelPage from "@/features/dashboard/pages/MainPanelPage.jsx";
import CreateUserPage from "@/features/users/pages/CreateUserPage";
import ManageUsersPage from "@/features/users/pages/ManageUsersPage.jsx";
import EditUserPage from "@/features/users/pages/EditUserPage.jsx";
import ManagePericiasPage from "@/features/pericias/pages/ManagePericiasPage.jsx";
import CreatePacientePage from "@/features/pacientes/pages/CreatePacientePage.jsx";
import ManagePacientesPage from "@/features/pacientes/pages/ManagePacientesPage.jsx";
import EditPacientePage from "@/features/pacientes/pages/EditPacientePage.jsx";
import ManageDocumentacoesPage from "@/features/documentacoes/pages/ManageDocumentacoesPage.jsx";
import PaginaDocumentacao from "@/features/documentacoes/pages/PaginaDocumentacao.jsx";
import RelatorioPage from "@/features/relatorios/pages/RelatorioPage.jsx";



function App() {
  return (
    <AuthProvider>
      <QueryProvider>
        <ToastProvider>
          <BrowserRouter>
            <ApiAuthBridge>
              <ToastHost />
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
                    <ProtectedRoute
                      allowedProfiles={[
                        "ADMINISTRADOR",
                        "MEDICO",
                        "RECEPCIONISTA",
                      ]}
                    >
                      <ManagePericiasPage />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/painel-principal/validar-documentacoes"
                  element={
                    <ProtectedRoute
                      allowedProfiles={[
                        "ADMINISTRADOR",
                        "MEDICO",
                        "RECEPCIONISTA",
                      ]}
                    >
                      <ManageDocumentacoesPage />
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
                <Route
                  path="/painel-principal/consultar-cadastro"
                  element={
                    <ProtectedRoute allowedProfiles={["RECEPCIONISTA"]}>
                      <ManagePacientesPage />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/painel-principal/consultar-cadastro/editar-paciente/:cpf"
                  element={
                    <ProtectedRoute allowedProfiles={["RECEPCIONISTA"]}>
                      <EditPacientePage />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/painel-principal/gerar-relatorio"
                  element={
                    <ProtectedRoute allowedProfiles={["ADMINISTRADOR"]}>
                      <RelatorioPage />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/painel-principal/validar-documentacoes/:id"
                  element={
                    <ProtectedRoute allowedProfiles={["MEDICO"]}>
                      <PaginaDocumentacao />
                    </ProtectedRoute>
                  }
                />
              </Routes>
            </ApiAuthBridge>
          </BrowserRouter>
        </ToastProvider>
      </QueryProvider>
    </AuthProvider>
  );
}

export default App;
