import { useMemo, useState } from "react";
import { Link } from "react-router-dom";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import { useAuth } from "@/app/providers/AuthContext";

import { useCreatePaciente } from "../hooks/useCreatePaciente";

import styles from "@/features/users/style/create-user.module.css";

export default function CreatePacientePage() {
  const { token } = useAuth();
  const createMutation = useCreatePaciente(token);

  const initialState = {
    nome: "",
    cpf: "",
    email: "",
    telefone: "",
    dataNascimento: "",
    sexo: "",
    estadoCivil: "",
  };

  const [formData, setFormData] = useState(initialState);

  function handleChange(e) {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: name === "nome" ? value.toUpperCase() : value,
    }));
  }

  async function handleSubmit(e) {
    e.preventDefault();

    const payload = {
      nome: formData.nome,
      cpf: formData.cpf,
      email: formData.email,
      telefone: formData.telefone,
      dataNascimento: formData.dataNascimento,
      sexo: formData.sexo,
      estadoCivil: formData.estadoCivil,
    };

    try {
      await createMutation.mutateAsync({ payload });
      setFormData(initialState);
    } catch {
      // erro já fica em createMutation.error
    }
  }

  const status = useMemo(() => {
    if (createMutation.isSuccess) {
      return { type: "success", message: "Paciente cadastrado com sucesso!" };
    }
    if (createMutation.isError) {
      return { type: "error", message: createMutation.error.message };
    }
    return null;
  }, [createMutation.isSuccess, createMutation.isError, createMutation.error]);

  return (
    <div className={styles.container}>
      <Header />

      <main className={styles.main}>
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="">Cadastrar Paciente</Link>
          </p>
        </div>

        {status?.message && (
          <div className={`status-msg ${status.type}`}>{status.message}</div>
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
              />
            </div>

            <div className={styles.numberField ?? styles.emailField}>
              <label>Telefone</label>
              <input
                placeholder="(00) 00000-0000"
                className={styles.input}
                value={formData.telefone}
                onChange={handleChange}
                type="text"
                name="telefone"
                required
              />
            </div>

            <div className={styles.birthdateField}>
              <label>Data de Nascimento</label>
              <input
                className={styles.input}
                value={formData.dataNascimento}
                onChange={handleChange}
                type="date"
                name="dataNascimento"
                required
              />
            </div>

            {/* ✅ faltava onChange */}
            <div className={styles.sexoField ?? styles.roleField}>
              <label>Sexo</label>
              <select
                className={styles.select ?? styles.input}
                value={formData.sexo}
                onChange={handleChange}
                name="sexo"
                required
              >
                <option value="" disabled>
                  Selecione uma opção
                </option>
                <option value="Masculino">Masculino</option>
                <option value="Feminino">Feminino</option>
              </select>
            </div>

            {/* ✅ name corrigido + onChange */}
            <div className={styles.estadoCivilField}>
              <label>Estado Civil</label>
              <select
                className={styles.select}
                value={formData.estadoCivil}
                onChange={handleChange}
                name="estadoCivil"
              >
                <option value="" disabled>
                  Selecione uma opção
                </option>
                <option value="Solteiro">Solteiro</option>
                <option value="Casado">Casado</option>
                <option value="Divorciado">Divorciado</option>
                <option value="Separado Judicialmente">
                  Separado Judicialmente
                </option>
                <option value="Viúvo">Viúvo</option>
              </select>
            </div>

            <div className={styles.submitButtonContainer}>
              <button
                className={styles.submitButton}
                type="submit"
                disabled={createMutation.isPending}
              >
                {createMutation.isPending ? "Cadastrando..." : "Cadastrar"}
              </button>
            </div>
          </form>
        </div>
      </main>

      <Footer />
    </div>
  );
}
