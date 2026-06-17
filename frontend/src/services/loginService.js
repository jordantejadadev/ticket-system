// const API_URL = import.meta.env.VITE_API_URL;
import { API_URL } from "./api";

export const login = async (email, password) => {

    const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            email,
            password
        })
    });

    if(!response.ok) {
        throw new Error("Login incorrecto");
    }

    return response.json();
}