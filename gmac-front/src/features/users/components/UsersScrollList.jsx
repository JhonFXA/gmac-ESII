import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/app/providers/AuthContext";

import { useUsers } from "../hooks/useUsers";
import UserDetailsModal from "./UserDetailsModal";

import styles from "../style/users-scroll-list.module.css";

export default function UsersScrollList({ search, perfisSelecionados }) {
  const { token } = useAuth();
  const navigate = useNavigate();

  const { data: usuarios = [], isLoading, error } = useUsers(token);


  const [cpfSelecionado, setCpfSelecionado] = useState(null);

  const usuariosFiltrados = useMemo(() => {
    const q = search.trim().toLowerCase();

    return usuarios.filter((u) => {
      const login = (u.login ?? u.username ?? "").toLowerCase();
      const perfil = (u.perfil ?? u.role ?? "").toUpperCase();
      const cpf = (u.cpf ?? "").toLowerCase();

      const matchTexto = !q || login.includes(q) || cpf.includes(q);
      const matchPerfil =
        !perfisSelecionados ||
        perfisSelecionados.size === 0 ||
        perfisSelecionados.has(perfil);

      return matchTexto && matchPerfil;
    });
  }, [usuarios, search, perfisSelecionados]);

  return (
    <>
      <UserDetailsModal
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

          {!isLoading && !error && usuariosFiltrados.length === 0 && (
            <li className={styles.emptyRow}>Nenhum usuário encontrado.</li>
          )}

          {usuariosFiltrados.map((u) => (
            <li
              className={styles.listItem}
              key={u.id ?? u.cpf ?? u.login ?? u.username}
            >
              <div className={styles.itemInfo}>
                <p>{u.login ?? u.username ?? "-"}</p>
                <p>{u.perfil ?? u.role ?? "-"}</p>
                <p>{u.cpf ?? "-"}</p>
              </div>

              <div className={styles.itemBtns}>
                <button
                  className={styles.iconBtn}
                  type="button"
                  title="Editar"
                  onClick={() =>
                    navigate(
                      `/painel-principal/gerenciar-usuarios/editar-usuario/${u.cpf}`
                    )
                  }
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
