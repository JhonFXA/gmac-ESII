import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext.jsx';
import { ProtectedRoute } from './routes/ProtectedRoute.jsx';
import Login from './pages/Login.jsx'
import PainelPrincipal from './pages/PainelPrincipal.jsx';
import CadastrarUsuario from './pages/CadastrarUsuario.jsx';
import GerenciarUsuarios from './pages/GerenciarUsuarios.jsx';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Login />} />
          <Route 
            path='/painel-principal' element={
              <ProtectedRoute>
                <PainelPrincipal />
              </ProtectedRoute>
            } 
          />
          <Route path='/painel-principal/cadastrar-usuario' element={
            <ProtectedRoute allowedProfiles={['ADMINISTRADOR']}>
              <CadastrarUsuario />
            </ProtectedRoute>}
          />
          <Route path='/painel-principal/gerenciar-usuarios' element={
            <ProtectedRoute allowedProfiles={['ADMINISTRADOR']}>
              <GerenciarUsuarios />
            </ProtectedRoute>}
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App;
