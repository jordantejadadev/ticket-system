// import { Navigate } from "react-router-dom";

// const ProtectedRoute = ({ children }) => {

//     const token = localStorage.getItem("token");

//     if(!token) {
//         return <Navigate to="/login" />
//     }

//     return children;
// }

// export default ProtectedRoute;

// import { useEffect, useState } from "react";
// import { Navigate } from "react-router-dom";
// import { getMe } from "../services/authService";

// const ProtectedRoute = ({ children }) => {
//     const [loading, setLoading] = useState(true);
//     const [authenticated, setAuthenticated] = useState(false);

//     useEffect(() => {
//         const checkAuth = async () => {
//             const user = await getMe();

//             setAuthenticated(!!user);

//             setLoading(false);
//         }

//         checkAuth();
//     }, []);

//     if(loading) {
//         return <div>Cargando...</div>
//     }

//     if(!authenticated) {
//         return <Navigate to="/login" />
//     }

//     return children;
// }

// export default ProtectedRoute;

import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const ProtectedRoute = ({ children }) => {
    const { user, loading } = useAuth();

    if(loading) {
        return <div>Cargando...</div>
    }

    if(!user) {
        return <Navigate to="/login" />
    }

    return children
}

export default ProtectedRoute;