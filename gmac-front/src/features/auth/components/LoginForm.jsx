import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/app/providers/AuthContext";
import { useLogin } from "../hooks/useLogin";

import styles from "../style/login-form.module.css";

export default function LoginForm() {
  const [login, setLogin] = useState("");
  const [senha, setSenha] = useState("");
  const [showSenha, setShowSenha] = useState(false);

  const navigate = useNavigate();
  const { login: authLogin } = useAuth();
  const loginMutation = useLogin();

  async function handleSubmit(e) {
    e.preventDefault();

    try {
      const data = await loginMutation.mutateAsync({ login, senha });
      authLogin(data.token, data.perfil);
      navigate("/painel-principal");
    } catch {}
  }

  const message = loginMutation.error?.message ?? "";

  return (
    <form className={styles.formContainer} onSubmit={handleSubmit}>
      <div className={styles.inputDiv}>
        <div className={styles.userField}>
          <label htmlFor="login">Usuário</label>
          <input
            type="text"
            id="login"
            value={login}
            onChange={(e) => setLogin(e.target.value)}
            className={styles.inputTxt}
            autoComplete="username"
          />
        </div>

        <div className={styles.passwordField}>
          <label htmlFor="senha">Senha</label>
          <input
            type={showSenha ? "text" : "password"}
            id="senha"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            className={styles.inputTxt}
            autoComplete="current-password"
          />
          <i
            className={`fa-solid ${
              showSenha ? "fa-eye-slash" : "fa-eye"
            } ${styles.viewIcon}`}
            onClick={() => setShowSenha((v) => !v)}
          />
        </div>

        {loginMutation.isError && (
          <p className={styles.errorMessage}>{message}</p>
        )}
      </div>

      <div className={styles.buttonDiv}>
        <input
          type="submit"
          value={loginMutation.isPending ? "Enviando..." : "Entrar"}
          className={styles.loginButton}
          disabled={loginMutation.isPending}
        />
        <button
          type="button"
          className={styles.loginButton}
          disabled={loginMutation.isPending}
        >
          Lembrar Senha
        </button>
      </div>
    </form>
  );
}
