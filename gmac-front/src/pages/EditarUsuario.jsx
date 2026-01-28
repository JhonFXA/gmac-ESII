import Header from '../components/Header.jsx';
import Footer from '../components/Footer.jsx';
import '../css/user-registration-form.css';

import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useParams, Link } from 'react-router-dom';

export default function EditarUsuario() {
  const { token } = useAuth();
  const API_BASE_URL = import.meta.env.VITE_API_URL;
  const { cpf } = useParams();

  const [loading, setLoading] = useState(true);
  const [statusMessage, setStatusMessage] = useState('');
  const [statusType, setStatusType] = useState(''); // 'success' | 'error'

  const [formData, setFormData] = useState({
    nome: '',
    cpf: '',
    email: '',
    role: '',
    birthdate: '',
    username: '',
    specialty: '',
    password: '',
    repeatPassword: ''
  });

  useEffect(() => {
    let mounted = true;

    async function loadUser() {
      try {
        setLoading(true);
        setStatusMessage('');

        const response = await fetch(`${API_BASE_URL}/usuario/buscar/${cpf}`, {
          method: 'GET',
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const data = await response.json();

        if (!mounted) return;

        setFormData(prev => ({
          ...prev,
          nome: data.nome ?? '',
          cpf: data.cpf ?? cpf,
          email: data.email ?? '',
          role: data.perfil ?? data.role ?? '',
          birthdate: (data.dataNascimento ?? data.birthdate ?? '').slice(0, 10),
          username: data.login ?? '',
          specialty: data.especializacao ?? data.specialty ?? '',
          password: '',
          repeatPassword: ''
        }));
      } catch (err) {
        if (mounted) {
          setStatusMessage('Erro ao carregar usuário.');
          setStatusType('error');
        }
      } finally {
        if (mounted) setLoading(false);
      }
    }

    if (token && cpf) loadUser();

    return () => {
      mounted = false;
    };
  }, [API_BASE_URL, token, cpf]);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'nome' ? value.toUpperCase() : value,
    }));
  }

    function cleanPayload(obj) {
        return Object.fromEntries(
            Object.entries(obj).filter(([_, v]) => {
                if (v === null || v === undefined) return false;
                if (typeof v === "string" && v.trim() === "") return false;
                return true;
            })
        );
    }

  async function handleSubmit(e) {
    e.preventDefault();

    if (formData.password !== formData.repeatPassword) {
      setStatusMessage('As senhas não conferem');
      setStatusType('error');
      return;
    }

    const payloadBase = {
        login: formData.username,
        email: formData.email,
        cpf: formData.cpf,
        nome: formData.nome,
        perfil: formData.role,
        dataNascimento: formData.birthdate,
        senha: formData.password,
        ...(formData.role === "MEDICO" && formData.specialty?.trim()? { especializacao: formData.specialty.trim() }: {})
    };

    const payload = cleanPayload(payloadBase);
    console.log(payload)

    try {
      const response = await fetch(`${API_BASE_URL}/usuario/alterar/${cpf}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        setStatusMessage(errorData.erro ?? 'Erro ao editar usuário.');
        setStatusType('error');
        return;
      }

      setStatusMessage('Usuário editado com sucesso!');
      setStatusType('success');
      setFormData(prev => ({ ...prev, password: '', repeatPassword: '' }));
    } catch {
      setStatusMessage('Erro inesperado ao editar usuário.');
      setStatusType('error');
    }
  }

  return (
    <div className="container">
      <Header />
      <main className="main-nocentered-container">
        <div className="breadcumb">
          <p>
            <Link to="/painel-principal">Painel Principal</Link> &gt;
            <Link to="/painel-principal/gerenciar-usuarios"> Gerenciar Usuários</Link> &gt;
            <Link to=""> Editar Usuário</Link>
          </p>
        </div>

        {statusMessage && (
          <div className={`status-msg ${statusType}`}>
            {statusMessage}
          </div>
        )}

        {loading ? (
          <p>Carregando...</p>
        ) : (
          <div className="user-registration-container">
            <form onSubmit={handleSubmit}>
              <div className="field name-field">
                <label>Nome Completo</label>
                <input className="input uppercase" value={formData.nome} onChange={handleChange} type="text" name="nome"/>
              </div>

              <div className="field cpf-field">
                <label>CPF</label>
                <input className="input" value={formData.cpf} onChange={handleChange} type="text" name="cpf"/>
              </div>

              <div className="field email-field">
                <label>Email</label>
                <input className="input" value={formData.email} onChange={handleChange} type="email" name="email"/>
              </div>

              <div className="role-field">
                <label>Função</label>
                <select className="input role-input" name="role" value={formData.role} 
                      onChange={(e) => {
                          handleChange(e); 
                          const isMedico = e.target.value === "MEDICO";
                          if (!isMedico) {
                              setFormData(prev => ({ ...prev, specialty: '' })); // ✅ limpa
                          }

                      }} required>
                      <option value="" disabled>Selecione a função</option>
                      <option value="ADMINISTRADOR">Administrador</option>
                      <option value="RECEPCIONISTA">Recepcionista</option>
                      <option value="MEDICO">Médico</option>
                  </select>
              </div>

              <div className="birthdate-field">
                <label>Data de Nascimento</label>
                <input className="input" value={formData.birthdate} onChange={handleChange} type="date" name="birthdate"/>
              </div>

              <div className="user-spec-row">
                <div className="username-field">
                  <label>Nome de Usuário</label>
                  <input className="input" value={formData.username} onChange={handleChange} type="text" name="username"/>
                </div>

                {formData.role == "MEDICO" && (
                    <div className="specialty-field">
                    <label>Especialização</label>
                    <input
                        type="text"
                        placeholder="Insira a especialização do médico"
                        className="input"
                        name="specialty"
                        value={formData.specialty ?? ""}
                        onChange={handleChange}
                    required/>
                    </div>
                )}
              </div>
              <div className="password-field">
                <label>Nova Senha (opcional)</label>
                <input className="input" value={formData.password} onChange={handleChange} type="password" name="password"/>
              </div>

              <div className="repeat-password-field">
                <label>Repetir Senha</label>
                <input className="input" value={formData.repeatPassword} onChange={handleChange} type="password" name="repeatPassword" />
              </div>

              <div className="submit-button-container">
                <button className="submit-button" type="submit">Editar</button>
              </div>
            </form>
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
}
