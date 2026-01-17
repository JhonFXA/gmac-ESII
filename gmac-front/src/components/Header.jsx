import logo from '../assets/gmac-logo2.png';
import { useState, useRef, useEffect } from 'react';
import { useAuth } from '../context/AuthContext.jsx';

function Header(){
    const [menuOpen, setMenuOpen] = useState(false);
    const menuRef = useRef();
    const {logout} = useAuth();

    const toggleMenu = () => {
        setMenuOpen(!menuOpen);
    };

    const handleLogout = () => {
        logout();
    }

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setMenuOpen(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    return (
        <header className='header'>
            <img height={100} src={logo} alt="Logo GMAC" />
            <nav className='nav-header'>
                <button className='header-btn notification-btn'><i className="fa-regular fa-bell"></i></button>
                <button onClick={toggleMenu} className='header-btn user-btn'><i className="fa-regular fa-user"></i></button>
            </nav>

            {menuOpen && (
                <div ref={menuRef} className="dropdown-menu">
                    <p className="user-icon">
                        <i style={{ fontSize: '90px' }} className="fa-regular fa-user"></i>
                    </p>
                    <button onClick={toggleMenu} className='close-menu-btn'>
                        <i className="fa-solid fa-xmark"></i>
                    </button>
                    <p className='username'>Usuário</p>
                    <ul className='menu-options'>
                        <li className='personal-info-option'>
                            <a href="">
                                <i className="fa-solid fa-user"></i>
                                <p>Informação Pessoal</p>
                            </a>
                        </li>
                        <li className='notifications-option'>
                            <a href="">
                                <i className="fa-solid fa-bell"></i>
                                <p>Avisos</p>
                            </a>
                        </li>
                        <li className='settings-option'>
                            <a href="">
                                <i className="fa-solid fa-gear"></i>
                                <p>Configuracões</p>
                            </a>
                        </li>
                        <li onClick={handleLogout} className='logout-option'>
                            <a href=''>
                                <i className="fa-solid fa-sign-out-alt"></i>
                                <p>Sair</p>
                            </a>
                        </li>
                    </ul>
                </div>
            )}
        </header>
    )
}

export default Header;