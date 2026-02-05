import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import { useAuth } from "@/app/providers/AuthContext";
import { useState } from "react";
import GerarRelatorio from "../components/GerarRelatorio";
import styles from "../style/relatorio.module.css"

import { Link } from "react-router-dom";


export default function DashboardRelatorio() {
  const { token } = useAuth();

  const [tipoPeriodo, setTipoPeriodo] = useState("");
  const [ano, setAno] = useState("");
  const [valorPeriodo, setValorPeriodo] = useState("");

  const podeBuscar =
    token &&
    tipoPeriodo &&
    ano &&
    (tipoPeriodo === "ANO" || valorPeriodo);

  return (
    <div className={styles.container}>
      <Header />
      <main className={styles.main}>
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;{" "}
            <Link to="/painel-principal/gerar-relatorio">Gerar Relatório</Link>
          </p>
        </div>
        <div style={{ padding: 24 }}>
          <h1>Dashboard de Relatórios</h1>

          {/* ================= FILTROS ================= */}
          <div style={{ display: "flex", gap: 16, marginBottom: 24 }}>
            {/* Tipo de período */}
            <select className={styles.select}
              value={tipoPeriodo}
              onChange={(e) => {
                setTipoPeriodo(e.target.value);
                setValorPeriodo("");
              }}
            >
              <option value="">Selecione o período</option>
              <option value="MES">Mês</option>
              <option value="TRIMESTRE">Trimestre</option>
              <option value="SEMESTRE">Semestre</option>
              <option value="ANO">Ano</option>
            </select>

            {/* Ano */}
            <select className={styles.select} value={ano} onChange={(e) => setAno(e.target.value)}>
              <option value="">Selecione o ano</option>
              {Array.from({ length: 5 }).map((_, i) => {
                const year = 2026 - i;
                return (
                  <option key={year} value={year}>
                    {year}
                  </option>
                );
              })}
            </select>

            {/* Valor dinâmico */}
            {tipoPeriodo !== "ANO" && tipoPeriodo && (
              <select
                className={styles.select}
                value={valorPeriodo}
                onChange={(e) => setValorPeriodo(e.target.value)}
              >
                <option value="">Selecione</option>

                {tipoPeriodo === "MES" &&
                  Array.from({ length: 12 }).map((_, i) => (
                    <option key={i + 1} value={i + 1}>
                      {new Date(0, i).toLocaleString("pt-BR", {
                        month: "long",
                      })}
                    </option>
                  ))}

                {tipoPeriodo === "TRIMESTRE" &&
                  [1, 2, 3, 4].map((t) => (
                    <option key={t} value={t}>
                      {t}º Trimestre
                    </option>
                  ))}

                {tipoPeriodo === "SEMESTRE" &&
                  [1, 2].map((s) => (
                    <option key={s} value={s}>
                      {s}º Semestre
                    </option>
                  ))}
              </select>
            )}
          </div>

          {/* ================= GRÁFICOS ================= */}
          {podeBuscar ? (
            <GerarRelatorio
              ano={Number(ano)}
              tipo={tipoPeriodo}
              valor={tipoPeriodo === "ANO" ? 1 : Number(valorPeriodo)}
            />
          ) : (
            <p>Selecione o período e o ano para gerar os gráficos</p>
          )}

        </div>
      </main>
      <Footer />
    </div>
  );
}
