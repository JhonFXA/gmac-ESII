import { useAuth } from "@/app/providers/AuthContext";
import { useRelatorioDashboard } from "../hooks/useGerarRelatorio";
import { useMemo } from "react";
import styles from "../style/relatorio.module.css"


import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from "recharts";

/**
 * Monta dados para gráfico removendo o TOTAL da visualização
 * e usando ele apenas como referência
 */
function buildChartData(obj = {}, labelsMap, totalKey) {
  const total = obj[totalKey] ?? 0;

  const data = Object.entries(obj)
    .filter(([key]) => key !== totalKey)
    .map(([key, value]) => ({
      key,
      name: labelsMap[key] ?? key,
      value,
    }));

  return { total, data };
}

const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042"];

export default function GerarRelatorio({ ano, tipo, valor }) {
  const { token } = useAuth();

  const { data, isLoading, error } = useRelatorioDashboard({
    ano,
    tipo,
    valor,
    token,
  });

  const { resumoBeneficios, resumoDocumentacoes } = data ?? {};

  const pacientesChart = useMemo(() => {
    if (!resumoBeneficios) return { total: 0, data: [] };

    return buildChartData(
      resumoBeneficios,
      {
        pacientesBeneficiados: "Beneficiados",
        pacientesNaoBeneficiados: "Não beneficiados",
        pacientesPendentes: "Pendentes",
      },
      "totalPacientes"
    );
  }, [resumoBeneficios]);

  const documentacoesChart = useMemo(() => {
    if (!resumoDocumentacoes) return { total: 0, data: [] };

    return buildChartData(
      resumoDocumentacoes,
      {
        aprovadas: "Aprovadas",
        reprovadas: "Reprovadas",
        pendentes: "Pendentes",
      },
      "totalDocumentacoes"
    );
  }, [resumoDocumentacoes]);

  if (isLoading) return <p>Carregando gráficos...</p>;
  if (error) return <p>Erro ao carregar relatório</p>;

  return (
    <div style={{ padding: 24 }}>
      {/* ================= PACIENTES ================= */}
      <h2 className = {styles.nameRelatorio}>Pacientes:</h2>

      {pacientesChart.total === 0 ? (
        <p>Sem dados de pacientes para exibição</p>
      ) : (
        <div style={{ display: "flex", gap: 32, height: 300 }}>
          {/* Barras */}
          <ResponsiveContainer width="50%" height="100%">
            <BarChart data={pacientesChart.data}>
              <XAxis dataKey="name" />
              <YAxis
                domain={[0, pacientesChart.total]}
                allowDecimals={false}
              />
              <Tooltip />
              <Bar dataKey="value">
                {pacientesChart.data.map((_, index) => (
                  <Cell
                    key={index}
                    fill={COLORS[index % COLORS.length]}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>

          {/* Pizza */}
          <ResponsiveContainer width="50%" height="100%">
            <PieChart>
              <Pie
                data={pacientesChart.data}
                dataKey="value"
                nameKey="name"
                outerRadius={100}
                label={({ name, percent }) =>
                  `${name} ${(percent * 100).toFixed(0)}%`
                }
              >
                {pacientesChart.data.map((_, index) => (
                  <Cell
                    key={index}
                    fill={COLORS[index % COLORS.length]}
                  />
                ))}
              </Pie>
              <Tooltip
                formatter={(value) =>
                  `${value} (${(
                    (value / pacientesChart.total) *
                    100
                  ).toFixed(1)}%)`
                }
              />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}

      {/* ================= DOCUMENTAÇÕES ================= */}
      <h2 className = {styles.nameRelatorio}>Documentações:</h2>

      {documentacoesChart.total === 0 ? (
        <p>Sem dados de documentações para exibição</p>
      ) : (
        <div style={{ display: "flex", gap: 32, height: 300 }}>
          {/* Barras */}
          <ResponsiveContainer width="50%" height="100%">
            <BarChart data={documentacoesChart.data}>
              <XAxis dataKey="name" />
              <YAxis
                domain={[0, documentacoesChart.total]}
                allowDecimals={false}
              />
              <Tooltip />
              <Bar dataKey="value">
                {documentacoesChart.data.map((_, index) => (
                  <Cell
                    key={index}
                    fill={COLORS[index % COLORS.length]}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>

          {/* Pizza */}
          <ResponsiveContainer width="50%" height="100%">
            <PieChart>
              <Pie
                data={documentacoesChart.data}
                dataKey="value"
                nameKey="name"
                outerRadius={100}
                label={({ name, percent }) =>
                  `${name} ${(percent * 100).toFixed(0)}%`
                }
              >
                {documentacoesChart.data.map((_, index) => (
                  <Cell
                    key={index}
                    fill={COLORS[index % COLORS.length]}
                  />
                ))}
              </Pie>
              <Tooltip
                formatter={(value) =>
                  `${value} (${(
                    (value / documentacoesChart.total) *
                    100
                  ).toFixed(1)}%)`
                }
              />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  );
}
