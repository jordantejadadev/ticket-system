import { useEffect, useState } from "react";
import { getMe } from "../services/authService";

export const useAuth = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const data = await getMe();
                setUser(data);
            } catch(error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        }
        fetchUser();
    }, []);

    return {
        user,
        loading
    }
}