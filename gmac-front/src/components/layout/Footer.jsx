import ufsLogo from "@/assets/images/ufs-logo.png";
import styles from './footer.module.css'
function Footer(){
    return (
        <footer className={styles.footer}>
            <div className={styles.footerInfo}>
                <img src={ufsLogo} alt="Logo da UFS" />
                <p>Desenvolvido pela <br/>Universidade Federal de Sergipe (UFS)</p>
                <p>@2026 GMAC</p>
            </div>
            <div className={styles.footerLinks}>
                <a href="#">Termos de uso</a>
                <a href="#">Política de privacidade</a>
                <a href="#">Ajuda</a>
            </div>
        </footer>
    )
}

export default Footer;