import Header from '../components/Header.jsx';
import Footer from '../components/Footer.jsx';
import ScrollListPericia from '../components/ScrollListPericias.jsx';
import { Link } from 'react-router-dom';

export default function GerenciarPericias(){
    return (
        <div className="container">
            <Header />
            <main className="main-nocentered-container">
                <div className="breadcumb">
                    <p>
                        <Link to="/painel-principal">Painel Principal</Link> &gt; 
                        <Link to=""> Gerenciar Pericias</Link>
                    </p>
                </div>
                <ScrollListPericia />
            </main>
            <Footer />
        </div>
    )
}