import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/app/providers/AuthContext";

import { usePacientes } from "../hooks/usePacientes";
import PacientesDetailsModal from "./PacienteDetailsModal";

import styles from "@/features/users/style/users-scroll-list.module.css";

export default function PacientesScrollList({ search, pacientesSelecionados,ordemNome }) {
  const { token } = useAuth();
  const navigate = useNavigate();

  const { data: pacientes = [], isLoading, error } = usePacientes(token);


  const [cpfSelecionado, setCpfSelecionado] = useState(null);

 const pacientesFiltrados = useMemo(() => {
  const q = search.trim().toLowerCase();

  let lista = pacientes.filter((u) => {
    const nome = (u.nome ?? "").toLowerCase();
    const status = (u.statusSolicitacao ?? "").toUpperCase();
    const cpf = (u.cpf ?? "").toLowerCase();

    const matchTexto = !q || nome.includes(q) || cpf.includes(q);
    const matchPerfil =
      !pacientesSelecionados ||
      pacientesSelecionados.size === 0 ||
      pacientesSelecionados.has(status);

    return matchTexto && matchPerfil;
  });

  if (ordemNome) {
    lista = [...lista].sort((a, b) => {
      const nomeA = (a.nome ?? "").toLowerCase();
      const nomeB = (b.nome ?? "").toLowerCase();

      return ordemNome === "ASC"
        ? nomeA.localeCompare(nomeB, "pt-BR")
        : nomeB.localeCompare(nomeA, "pt-BR");
    });
  }

  return lista;
}, [pacientes, search, pacientesSelecionados, ordemNome]);

  return (
    <>
      <PacientesDetailsModal
        cpf={cpfSelecionado}
        onClose={() => setCpfSelecionado(null)}
      />

      <div className={styles.scrollList}>
        <ul className={styles.listSection}>
          {isLoading && (
            <li className={styles.emptyRow}>Carregando...</li>
          )}

          {error && (
            <li className={styles.emptyRow}>Erro: {error.message}</li>
          )}

          {!isLoading && !error && pacientesFiltrados.length === 0 && (
            <li className={styles.emptyRow}>Nenhum paciente encontrado.</li>
          )}

          {pacientesFiltrados.map((u) => (
            <li
              className={styles.listItem}
              key={u.id ?? u.cpf ?? u.nome}
            >
              <div className={styles.itemInfo}>
                <p>{u.nome ?? "-"}</p>
                <p>{u.statusSolicitacao ?? "-"}</p>
                <p>{u.cpf ?? "-"}</p>
              </div>

              <div className={styles.itemBtns}>
                <button
                  onClick={()=> navigate(`/painel-principal/consultar-cadastro/editar-paciente/${u.cpf}`)}
                  className={styles.iconBtn}
                  type="button"
                  title="Editar"
                >
                  <i className="fa-solid fa-pen-to-square"></i>
                </button>

                <button
                  className={styles.iconBtn}
                  type="button"
                  title="Visualizar"
                  onClick={() => setCpfSelecionado(u.cpf)}
                >
                  <i className="fa-solid fa-magnifying-glass"></i>
                </button>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}
