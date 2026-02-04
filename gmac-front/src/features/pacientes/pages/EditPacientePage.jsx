import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import { useAuth } from "@/app/providers/AuthContext";

import { usePacienteDetails } from "../hooks/usePacienteDetails";
import { useUpdatePaciente } from "../hooks/useUpdatePaciente";
import { useAddPacienteEndereco } from "../hooks/useAddPacienteEndereco";
import { useAddPacienteDocumento } from "../hooks/useAddPacienteDocumento";

import { buscarCep } from "@/features/pacientes/services/buscaViaCep";


import styles from "@/features/users/style/create-user.module.css";

function cleanPayload(obj) {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => {
      if (v === null || v === undefined) return false;
      if (typeof v === "string" && v.trim() === "") return false;
      return true;
    })
  );
}

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

function isEnderecoCompleto(e) {
  return (
    !!e.cep &&
    !!e.estado &&
    !!e.cidade &&
    !!e.bairro &&
    !!e.logradouro &&
    !!e.numero
  );
}

function isEnderecoVazio(e) {
  return Object.values(e).every((v) => !String(v ?? "").trim());
}

export default function EditPacientePage() {
  const { token } = useAuth();
  const { cpf } = useParams();
  const [cepErrors, setCepErrors] = useState({});
  const queryClient = useQueryClient();

  const pacienteQuery = usePacienteDetails(cpf, token);

  const updateMutation = useUpdatePaciente(token, cpf);
  const addEnderecoMutation = useAddPacienteEndereco(token, cpf);
  const addDocumentoMutation = useAddPacienteDocumento(token, cpf);

  const initialState = {
    nome: "",
    cpf: "",
    email: "",
    telefone: "",
    dataNascimento: "",
    sexo: "",
    estadoCivil: "",
    enderecosExistentes: [],
    enderecosNovos: [emptyEndereco()],
    documento: null,
  };

  const [formData, setFormData] = useState(initialState);
  const [localStatus, setLocalStatus] = useState(null);

  useEffect(() => {
    const data = pacienteQuery.data;
    if (!data) return;

    const existentes = Array.isArray(data.enderecos) ? data.enderecos : [];

    setFormData((prev) => ({
      ...prev,
      nome: data.nome ?? "",
      cpf: data.cpf ?? cpf ?? "",
      email: data.email ?? "",
      telefone: data.telefone ?? "",
      dataNascimento: (data.dataNascimento ?? "").slice(0, 10),
      sexo: data.sexo ?? "",
      estadoCivil: data.estadoCivil ?? "",
      enderecosExistentes: existentes.map((e) => ({
        cep: e.cep ?? "",
        cidade: e.cidade ?? "",
        estado: e.estado ?? "",
        bairro: e.bairro ?? "",
        logradouro: e.logradouro ?? "",
        numero: e.numero ?? "",
        complemento: e.complemento ?? "",
      })),
      enderecosNovos: [emptyEndereco()],
      documento: null,
    }));
  }, [pacienteQuery.data, cpf]);


  const handleCepBlur = async (cep, idx) => {
    const dadosEndereco = await buscarCep(cep);

    if (!dadosEndereco) {
      setCepErrors((prev) => ({
        ...prev,
        [idx]: "CEP inválido ou não encontrado",
      }));

      setLocalStatus({
        type: "error",
        message: "CEP inválido ou não encontrado. Verifique e tente novamente.",
      });

      return;
    }

    //  CEP válido → limpa erro do CEP
    setCepErrors((prev) => {
      const copy = { ...prev };
      delete copy[idx];
      return copy;
    });

    //  LIMPA a mensagem global de erro
    setLocalStatus(null);

    setFormData((prev) => ({
      ...prev,
      enderecosNovos: prev.enderecosNovos.map((end, i) =>
        i === idx
          ? { ...end, ...dadosEndereco, readonlyViaCep: true }
          : end
      ),
    }));
  };




  function handleChange(e) {
    const { name, value } = e.target;

    if (name.startsWith("enderecosNovos.")) {
      const [, idxStr, field] = name.split(".");
      const idx = Number(idxStr);

      setFormData((prev) => ({
        ...prev,
        enderecosNovos: prev.enderecosNovos.map((end, i) => {
          if (i !== idx) return end;
          return {
            ...end,
            [field]: field === "estado" ? value.toUpperCase() : value,
          };
        }),
      }));
      if (field === "cep" && value.replace(/\D/g, "").length === 8) {
        const cepLimpo = value.replace(/\D/g, "");
        handleCepBlur(cepLimpo, idx)

        
        if (!cepLimpo) {
          setFormData((prev) => ({
            ...prev,
            enderecosNovos: prev.enderecosNovos.map((end, i) =>
              i === idx
                ? {
                  ...emptyEndereco(),
                }
                : end
            ),
          }));

          setCepErrors((prev) => {
            const copy = { ...prev };
            delete copy[idx];
            return copy;
          });

          setLocalStatus(null);
          return;
        }
      }
    }

    setFormData((prev) => ({
      ...prev,
      [name]: name === "nome" ? value.toUpperCase() : value,
    }));
  }

  function addNovoEndereco() {
    setFormData((prev) => ({
      ...prev,
      enderecosNovos: [...prev.enderecosNovos, emptyEndereco()],
    }));
  }

  function removeNovoEndereco(index) {
    setFormData((prev) => {
      if (prev.enderecosNovos.length <= 1) return prev;
      return {
        ...prev,
        enderecosNovos: prev.enderecosNovos.filter((_, i) => i !== index),
      };
    });
  }

  function handleDocumentoChange(e) {
    const file = e.target.files?.[0] ?? null;
    setFormData((prev) => ({ ...prev, documento: file }));
  }

  async function handleSubmit(e) {
    e.preventDefault();

    setLocalStatus(null);

    updateMutation.reset();
    addEnderecoMutation.reset();
    addDocumentoMutation.reset();

    const cpfNovo = formData.cpf;

    const payloadBase = {
      cpf: formData.cpf,
      nome: formData.nome,
      telefone: formData.telefone,
      email: formData.email,
      sexo: formData.sexo,
      estadoCivil: formData.estadoCivil,
      statusSolicitacao: pacienteQuery.data?.statusSolicitacao,
      dataNascimento: formData.dataNascimento,
    };

    const payload = cleanPayload(payloadBase);

    const temEnderecoNovoIncompleto = formData.enderecosNovos.some(
      (e) => !isEnderecoVazio(e) && !isEnderecoCompleto(e)
    );
    if (temEnderecoNovoIncompleto) {
      setLocalStatus({
        type: "error",
        message:
          "Há um novo endereço incompleto. Preencha todos os campos obrigatórios ou remova o bloco.",
      });
      return;
    }
    if (Object.keys(cepErrors).length > 0) {
      setLocalStatus({
        type: "error",
        message: "Existe CEP inválido nos endereços adicionados.",
      });
      return;
    }


    try {
      await updateMutation.mutateAsync({ payload });

      const novosParaEnviar = formData.enderecosNovos
        .filter((e) => !isEnderecoVazio(e))
        .filter(isEnderecoCompleto)
        .map((e) =>
          cleanPayload({
            cep: e.cep,
            cidade: e.cidade,
            estado: e.estado,
            bairro: e.bairro,
            logradouro: e.logradouro,
            numero: e.numero,
            complemento: e.complemento?.trim() ? e.complemento : null,
          })
        );

      for (const endPayload of novosParaEnviar) {
        await addEnderecoMutation.mutateAsync({ payload: endPayload });
      }

      if (formData.documento) {
        const fd = new FormData();
        fd.append("documento", formData.documento);
        await addDocumentoMutation.mutateAsync({ payload: fd });
        setFormData((prev) => ({ ...prev, documento: null }));
      }

      await queryClient.invalidateQueries({ queryKey: ["pacientes"] });
      await queryClient.invalidateQueries({ queryKey: ["paciente", cpf] });
      if (cpfNovo && cpfNovo !== cpf) {
        await queryClient.invalidateQueries({ queryKey: ["paciente", cpfNovo] });
      }

      await pacienteQuery.refetch();

      setFormData((prev) => ({ ...prev, enderecosNovos: [emptyEndereco()] }));

      setLocalStatus({
        type: "success",
        message: "Paciente editado com sucesso!",
      });
    } catch (err) {
      setLocalStatus({
        type: "error",
        message: err?.message || "Erro ao salvar alterações.",
      });
    }
  }

  const status = useMemo(() => {
    if (localStatus) return localStatus;
    if (pacienteQuery.isError)
      return { type: "error", message: pacienteQuery.error.message };
    return null;
  }, [localStatus, pacienteQuery.isError, pacienteQuery.error]);

  const isSaving =
    updateMutation.isPending ||
    addEnderecoMutation.isPending ||
    addDocumentoMutation.isPending;

  return (
    <div className={styles.container}>
      <Header />

      <main className={styles.main}>
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="/painel-principal/consultar-cadastro">
              Consultar Cadastro
            </Link>{" "}
            &gt; <Link to="">Editar Paciente</Link>
          </p>
        </div>

        {status?.message && (
          <div className={`status-msg ${status.type}`}>{status.message}</div>
        )}

        {pacienteQuery.isLoading ? (
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
                  required
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
                  required
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
                  required
                />
              </div>

              <div className={styles.numberField ?? styles.emailField}>
                <label>Telefone</label>
                <input
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
                  className={styles.select}
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

              {formData.enderecosExistentes.length > 0 && (
                <div style={{ borderTop: "1px solid #ccc", paddingTop: 12 }}>
                  <strong>Endereços cadastrados</strong>
                  {formData.enderecosExistentes.map((e, i) => (
                    <div
                      key={`${e.cep}-${e.logradouro}-${e.numero}-${i}`}
                      style={{
                        marginTop: 8,
                        padding: 8,
                        background: "#f3f3f3",
                      }}
                    >
                      <div>
                        <strong>CEP:</strong> {e.cep}
                      </div>
                      <div>
                        <strong>Logradouro:</strong> {e.logradouro},{" "}
                        {e.numero}
                      </div>
                      <div>
                        <strong>Bairro:</strong> {e.bairro}
                      </div>
                      <div>
                        <strong>Cidade/UF:</strong> {e.cidade} - {e.estado}
                      </div>
                      {e.complemento ? (
                        <div>
                          <strong>Complemento:</strong> {e.complemento}
                        </div>
                      ) : null}
                    </div>
                  ))}
                </div>
              )}

              <div style={{ borderTop: "1px solid #ccc", paddingTop: 12 }}>
                <strong>Adicionar novos endereços</strong>

                {formData.enderecosNovos.map((end, idx) => (
                  <div key={idx} style={{ marginTop: 12 }}>
                    <div
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                      }}
                    >
                      <span>Endereço novo {idx + 1}</span>
                      <button
                        type="button"
                        onClick={() => removeNovoEndereco(idx)}
                        disabled={formData.enderecosNovos.length <= 1}
                        style={{ cursor: "pointer" }}
                      >
                        Remover
                      </button>
                    </div>

                    <div className={styles.emailField}>
                      <label>CEP</label>
                      <input
                        className={styles.input}
                        value={end.cep}
                        onChange={handleChange}
                        type="text"
                        name={`enderecosNovos.${idx}.cep`}
                      />
                    </div>


                    <div className={styles.nameField}>
                      <label>Logradouro</label>
                      <input
                        className={styles.input}
                        value={end.logradouro}
                        readOnly={end.readonlyViaCep}
                        onChange={handleChange}
                        type="text"
                        name={`enderecosNovos.${idx}.logradouro`}
                      />
                    </div>

                    <div className={styles.cpfField}>
                      <label>Número</label>
                      <input
                        className={styles.input}
                        value={end.numero}
                        onChange={handleChange}
                        type="text"
                        name={`enderecosNovos.${idx}.numero`}
                      />
                    </div>

                    <div className={styles.emailField}>
                      <label>Complemento</label>
                      <input
                        className={styles.input}
                        value={end.complemento}
                        onChange={handleChange}
                        type="text"
                        name={`enderecosNovos.${idx}.complemento`}
                      />
                    </div>

                    <div className={styles.emailField}>
                      <label>Bairro</label>
                      <input
                        className={styles.input}
                        value={end.bairro}
                        readOnly={end.readonlyViaCep}
                        onChange={handleChange}
                        type="text"
                        name={`enderecosNovos.${idx}.bairro`}
                      />
                    </div>

                    <div className={styles.emailField}>
                      <label>Cidade</label>
                      <input
                        className={styles.input}
                        value={end.cidade}
                        readOnly={end.readonlyViaCep}
                        onChange={handleChange}
                        type="text"
                        name={`enderecosNovos.${idx}.cidade`}
                      />
                    </div>

                    <div className={styles.cpfField}>
                      <label>Estado (UF)</label>
                      <input
                        className={styles.input}
                        value={end.estado}
                        readOnly={end.readonlyViaCep}
                        onChange={handleChange}
                        type="text"
                        name={`enderecosNovos.${idx}.estado`}
                        maxLength={2}
                      />
                    </div>
                  </div>
                ))}

                <div style={{ marginTop: 12 }}>
                  <button
                    type="button"
                    onClick={addNovoEndereco}
                    style={{ cursor: "pointer" }}
                  >
                    + Adicionar outro endereço
                  </button>
                </div>
              </div>

              <div className={styles.emailField} style={{ marginTop: 12 }}>
                <label>Novo Documento (PDF)</label>
                <input
                  className={styles.input}
                  type="file"
                  accept="application/pdf"
                  onChange={handleDocumentoChange}
                />
                {formData.documento?.name && (
                  <small>{formData.documento.name}</small>
                )}
              </div>

              <div className={styles.submitButtonContainer}>
                <button
                  className={styles.submitButton}
                  type="submit"
                  disabled={isSaving}
                >
                  {isSaving ? "Salvando..." : "Salvar alterações"}
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
