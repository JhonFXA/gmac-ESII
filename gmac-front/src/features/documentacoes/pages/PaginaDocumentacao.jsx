import { useEffect, useMemo, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { useAuth } from "@/app/providers/AuthContext";

import { useDocumentacaoDetails } from "../hooks/useDocumentacaoDetails";
import { useGerarUrlDocumentacao } from "../hooks/useGerarUrlDocumentacao";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";

import styles from "../style/pagina-documentacao.module.css";

function toBackendDateTime(datetimeLocal) {
  if (!datetimeLocal) return null;
  return datetimeLocal.replace("T", " ") + ":00";
}

export default function PaginaDocumentacao() {
  const { id } = useParams();
  const { token } = useAuth();

  const { data: doc, isLoading, error } = useDocumentacaoDetails(id, token);

  const gerarUrlMutation = useGerarUrlDocumentacao(token);
  const [pdfUrl, setPdfUrl] = useState(null);

  const [dialog, setDialog] = useState({ open: false, action: null });
  const [credenciais, setCredenciais] = useState({ login: "", senha: "" });
  const [agendamento, setAgendamento] = useState({ dataHora: "" });
  const [observacao, setObservacao] = useState("");

  const [showPassword, setShowPassword] = useState(false);

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

  function openDialog(action) {
    setDialog({ open: true, action });
    setCredenciais({ login: "", senha: "" });
    setAgendamento({ dataHora: "" });
    setShowPassword(false);
  }

  function closeDialog() {
    setDialog({ open: false, action: null });
  }

  const observacaoObrigatoria = true;

  const isConfirmDisabled = useMemo(() => {
    const obsOk = !observacaoObrigatoria || !!observacao.trim();

    if (dialog.action === "aprovar" || dialog.action === "reprovar") {
      return !obsOk || !credenciais.login.trim() || !credenciais.senha.trim();
    }

    if (dialog.action === "agendar") {
      return !obsOk || !agendamento.dataHora;
    }

    return true;
  }, [dialog.action, credenciais.login, credenciais.senha, agendamento.dataHora, observacao, observacaoObrigatoria]);

  const dialogTitle =
    dialog.action === "aprovar"
      ? "Confirmar aprovação"
      : dialog.action === "reprovar"
        ? "Confirmar reprovação"
        : dialog.action === "agendar"
          ? "Agendar perícia"
          : "";

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
              <Link to="/painel-principal/validar-documentacoes">
                Validar Documentações
              </Link>{" "}
              &gt; <Link to="">{id}</Link>
            </p>
          </div>

          <div className={styles.patientInfo}>
            <div>
              <strong>Paciente:</strong> {doc?.nomePaciente ?? doc?.nome ?? "-"}
            </div>
            <div>
              <strong>CPF:</strong> {doc?.cpf ?? "-"}
            </div>
            <div>
              <strong>Status da solicitação:</strong> {doc?.status ?? "-"}
            </div>
            <div>
              <strong>Data de envio:</strong> {dataEnvioBR}
            </div>
          </div>

          <div className={styles.noteField}>
            <label>
              <strong>Observação</strong>:
            </label>
            <textarea
              className={styles.noteArea}
              value={observacao}
              onChange={(e) => setObservacao(e.target.value)}
              required={observacaoObrigatoria}
            />
          </div>
        </div>

        <div className={styles.buttonSection}>
          <div className={styles.topBtns}>
            <button
              className={styles.openFileBtn}
              type="button"
              disabled={!pdfUrl}
              onClick={() => {
                if (!pdfUrl) return;
                window.open(pdfUrl, "_blank", "noopener,noreferrer");
              }}
            >
              <i className="fa-solid fa-arrow-up-right-from-square"></i>
              <p>Abrir Documento</p>
            </button>
          </div>

          <div className={styles.bottomBtns}>
            <button
              className={styles.approveBtn}
              type="button"
              onClick={() => openDialog("aprovar")}
            >
              <i className="fa-solid fa-check"></i>
              <p>Aprovar Documento</p>
            </button>

            <button
              className={styles.disapproveBtn}
              type="button"
              onClick={() => openDialog("reprovar")}
            >
              <i className="fa-solid fa-xmark"></i>
              <p>Reprovar Documento</p>
            </button>

            <button
              className={styles.scheduleBtn}
              type="button"
              onClick={() => openDialog("agendar")}
            >
              <i className="fa-regular fa-calendar-plus"></i>
              <p>Agendar Perícia</p>
            </button>
          </div>
        </div>

        {dialog.open && (
          <div className={styles.dialogOverlay} role="dialog" aria-modal="true">
            <div className={styles.dialogBox}>
              <div className={styles.dialogHeader}>
                <p>{dialogTitle}</p>
                <button
                  type="button"
                  className={styles.dialogCloseBtn}
                  onClick={closeDialog}
                  aria-label="Fechar"
                >
                  ×
                </button>
              </div>

              <div className={styles.dialogBody}>
                {(dialog.action === "aprovar" || dialog.action === "reprovar") && (
                  <>
                    <div className={styles.dialogField}>
                      <label>
                        <strong>Login</strong>
                      </label>
                      <input
                        className={styles.dialogInput}
                        type="text"
                        value={credenciais.login}
                        onChange={(e) =>
                          setCredenciais((p) => ({ ...p, login: e.target.value }))
                        }
                        autoComplete="off"
                      />
                    </div>

                    <div className={styles.dialogField}>
                      <label>
                        <strong>Senha</strong>
                      </label>
                      <input
                        className={styles.dialogInput}
                        type={showPassword ? "text" : "password"}
                        value={credenciais.senha}
                        onChange={(e) =>
                          setCredenciais((p) => ({ ...p, senha: e.target.value }))
                        }
                        autoComplete="new-password"
                      />
                      <button
                        type="button"
                        className={styles.showPasswordBtn}
                        onClick={() => setShowPassword((v) => !v)}
                        aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}
                      >
                        <i className={`fa-solid ${showPassword ? "fa-eye-slash" : "fa-eye"}`} />
                      </button>
                    </div>
                  </>
                )}

                {dialog.action === "agendar" && (
                  <div className={styles.dialogField}>
                    <label>Data e horário</label>
                    <input
                      className={styles.dialogInput}
                      type="datetime-local"
                      value={agendamento.dataHora}
                      onChange={(e) => setAgendamento({ dataHora: e.target.value })}
                    />
                  </div>
                )}
              </div>

              <div className={styles.dialogFooter}>
                <button
                  type="button"
                  className={styles.dialogCancelBtn}
                  onClick={closeDialog}
                >
                  Cancelar
                </button>

                <button
                  type="button"
                  className={styles.dialogConfirmBtn}
                  disabled={isConfirmDisabled}
                  onClick={() => {
                    const payload = {
                      login: credenciais.login.trim(),
                      senha: credenciais.senha,
                      documentacaoId: id,
                      observacao: observacao.trim(),
                      status:
                        dialog.action === "aprovar"
                          ? "APROVADA"
                          : dialog.action === "reprovar"
                            ? "REPROVADA"
                            : "PERICIA",
                      data:
                        dialog.action === "agendar"
                          ? toBackendDateTime(agendamento.dataHora)
                          : null,
                    };

                    closeDialog();
                    console.log(payload);
                  }}
                >
                  Confirmar
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      <Footer />
    </div>
  );
}
