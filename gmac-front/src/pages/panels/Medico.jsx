import '../../css/App.css';
import '../../css/dashboard.css';
import Header from '../../components/Header.jsx';
import Footer from '../../components/Footer.jsx';

function Medico() {
  return (
    <div className="container">
        <Header />
        <main className="main">
          <div className="button-collection">
            <button className="main-button">
              <i class="fa-solid fa-clipboard-check"></i>
              <p>Validar Documentação</p>
            </button>
            <button className="main-button">
              <i class="fa-regular fa-calendar"></i>
              <p>Gerenciar Agenda</p>
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

export default Medico;
