import styles from "../style/login-page.module.css";

import logo from "@/assets/images/gmac-logo.png";
import illustration from "@/assets/images/login-illustration.svg";

import LoginForm from "../components/LoginForm";
import Footer from "@/components/layout/Footer";

export default function LoginPage() {
  return (
    <div className={styles.container}>
      <main className={styles.main}>
        <section className={styles.imageContent}>
          <img src={illustration} height={500} alt="Ilustração" />
        </section>

        <section className={styles.formContent}>
          <div className={styles.formDiv}>
            <img src={logo} height={200} alt="GMAC" />
            <LoginForm />
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}