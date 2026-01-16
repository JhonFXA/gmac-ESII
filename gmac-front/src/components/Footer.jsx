import ufsLogo from "../assets/ufs-logo.png";

function Footer(){
    return (
        <footer className="footer">
            <div className="footer-info">
                <img src={ufsLogo} alt="Logo da UFS" />
                <p>Desenvolvido pela <br/>Universidade Federal de Sergipe (UFS)</p>
                <p>@2026 GMAC</p>
            </div>
            <div className="footer-links">
                <a href="#">Termos de uso</a>
                <a href="#">Política de privacidade</a>
                <a href="#">Ajuda</a>
            </div>
        </footer>
    )
}

export default Footer;