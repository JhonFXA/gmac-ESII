import Header from '../components/Header.jsx';
import Footer from '../components/Footer.jsx';
import '../css/user-registration-form.css';

import { useState } from 'react';
import { useAuth } from '../context/AuthContext';   
import { data } from 'react-router-dom';

export default function CadastrarUsuario(){
    const { token, perfil } = useAuth();

    const [formData, setFormData] = useState({
        nome: '',
        cpf: '',
        email: '',
        role: '',
        birthdate: '',
        username: '',
        password: '',
        repeatPassword: ''
    });


    function handleChange(e) {
        const { name, value } = e.target;
        setFormData(prev => ({
        ...prev,
        [name]: value
        }));
    }
    
    async function handleSubmit(e) {
        e.preventDefault();

        if (formData.password !== formData.repeatPassword) {
            alert('As senhas não conferem');
            return;
        }

        const payload = {
            login: formData.username,
            email: formData.email,
            senha: formData.password,
            cpf: formData.cpf,
            nome: formData.nome,
            perfil: formData.role,
            dataNascimento: formData.birthdate,
        };

        try {
        const response = await fetch('http://localhost:8080/usuario/registro', {
            method: 'POST',
            headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error('Erro ao cadastrar usuário');
        }

        alert('Usuário cadastrado com sucesso!');
        } catch (error) {
        alert(error.message);
        }
    }

    return (
        <div className="container">
        <Header />
        <main className="main-nocentered-container">
            <div className="breadcumb">
                <p>
                    <a href="/painel-principal">Painel Principal</a> &gt; <a href="/painel-principal/cadastrar-usuario">Cadastrar Usuário</a></p>
            </div>
            <div className="user-registration-container">
                <form onSubmit={handleSubmit}>
                    <div className="field name-field">
                        <label>Nome Completo</label>
                        <input placeholder="Insira o nome completo" className="input name-input uppercase" value={formData.nome} onChange={handleChange} type="text" name="nome" required />
                    </div>
                    <div className="field cpf-field">
                        <label>CPF</label>
                        <input placeholder="000.000.000-00" className="input cpf-input" value={formData.cpf} onChange={handleChange} type="text" name="cpf" required />
                    </div>
                    <div className="field email-field">
                        <label>Email</label>
                        <input placeholder="Insira o email" className="input email-input" value={formData.email} onChange={handleChange} type="email" name="email" required />
                    </div>
                    <div className="role-field">
                        <label>Função</label>
                        <select className="input role-input" name="role" value={formData.role} onChange={handleChange} required>
                            <option value="" disabled>Selecione a função</option>
                            <option value="ADMINISTRADOR">Administrador</option>
                            <option value="RECEPCIONISTA">Recepcionista</option>
                            <option value="MEDICO">Médico</option>
                        </select>
                    </div>
                    <div className="birthdate-field">
                        <label>Data de Nascimento</label>
                        <input placeholder="__/__/____" className="input birthdate-input" value={formData.birthdate} onChange={handleChange} type="date" name="birthdate" required />
                    </div>
                    <div className="username-field">
                        <label>Nome de Usuário</label>
                        <input placeholder="Insira o nome de usuário" className="input username-input" value={formData.username} onChange={handleChange} type="text" name="username" required />
                    </div>
                    <div className="password-field">
                        <label>Senha</label>
                        <input placeholder="Insira a senha" className="input password-input" value={formData.password} onChange={handleChange} type="password" name="password" required />
                    </div>
                    <div className="repeat-password-field">
                        <label>Repetir Senha</label>
                        <input placeholder="Repita a senha" className="input repeat-password-input" value={formData.repeatPassword} onChange={handleChange} type="password" name="repeatPassword" required />
                    </div>
                    <div className="submit-button-container">
                        <button className="submit-button" type="submit">Cadastrar</button>
                    </div>
                </form>
            </div>
        </main>
        <Footer />
        </div>
    )
}