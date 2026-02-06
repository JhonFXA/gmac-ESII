import { useState } from "react";
import { Link } from "react-router-dom";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import { useAuth } from "@/app/providers/AuthContext";
import { useToast } from "@/app/providers/ToastProvider";

import { useCreatePaciente } from "../hooks/useCreatePaciente";
import { buscarCep } from "@/features/pacientes/services/buscaViaCep";

import styles from "@/features/users/style/create-user.module.css";

const emptyEndereco = () => ({
  cep: "",
  cidade: "",
  estado: "",
  bairro: "",
  logradouro: "",
  numero: "",
  complemento: "",
  readonlyViaCep: false,
});

export default function CreatePacientePage() {
  const { token } = useAuth();
  const toast = useToast();
  const createMutation = useCreatePaciente(token);

  const [cepErrors, setCepErrors] = useState({});

  const initialState = {
    nome: "",
    cpf: "",
    email: "",
    telefone: "",
    dataNascimento: "",
    sexo: "",
    estadoCivil: "",
    enderecos: [emptyEndereco()],
    documento: null,
  };

  const [formData, setFormData] = useState(initialState);

  const handleCepBlur = async (cep, idx) => {
    const dadosEndereco = await buscarCep(cep);

    if (!dadosEndereco) {
      setCepErrors((prev) => ({
        ...prev,
        [idx]: "CEP inválido ou não encontrado",
      }));
      toast.error("CEP inválido ou não encontrado. Verifique e tente novamente.");
      return;
    }

    // Se deu certo, limpa erro
    setCepErrors((prev) => {
      const copy = { ...prev };
      delete copy[idx];
      return copy;
    });

    setFormData((prev) => ({
      ...prev,
      enderecos: prev.enderecos.map((end, i) =>
        i === idx ? { ...end, ...dadosEndereco, readonlyViaCep: true } : end
      ),
    }));
  };

  function handleChange(e) {
    const { name, value } = e.target;

    // campos aninhados: enderecos.{index}.{campo}
    if (name.startsWith("enderecos.")) {
      const [, idxStr, field] = name.split(".");
      const idx = Number(idxStr);

      setFormData((prev) => ({
        ...prev,
        enderecos: prev.enderecos.map((end, i) => {
          if (i !== idx) return end;
          return {
            ...end,
            [field]: field === "estado" ? value.toUpperCase() : value,
          };
        }),
      }));

      if (field === "cep") {
        const cepLimpo = value.replace(/\D/g, "");

        if (cepLimpo.length === 8) {
          handleCepBlur(cepLimpo, idx);
        }

        // se apagou o CEP
        if (!cepLimpo) {
          setFormData((prev) => ({
            ...prev,
            enderecos: prev.enderecos.map((end, i) =>
              i === idx ? { ...emptyEndereco() } : end
            ),
          }));

          setCepErrors((prev) => {
            const copy = { ...prev };
            delete copy[idx];
            return copy;
          });

          return;
        }
      }

      return;
    }

    setFormData((prev) => ({
      ...prev,
      [name]: name === "nome" ? value.toUpperCase() : value,
    }));
  }

  function handleDocumentoChange(e) {
    const file = e.target.files?.[0] ?? null;
    setFormData((prev) => ({ ...prev, documento: file }));
  }

  function addEndereco() {
    setFormData((prev) => ({
      ...prev,
      enderecos: [...prev.enderecos, emptyEndereco()],
    }));
  }

  function removeEndereco(index) {
    setFormData((prev) => {
      // mantém pelo menos 1 endereço (backend exige)
      if (prev.enderecos.length <= 1) return prev;
      return {
        ...prev,
        enderecos: prev.enderecos.filter((_, i) => i !== index),
      };
    });

    // limpa erro do índice removido (se existir)
    setCepErrors((prev) => {
      if (!(index in prev)) return prev;
      const copy = { ...prev };
      delete copy[index];
      return copy;
    });
  }

  async function handleSubmit(e) {
    e.preventDefault();

    if (Object.keys(cepErrors).length > 0) {
      toast.error("Existe um CEP inválido nos endereços informados.");
      return;
    }

    const dados = {
      nome: formData.nome,
      cpf: formData.cpf,
      dataNascimento: formData.dataNascimento,
      telefone: formData.telefone,
      email: formData.email,
      sexo: formData.sexo,
      estadoCivil: formData.estadoCivil,
      enderecos: formData.enderecos.map((end) => ({
        cep: end.cep,
        cidade: end.cidade,
        estado: end.estado,
        bairro: end.bairro,
        logradouro: end.logradouro,
        numero: end.numero,
        complemento: end.complemento?.trim() ? end.complemento : null,
      })),
    };

    const fd = new FormData();
    fd.append(
      "dados",
      new Blob([JSON.stringify(dados)], { type: "application/json" })
    );

    if (formData.documento) {
      fd.append("documento", formData.documento);
    }

    try {
      await createMutation.mutateAsync({ payload: fd });
      setFormData(initialState);
      setCepErrors({});
    } catch (err) {
      //toast já trata os erros
    }
  }

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

            <div className={styles.sexoField}>
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
                <option value="MASCULINO">Masculino</option>
                <option value="FEMININO">Feminino</option>
                <option value="NAO_INFORMADO">Não Informado</option>
              </select>
            </div>

            <div className={styles.estadoCivilField}>
              <label>Estado Civil</label>
              <select
                className={styles.select}
                value={formData.estadoCivil}
                onChange={handleChange}
                name="estadoCivil"
                required
              >
                <option value="" disabled>
                  Selecione uma opção
                </option>
                <option value="SOLTEIRO">Solteiro(a)</option>
                <option value="CASADO">Casado(a)</option>
                <option value="DIVORCIADO">Divorciado(a)</option>
                <option value="VIUVO">Viúvo(a)</option>
                <option value="UNIAO_ESTAVEL">União Estável</option>
                <option value="OUTROS">Outros</option>
              </select>
            </div>

            {formData.enderecos.map((end, idx) => (
              <div
                key={idx}
                style={{ borderTop: "1px solid #ccc", paddingTop: 12 }}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                  }}
                >
                  <strong>Endereço {idx + 1}</strong>

                  <button
                    type="button"
                    onClick={() => removeEndereco(idx)}
                    disabled={formData.enderecos.length <= 1}
                    style={{ cursor: "pointer" }}
                  >
                    Remover
                  </button>
                </div>

                <div>
                  <label>CEP</label>
                  <input
                    placeholder="00000-000"
                    className={styles.input}
                    value={end.cep}
                    onChange={handleChange}
                    type="text"
                    name={`enderecos.${idx}.cep`}
                    required
                  />
                </div>

                <div>
                  <label>Logradouro</label>
                  <input
                    placeholder="Rua / Avenida"
                    className={styles.input}
                    value={end.logradouro}
                    readOnly={end.readonlyViaCep}
                    onChange={handleChange}
                    type="text"
                    name={`enderecos.${idx}.logradouro`}
                    required
                  />
                </div>

                <div className={styles.cpfField}>
                  <label>Número</label>
                  <input
                    placeholder="Nº"
                    className={styles.input}
                    value={end.numero}
                    onChange={handleChange}
                    type="text"
                    name={`enderecos.${idx}.numero`}
                    required
                  />
                </div>

                <div className={styles.emailField}>
                  <label>Complemento</label>
                  <input
                    placeholder="Apto, bloco, etc."
                    className={styles.input}
                    value={end.complemento}
                    onChange={handleChange}
                    type="text"
                    name={`enderecos.${idx}.complemento`}
                  />
                </div>

                <div className={styles.emailField}>
                  <label>Bairro</label>
                  <input
                    placeholder="Bairro"
                    className={styles.input}
                    value={end.bairro}
                    readOnly={end.readonlyViaCep}
                    onChange={handleChange}
                    type="text"
                    name={`enderecos.${idx}.bairro`}
                    required
                  />
                </div>

                <div className={styles.emailField}>
                  <label>Cidade</label>
                  <input
                    placeholder="Cidade"
                    className={styles.input}
                    value={end.cidade}
                    readOnly={end.readonlyViaCep}
                    onChange={handleChange}
                    type="text"
                    name={`enderecos.${idx}.cidade`}
                    required
                  />
                </div>

                <div className={styles.cpfField}>
                  <label>Estado (UF)</label>
                  <input
                    placeholder="UF"
                    className={styles.input}
                    value={end.estado}
                    readOnly={end.readonlyViaCep}
                    onChange={handleChange}
                    type="text"
                    name={`enderecos.${idx}.estado`}
                    maxLength={2}
                    required
                  />
                </div>
              </div>
            ))}

            <div style={{ marginTop: 12 }}>
              <button
                type="button"
                onClick={addEndereco}
                style={{ cursor: "pointer" }}
              >
                + Adicionar outro endereço
              </button>
            </div>

            <div style={{ marginTop: 12 }}>
              <label>Documento (PDF)</label>
              <input
                className={styles.input}
                type="file"
                accept="application/pdf"
                onChange={handleDocumentoChange}
                required
              />
              {formData.documento?.name && <small>{formData.documento.name}</small>}
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
