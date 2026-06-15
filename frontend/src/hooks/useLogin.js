import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/loginService";
import toast from "react-hot-toast";
import { useAuth } from "../context/AuthContext";
import { getMe } from "../services/authService";

export const useLogin = () => {
    const navigate = useNavigate();

    const { setUser } = useAuth();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleLogin = async (e) => {
        e.preventDefault();

        try {
            await login(email, password);
            const userData = await getMe();
            setUser(userData);
            toast.success("Login correcto");
            navigate("/");
        } catch(error) {
            toast.error(error.message);
        }
    }

    return {
        email,
        setEmail,
        password,
        setPassword,
        handleLogin
    };
};