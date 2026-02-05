import logo from "@/assets/images/gmac-logo2.png";
import { useState, useRef, useEffect } from "react";
import { useLogout } from "@/features/auth/hooks/useLogout";

import styles from "./header.module.css";

function Header() {
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);

  const logout = useLogout();

  const toggleMenu = () => {
    setMenuOpen((v) => !v);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <header className={styles.header}>
      <img height={100} src={logo} alt="Logo GMAC" />

      <nav className={styles.navHeader}>
        <button
          onClick={toggleMenu}
          className={`${styles.headerBtn} ${styles.userBtn}`}
        >
          <i className="fa-regular fa-user"></i>
        </button>
      </nav>

      {menuOpen && (
        <div ref={menuRef} className={styles.dropdownMenu}>
          <p className={styles.userIcon}>
            <i style={{ fontSize: "90px" }} className="fa-regular fa-user"></i>
          </p>

          <button
            onClick={toggleMenu}
            className={styles.closeMenuBtn}
          >
            <i className="fa-solid fa-xmark"></i>
          </button>

          <p className={styles.username}>Usuário</p>

          <ul className={styles.menuOptions}>
            <li className={styles.personalInfoOption}>
              <button>
                <i className="fa-solid fa-user"></i>
                <p>Informação Pessoal</p>
              </button>
            </li>

            <li className={styles.logoutOption}>
              <button onClick={logout} className={styles.logoutBtn}>
                <i className="fa-solid fa-sign-out-alt"></i>
                <p>Sair</p>
              </button>
            </li>
          </ul>
        </div>
      )}
    </header>
  );
}

export default Header;
