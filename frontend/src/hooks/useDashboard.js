import { useEffect, useState } from "react";
import { getStats } from "../services/dashboardService";

const useDashboard = () => {
  const [stats, setStats] = useState({
    abiertos: 0,
    en_progreso: 0,
    cerrados: 0
  });

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    setLoading(true);

    try {
      const data = await getStats();
      setStats(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }

  return {
    stats,
    loading
  };
};

export default useDashboard;