import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Login from './pages/Login.jsx'
import Recepcionista from './pages/Recepcionista.jsx';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<Login />} />
        <Route path='/recepcionista' element={<Recepcionista />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App;
