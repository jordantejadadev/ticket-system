// const API_URL = import.meta.env.VITE_API_URL;
import { API_URL } from "./api";
import { fetchWithRefresh } from "./authService";
// import { getAuthHeaders } from "./auth";


// export const getStats = async () => {
//   const response = await fetch(`${API_URL}/tickets/stats`, {
//     headers: getAuthHeaders()
//   });

//   if(!response.ok) {
//     throw new Error("Error cargando stats");
//   }

//   return response.json();
// }

export const getStats = async () => {
  const response = await fetchWithRefresh(`${API_URL}/tickets/stats`, {
    credentials: "include"
  });

  if(!response.ok) {
    throw new Error("Error cargando stats");
  }

  return response.json();
}