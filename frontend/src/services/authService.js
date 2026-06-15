// const API_URL = import.meta.env.VITE_API_URL;
import { API_URL } from "./api";

export const fetchWithRefresh = async (url, options = {}) => {
  let response = await fetch(url, {
    ...options,
    credentials: "include",
  });

  if (response.status === 401 && !url.endsWith("/auth/refresh")) {
    const refreshResponse = await fetch(`${API_URL}/auth/refresh`, {
      method: "POST",
      credentials: "include",
    });

    if (!refreshResponse.ok) {
      window.location.href = "/login";
      return response;
    }

    response = await fetch(url, {
      ...options,
      credentials: "include",
    });
  }

  return response;
};

export const getMe = async () => {
  const response = await fetchWithRefresh(`${API_URL}/auth/me`);

  if (!response.ok) {
    return null;
  }

  return response.json();
};

export const isAdmin = (user) => {
  return user?.role === "ADMIN";
};
