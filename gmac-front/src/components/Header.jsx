import '../css/App.css';
import logo from '../assets/gmac-logo2.png';

function Header(){
    return (
        <header className='header'>
            <img height={100} src={logo} alt="Logo GMAC" />
            <nav className='nav-header'>
                <button className='header-btn notification-btn'><i class="fa-regular fa-bell"></i></button>
                <button className='header-btn user-btn'><i class="fa-regular fa-user"></i></button>
            </nav>
        </header>
    )
}

export default Header;