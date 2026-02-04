import { useEffect, useState } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import { useAuth } from "@/app/providers/AuthContext";

import { useDocumentacaoDetails } from "../hooks/useDocumentacaoDetails";
import { useGerarUrlDocumentacao } from "../hooks/useGerarUrlDocumentacao";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";

import styles from "../style/pagina-documentacao.module.css";

export default function PaginaDocumentacao() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { token } = useAuth();

  const { data: doc, isLoading, error } = useDocumentacaoDetails(id, token);

  const gerarUrlMutation = useGerarUrlDocumentacao(token);
  const [pdfUrl, setPdfUrl] = useState(null);

  useEffect(() => {
    if (!id) return;

    gerarUrlMutation
      .mutateAsync({ id })
      .then((data) => {
        const url = typeof data === "string" ? data : data?.url;
        setPdfUrl(url || null);
      })
      .catch(() => setPdfUrl(null));
  }, [id]);

  if (isLoading) return <p>Carregando...</p>;
  if (error) return <p>Erro: {error.message}</p>;

  const dataEnvioBR = doc?.dataEnvio
    ? new Date(doc.dataEnvio).toLocaleDateString("pt-BR")
    : "-";

  return (
    <div className={styles.container}>
      <Header />
      <div className={styles.main}>
        <div className={styles.noteSection}>
          <div className="breadcumb">
            <p>
              <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
              <Link to="/painel-principal/validar-documentacoes">Validar Documentações</Link> &gt;{" "}
              <Link to="">{id}</Link>
            </p>
          </div>
          <div>
            <div>
              <strong>Paciente:</strong> {doc?.nomePaciente ?? doc?.nome ?? "-"}
            </div>
            <div>
              <strong>CPF:</strong> {doc?.cpf ?? "-"}
            </div>
            <div>
              <strong>Status:</strong> {doc?.status ?? "-"}
            </div>
            <div>
              <strong>Data de envio:</strong> {dataEnvioBR}
            </div>
          </div>
          <div>
            <input type="text" name="" id="" />
          </div>
        </div>
        <div className={styles.buttonSection}>
          <button className={styles.openFileBtn}>
            Abrir Documento
          </button>
          <button className={styles.approveBtn}>
            Aprovar Documento
          </button>
          <button className={styles.disapproveBtn}>
            Reprovar Documento
          </button>
          <button className={styles.scheduleBtn}>
            Agendar Perícia
          </button>
        </div>
      </div>
      <Footer />
    </div>
  );
}
