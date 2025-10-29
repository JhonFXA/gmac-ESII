import { useState } from "react";

export function Form() {
  const [login, setLogin] = useState("");
  const [senha, setSenha] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ login, senha }),
      });

      if (!response.ok) {
        setMessage("Login ou senha inválidos");
        return;
      }

      const data = await response.json();

      localStorage.setItem("token", data.token);

      setMessage("Login bem-sucedido!");

      setTimeout(() => {
        window.location.href = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
      }, 2000);

    } catch (err) {
      setMessage("Erro de conexão: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
      <div>
        <label htmlFor="login" className="block mb-1">Login:</label>
        <input
          type="text"
          id="login"
          value={login}
          onChange={(e) => setLogin(e.target.value)}
          className="w-72 h-10 p-2 border rounded"
        />
      </div>
      <div>
        <label htmlFor="senha" className="block mb-1">Senha:</label>
        <input
          type="password"
          id="senha"
          value={senha}
          onChange={(e) => setSenha(e.target.value)}
          className="w-72 h-10 p-2 border rounded"
        />
      </div>
      <input
        type="submit"
        value={loading ? "Enviando..." : "Login"}
        className="w-72 h-10 bg-blue-500 text-white rounded cursor-pointer hover:bg-blue-600"
        disabled={loading}
      />
      {message && <p className="mt-2">{message}</p>}
    </form>
  );
}
