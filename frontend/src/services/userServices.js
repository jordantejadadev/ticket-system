import { getAuthHeaders } from "./auth";
// const API_URL = import.meta.env.VITE_API_URL;
import { API_URL } from "./api";
import { fetchWithRefresh } from "./authService";
// export const loadUsers = async (page) => {

//       const response = await fetch(`${API_URL}/users?page=${page}&size=5`, {
//         headers: getAuthHeaders(),
//       });

//       if (!response.ok) {
//         throw new Error("Error cargando usuarios");
//       }

//       return response.json();
//   };

// export const createUser = async (userData) => {

//       const response = await fetch(`${API_URL}/users`, {
//         method: "POST",
//         headers: getAuthHeaders(),
//         body: JSON.stringify(userData),
//       });

//       if (!response.ok) {
//         throw new Error("Error creando usuario");
//       }

//       return response.json();
//   };

// export const loadUsers = async (page) => {

//       const response = await fetch(`${API_URL}/users?page=${page}&size=5`, {
//         credentials: "include"
//       });

//       if (!response.ok) {
//         throw new Error("Error cargando usuarios");
//       }

//       return response.json();
//   };

export const loadUsers = async (page, search) => {
  const response = await fetchWithRefresh(`${API_URL}/users?page=${page}&size=5&search=${search}`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error("Error cargando usuarios");
  }

  return response.json();
};

export const createUser = async (userData) => {
  const response = await fetchWithRefresh(`${API_URL}/users`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw errorData;
  }

  return response.json();
};

export const deleteUserById = async (id) => {
  const response = await fetchWithRefresh(`${API_URL}/users/${id}`, {
    method: "DELETE",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error("Error eliminando usuario");
  }
};

export const updateUser = async (id, userData) => {
  const response = await fetchWithRefresh(`${API_URL}/users/${id}`, {
    method: "PUT",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw errorData;
  }

  return response.json();
};
