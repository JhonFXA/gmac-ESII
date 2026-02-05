import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import { useMemo, useState } from "react";
import { Link } from "react-router-dom";

import { useAuth } from "@/app/providers/AuthContext";
import { useCreateUser } from "../hooks/useCreateUser";

import styles from "../style/create-user.module.css";

export default function CadastrarUsuario() {
  const { token } = useAuth();

  const initialState = {
    nome: "",
    cpf: "",
    email: "",
    role: "",
    birthdate: "",
    username: "",
    password: "",
    repeatPassword: "",
    specialty: "",
  };

  const [formData, setFormData] = useState(initialState);
  const [specialty, setSpecialty] = useState(false);

  const createUserMutation = useCreateUser(token);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === "nome" ? value.toUpperCase() : value,
    }));
  }

  const senhasDiferentes =
    !!formData.repeatPassword && formData.password !== formData.repeatPassword;

  async function handleSubmit(e) {
    e.preventDefault();

    if (formData.password !== formData.repeatPassword) return;

    const payload = {
      login: formData.username,
      email: formData.email,
      senha: formData.password,
      cpf: formData.cpf,
      nome: formData.nome,
      perfil: formData.role,
      dataNascimento: formData.birthdate,
      ...(formData.role === "MEDICO" && formData.specialty?.trim()
        ? { especializacao: formData.specialty.trim() }
        : {}),
    };

    try {
      await createUserMutation.mutateAsync({ payload });
      setFormData(initialState);
      setSpecialty(false);
    } catch {
      // o toast já mostra a mensagem do backend (error.message)
    }
  }

  return (
    <div className={styles.container}>
      <Header />

      <main className={styles.main}>
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="/painel-principal/cadastrar-usuario">Cadastrar Usuário</Link>
          </p>
        </div>

        {senhasDiferentes && (
          <div className={`status-msg error`}>As senhas não conferem</div>
        )}

        <div className={styles.userRegistrationContainer}>
          <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.nameField}>
              <label>Nome Completo</label>
              <input
                placeholder="Insira o nome completo"
                className={`${styles.input} ${styles.uppercase}`}
                value={formData.nome}
                onChange={handleChange}
                type="text"
                name="nome"
                required
              />
            </div>

            <div className={styles.cpfField}>
              <label>CPF</label>
              <input
                placeholder="000.000.000-00"
                className={styles.input}
                value={formData.cpf}
                onChange={handleChange}
                type="text"
                name="cpf"
                required
                autoComplete="off"
              />
            </div>

            <div className={styles.emailField}>
              <label>Email</label>
              <input
                placeholder="Insira o email"
                className={styles.input}
                value={formData.email}
                onChange={handleChange}
                type="email"
                name="email"
                required
                autoComplete="email"
              />
            </div>

            <div className={styles.roleField}>
              <label>Função</label>
              <select
                className={styles.select}
                name="role"
                value={formData.role}
                onChange={(e) => {
                  handleChange(e);
                  const isMedico = e.target.value === "MEDICO";
                  setSpecialty(isMedico);

                  if (!isMedico) {
                    setFormData((prev) => ({ ...prev, specialty: "" }));
                  }
                }}
                required
              >
                <option value="" disabled>
                  Selecione a função
                </option>
                <option value="ADMINISTRADOR">Administrador</option>
                <option value="RECEPCIONISTA">Recepcionista</option>
                <option value="MEDICO">Médico</option>
              </select>
            </div>

            <div className={styles.birthdateField}>
              <label>Data de Nascimento</label>
              <input
                className={styles.input}
                value={formData.birthdate}
                onChange={handleChange}
                type="date"
                name="birthdate"
                required
                autoComplete="bday"
              />
            </div>

            <div className={styles.userSpecRow}>
              <div className={styles.usernameField}>
                <label>Nome de Usuário</label>
                <input
                  placeholder="Insira o nome de usuário"
                  className={`${styles.input} ${styles.usernameInput}`}
                  value={formData.username}
                  onChange={handleChange}
                  type="text"
                  name="username"
                  required
                  autoComplete="username"
                />
              </div>

              {specialty && (
                <div className={styles.specialtyField}>
                  <label>Especialização</label>
                  <input
                    type="text"
                    placeholder="Insira a especialização do médico"
                    className={styles.input}
                    name="specialty"
                    value={formData.specialty ?? ""}
                    onChange={handleChange}
                    required
                    autoComplete="off"
                  />
                </div>
              )}
            </div>

            <div className={styles.passwordField}>
              <label>Senha</label>
              <input
                placeholder="Insira a senha"
                className={styles.input}
                value={formData.password}
                onChange={handleChange}
                type="password"
                name="password"
                required
                autoComplete="new-password"
              />
            </div>

            <div className={styles.repeatPasswordField}>
              <label>Repetir Senha</label>
              <input
                placeholder="Repita a senha"
                className={styles.input}
                value={formData.repeatPassword}
                onChange={handleChange}
                type="password"
                name="repeatPassword"
                required
                autoComplete="new-password"
              />
            </div>

            <div className={styles.submitButtonContainer}>
              <button
                className={styles.submitButton}
                type="submit"
                disabled={createUserMutation.isPending || senhasDiferentes}
              >
                {createUserMutation.isPending ? "Cadastrando..." : "Cadastrar"}
              </button>
            </div>
          </form>
        </div>
      </main>

      <Footer />
    </div>
  );
}
