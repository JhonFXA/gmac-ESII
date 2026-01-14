import { useState } from "react";

import '../css/default.css'
import '../css/Form.css'
export function Form() {
  const [login, setLogin] = useState("");
  const [senha, setSenha] = useState("");
  const [message, setMessage] = useState("");
  const [showSenha, setShowSenha] = useState(false);
  const [loading, setLoading] = useState(false);


  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ login, senha }),
      });

      if (!response.ok) {
        const error = await response.json();
        setMessage(error.Erro || "Login ou senha inválidos");
        return;
      }

      const data = await response.json();

      localStorage.setItem("token", data.token);

      setTimeout(() => {
        window.location.href = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
      }, 2000);

    } catch (err) {
      setMessage("Erro de conexão: " + err.message);
    } finally {
      setLoading(false);
    }
  };


  function printWindowSize() {
    console.log("Width: " + window.innerWidth + ", Height: " + window.innerHeight);
  }

  return (
    <form className="form-container" onSubmit={handleSubmit}>

      <div className="input-div">
        <div className="user-field">
          <label htmlFor="login" className="">Usuário</label>
          <input type="text" id="login" value={login} onChange={(e) => setLogin(e.target.value)}className="input-txt user-input"/>
        </div>
        <div className="password-field">
          <label htmlFor="senha" className="">Senha</label>
          <input type={showSenha ? "text" : "password"} id="senha" value={senha} onChange={(e) => setSenha(e.target.value)} className="input-txt password-input"/>
          <i className={`fa-solid ${ showSenha ? "fa-eye-slash" : "fa-eye" } view-icon`} onClick={() => setShowSenha(!showSenha)}></i>
        </div>
        <p className="error-message">{message}</p>
      </div>

      <div className="button-div">
        <input type="submit" value={loading ? "Enviando..." : "Entrar"} className="default-button"/>
        <button type="button" onClick={printWindowSize} className="default-button">Lembrar Senha</button>
      </div>

    </form>
  );
}
