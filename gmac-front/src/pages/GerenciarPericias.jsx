import Header from '../components/Header.jsx';
import Footer from '../components/Footer.jsx';
import ScrollListPericia from '../components/ScrollListPericias.jsx';

export default function GerenciarPericias(){
    return (
        <div className="container">
            <Header />
            <main className="main-nocentered-container">
                <div className="breadcumb">
                    <p>
                        <a href="/painel-principal">Painel Principal</a> &gt; <a href="/painel-principal/gerenciar-pericias">Gerenciar Pericias</a>
                    </p>
                </div>
                <ScrollListPericia />
            </main>
            <Footer />
        </div>
    )
}