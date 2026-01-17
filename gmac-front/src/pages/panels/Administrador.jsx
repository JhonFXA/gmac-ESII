import Header from '../../components/Header.jsx';
import Footer from '../../components/Footer.jsx';

function Administrador() {
  return (
    <div className="container">
        <Header />
        <main className="main">
          <div className="button-collection">
            <button className="main-button">
              <i className="fas fa-user-plus"></i>
              <p>Cadastrar Usuário</p>
              </button>
            <button className="main-button">
              <i className="fas fa-users"></i>
              <p>Gerenciar Usuários</p>
              </button>
            <button className="main-button">
              <i class="fa-regular fa-calendar"></i>
              <p>Gerenciar Perícias</p>
              </button>
            <button className="main-button">
              <i class="fa-solid fa-chart-column"></i>
              <p>Gerar Relatório</p>
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

export default Administrador;
