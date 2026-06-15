import { useEffect, useState } from "react";
import StatCard from "../components/StatCard";
import DashboardLayout from "../layouts/DashboardLayout";
const API_URL = import.meta.env.VITE_API_URL;
import useDashboard from "../hooks/useDashboard";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  PieChart,
  Pie,
  Cell,
} from "recharts";

const Dashboard = () => {
  const { stats, loading } = useDashboard();

  const chartData = [
    {
      name: "Abiertos",
      value: stats.abiertos,
    },
    {
      name: "En progreso",
      value: stats.en_progreso,
    },
    {
      name: "Cerrados",
      value: stats.cerrados,
    },
  ];

  const COLORS = ["#ef4444", "#f59e0b", "#22c55e"];
  return (
    <DashboardLayout>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <StatCard title="Tickets Abiertos" value={stats.abiertos} />

        <StatCard title="En Progreso" value={stats.en_progreso} />

        <StatCard title="Tickets Cerrados" value={stats.cerrados} />
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        {/* BAR CHART */}
        <div className="bg-white rounded-2xl shadow-sm p-6 h-[400px]">
          <h2 className="text-lg font-semibold mb-6">Tickets por estado</h2>

          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData}>
              <XAxis dataKey="name" />

              <YAxis />

              <Tooltip />

              <Bar dataKey="value" radius={[8, 8, 0, 0]}>
                {chartData.map((_, index) => (
                  <Cell key={index} fill={COLORS[index]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* PIE CHART */}
        <div className="bg-white rounded-2xl shadow-sm p-6 h-[400px]">
          <h2 className="text-lg font-semibold mb-6">Distribución</h2>

          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={chartData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                outerRadius={120}
                label
              >
                {chartData.map((_, index) => (
                  <Cell key={index} fill={COLORS[index]} />
                ))}
              </Pie>

              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default Dashboard;
