import { useState } from "react";
import './css/App.css';

import logo from "./assets/gmac-logo.png";
import illustration from "./assets/illustration.svg";
import ufsLogo from "./assets/ufs-logo.png";
import {Form} from './components/Form.jsx';


function App() {
  return (
    <div className="container">

      <main className="">
          {/* Conteúdo da esquerda */}
          <section className="image-content">
            <img src={illustration} height={500} alt="" />
          </section>

          <section className="form-content">
            <div className="form-div">
              <img src={logo} height={200}/>
              <Form />
            </div>
          </section>
      </main>

      <footer className="footer">
        <div className="footer-info">
          <img src={ufsLogo} alt="Logo da UFS" />
          <p>Desenvolvido pela <br/>Universidade Federal de Sergipe (UFS)</p>
          <p>@2025 GMAC</p>
        </div>
        <div className="footer-links">
          <a href="#">Termos de uso</a>
          <a href="#">Ajuda</a>
        </div>
      </footer>
    </div>
  )
}

export default App;
