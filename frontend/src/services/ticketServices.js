// const API_URL = import.meta.env.VITE_API_URL;
import { API_URL } from "./api";
// import { getAuthHeaders } from "./auth";
import { fetchWithRefresh } from "./authService";

// export const getTickets = async ({page, estado, search}) => {
//   const response = await fetch(`${API_URL}/tickets?page=${page}&size=5&estado=${estado}&search=${search}`,{
//     headers: getAuthHeaders(),
//   });

//   if (!response.ok) {
//     throw new Error("Error cargando tickets");
//   }

//   return response.json();
// };

// export const updateTicketEstado = async (id, estado) => {
//   const response = await fetch(`${API_URL}/tickets/${id}/estado`, {
//     method: "PATCH",
//     headers: getAuthHeaders(),
//     body: JSON.stringify({ estado }),
//   });

//   if(!response.ok) {
//     throw new Error("Error actualizando estado");
//   }

//   return response.json();
// }

// export const deleteTicketById = async (id) => {
//   const response = await fetch(`${API_URL}/tickets/${id}`, {
//     method: "DELETE",
//     headers: getAuthHeaders(),
//   });

//   if(!response.ok) {
//     throw new Error("Error eliminando ticket");
//   }
// }

export const getTickets = async ({ page, estado, search }) => {
  const response = await fetchWithRefresh(
    `${API_URL}/tickets?page=${page}&size=5&estado=${estado}&search=${search}`,    
  );

  if (!response.ok) {
    throw new Error("Error cargando tickets");
  }

  return response.json();
};

export const updateTicketEstado = async (id, estado) => {
  const response = await fetchWithRefresh(`${API_URL}/tickets/${id}/estado`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ estado }),
  });

  if (!response.ok) {
    throw new Error("Error actualizando estado");
  }

  return response.json();
};

export const deleteTicketById = async (id) => {
  const response = await fetchWithRefresh(`${API_URL}/tickets/${id}`, {
    method: "DELETE",    
  });

  if (!response.ok) {
    throw new Error("Error eliminando ticket");
  }
};

export const markTicketAsSeen = async (id) => {
  const response = await fetchWithRefresh(`${API_URL}/tickets/${id}/seen`, {
    method: "PATCH",    
  });

  if (!response.ok) {
    throw new Error("Error marcando ticket como visto");
  }

  return response.json();
};

export const logout = async () => {
  await fetch(`${API_URL}/auth/logout`, {
    method: "POST",
    credentials: "include",
  });
};
