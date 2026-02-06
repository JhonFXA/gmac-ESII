import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import { useAuth } from "@/app/providers/AuthContext";

import { useUserDetails } from "../hooks/useUserDetails";
import { useUpdateUser } from "../hooks/useUpdateUser";

import { useToast } from "@/app/providers/ToastProvider";

import styles from "../style/create-user.module.css";

function cleanPayload(obj) {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => {
      if (v === null || v === undefined) return false;
      if (typeof v === "string" && v.trim() === "") return false;
      return true;
    })
  );
}

export default function EditUserPage() {
  const { token } = useAuth();
  const { cpf } = useParams();
  const toast = useToast();

  const userQuery = useUserDetails(cpf, token);
  const updateMutation = useUpdateUser(token, cpf);

  const [formData, setFormData] = useState({
    nome: "",
    cpf: "",
    email: "",
    role: "",
    birthdate: "",
    username: "",
    specialty: "",
    password: "",
    repeatPassword: "",
  });

  // quando carregar o usuário, preenche o form
  useEffect(() => {
    if (!userQuery.data) return;

    const data = userQuery.data;

    setFormData((prev) => ({
      ...prev,
      nome: data.nome ?? "",
      cpf: data.cpf ?? cpf ?? "",
      email: data.email ?? "",
      role: data.perfil ?? data.role ?? "",
      birthdate: (data.dataNascimento ?? data.birthdate ?? "").slice(0, 10),
      username: data.login ?? data.username ?? "",
      specialty: data.especializacao ?? data.specialty ?? "",
      password: "",
      repeatPassword: "",
    }));
  }, [userQuery.data, cpf]);

  const isMedico = formData.role === "MEDICO";

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === "nome" ? value.toUpperCase() : value,
    }));
  }

  async function handleSubmit(e) {
    e.preventDefault();

    if (formData.password !== formData.repeatPassword) {
      toast.error("As senhas não conferem");
      return;
    }

    const payloadBase = {
      login: formData.username,
      email: formData.email,
      cpf: formData.cpf,
      nome: formData.nome,
      perfil: formData.role,
      dataNascimento: formData.birthdate,
      senha: formData.password,
      ...(isMedico && formData.specialty?.trim()
        ? { especializacao: formData.specialty.trim() }
        : {}),
    };

    const payload = cleanPayload(payloadBase);

    try {
      await updateMutation.mutateAsync({ payload });
      setFormData((prev) => ({ ...prev, password: "", repeatPassword: "" }));
    } catch {
      // erro já vai ficar em updateMutation.error
    }
  }

  const status = useMemo(() => {
    if (formData.password !== formData.repeatPassword && formData.repeatPassword) {
      return;
    }
    return null;
  }, [
    formData.password,
    formData.repeatPassword,
    updateMutation.isSuccess,
    updateMutation.isError,
    updateMutation.error,
    userQuery.isError,
    userQuery.error,
  ]);

  return (
    <div className={styles.container}>
      <Header />

      <main className={styles.main}>
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="/painel-principal/gerenciar-usuarios">Gerenciar Usuários</Link> &gt;{" "}
            <Link to="">Editar Usuário</Link>
          </p>
        </div>

        {status?.message && (
          <div className={`status-msg ${status.type}`}>{status.message}</div>
        )}

        {userQuery.isLoading ? (
          <p>Carregando...</p>
        ) : (
          <div className={styles.userRegistrationContainer}>
            <form className={styles.form} onSubmit={handleSubmit}>
              <div className={styles.nameField}>
                <label>Nome Completo</label>
                <input
                  className={`${styles.input} ${styles.uppercase}`}
                  value={formData.nome}
                  onChange={handleChange}
                  type="text"
                  name="nome"
                />
              </div>

              <div className={styles.cpfField}>
                <label>CPF</label>
                <input
                  className={styles.input}
                  value={formData.cpf}
                  onChange={handleChange}
                  type="text"
                  name="cpf"
                />
              </div>

              <div className={styles.emailField}>
                <label>Email</label>
                <input
                  className={styles.input}
                  value={formData.email}
                  onChange={handleChange}
                  type="email"
                  name="email"
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
                    const nextRole = e.target.value;
                    if (nextRole !== "MEDICO") {
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
                  <option value="INATIVO">Inativo</option>
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
                />
              </div>

              <div className={styles.userSpecRow}>
                <div className={styles.usernameField}>
                  <label>Nome de Usuário</label>
                  <input
                    className={`${styles.input} ${styles.usernameInput}`}
                    value={formData.username}
                    onChange={handleChange}
                    type="text"
                    name="username"
                  />
                </div>

                {isMedico && (
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
                    />
                  </div>
                )}
              </div>

              <div className={styles.passwordField}>
                <label>Nova Senha (opcional)</label>
                <input
                  className={styles.input}
                  value={formData.password}
                  onChange={handleChange}
                  type="password"
                  name="password"
                  autoComplete="new-password"
                />
              </div>

              <div className={styles.repeatPasswordField}>
                <label>Repetir Senha</label>
                <input
                  className={styles.input}
                  value={formData.repeatPassword}
                  onChange={handleChange}
                  type="password"
                  name="repeatPassword"
                  autoComplete="new-password"
                />
              </div>

              <div className={styles.submitButtonContainer}>
                <button
                  className={styles.submitButton}
                  type="submit"
                  disabled={updateMutation.isPending}
                >
                  {updateMutation.isPending ? "Salvando..." : "Editar"}
                </button>
              </div>
            </form>
          </div>
        )}
      </main>

      <Footer />
    </div>
  );
}
