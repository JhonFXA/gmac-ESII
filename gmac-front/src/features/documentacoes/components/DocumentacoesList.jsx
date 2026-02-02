import { useAuth } from "@/app/providers/AuthContext";
import { useGerarUrlDocumentacao } from "../hooks/useGerarUrlDocumentacao";
import { useMemo } from "react";
import { useListarDocumentacao } from "../hooks/useListarDocumentacao";
import styles from "@/features/pericias/style/pericias-list.module.css";

export default function DocumentacoesList({ search }) {
    const { token } = useAuth();

    const {
        data: documentacoes = [],
        isLoading,
        error,
    } = useListarDocumentacao(token);

    const visualizarMutation = useGerarUrlDocumentacao(token);

    const documentacoesFiltradas = useMemo(() => {
        const query = (search ?? "").trim().toLowerCase();

        return documentacoes.filter((d) => {
            const paciente = (d.nomePaciente ?? "").toLowerCase();
            return !query || paciente.includes(query);
        });
    }, [documentacoes, search]);

    return (
        <div className={styles.scrollList}>
            <ul className={styles.listSection}>
                {isLoading && (
                    <li className={styles.emptyRow}>Carregando...</li>
                )}

                {error && (
                    <li className={styles.emptyRow}>
                        Erro: {error.message}
                    </li>
                )}

                {!isLoading && !error && documentacoesFiltradas.length === 0 && (
                    <li className={styles.listItem}>
                        <div className={styles.itemInfo}>
                            <p>Nenhuma Documentação encontrada.</p>
                        </div>
                    </li>
                )}

                {documentacoesFiltradas.map((d) => (
                    <li className={styles.listItem} key={d.id}>
                        <div className={styles.itemInfo}>
                            <p>{d.nome ?? "-"}</p>
                            <p>{d.cpf ?? "-"}</p>
                            <p
                                className={
                                    styles[`status${(d.status ?? "").toLowerCase()}`]
                                }
                            >
                                {d.status ?? "-"}
                            </p>
                            <p>
                                {d.dataEnvio
                                    ? new Date(d.dataEnvio).toLocaleDateString("pt-BR")
                                    : "-"}
                            </p>
                        </div>

                        <div className={styles.itemBtns}>
                            <button
                                type="button"
                                className={styles.viewBtn}
                                title="Ver Documentação"
                                disabled={visualizarMutation.isPending}
                                onClick={() =>
                                    visualizarMutation.mutate({ id: d.id })
                                }
                            >
                                <i className="fa-solid fa-magnifying-glass"></i>
                            </button>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
}
