import Header from '../components/Header.jsx';
import Footer from '../components/Footer.jsx';
import ScrollList from '../components/ScrollList.jsx';

export default function GerenciarUsuarios(){
    return (
        <div className="container">
            <Header />
            <main className="main-nocentered-container">
                <div className="breadcumb">
                    <p>
                        <a href="/painel-principal">Painel Principal</a> &gt; <a href="/painel-principal/gerenciar-usuarios">Gerenciar Usuários</a>
                    </p>
                </div>
                <ScrollList />
            </main>
            <Footer />
        </div>
    )
}