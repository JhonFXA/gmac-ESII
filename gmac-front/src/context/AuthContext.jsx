import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [token, setToken] = useState(localStorage.getItem("token") || null);
    const [perfil, setPerfil] = useState(localStorage.getItem("perfil") || null);

    function login(token, perfil) {
        setToken(token);
        setPerfil(perfil);
        localStorage.setItem("token", token);
        localStorage.setItem("perfil", perfil);
    }

    function logout() {
        setToken(null);
        setPerfil(null);
        localStorage.removeItem("token");
        localStorage.removeItem("perfil");
    }

    return (
        <AuthContext.Provider value={{ token, perfil, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}