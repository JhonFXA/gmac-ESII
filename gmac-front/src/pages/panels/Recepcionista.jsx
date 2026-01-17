import '../../css/App.css';
import '../../css/dashboard.css';
import Header from '../../components/Header.jsx';
import Footer from '../../components/Footer.jsx';

function Recepcionista() {
  return (
    <div className="container">
        <Header />
        <main className="main">
          <div className="button-collection">
            <button className="main-button">
              <i className="fas fa-user-plus"></i>
              <p>Cadastrar Paciente</p>
              </button>
            <button className="main-button">
              <i class="fa-solid fa-magnifying-glass"></i>
              <p>Consultar Cadastro</p>
              </button>
            <button className="main-button">
              <i className="fas fa-user-edit"></i>
              <p>Atualizar Cadastro</p>
              </button>
            <button className="main-button">
              <i className="fas fa-bell"></i>
              <p>Gerar Notificação</p>
              </button>
          </div>
        </main>
        <Footer />
    </div>
  )
}

export default Recepcionista;
