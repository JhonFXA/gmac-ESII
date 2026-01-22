package com.example.apigmac.servicos.usuariosServicos;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.utils.ServicoVerificacao;
import com.example.apigmac.utils.CpfUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ServicoAlterarUsuario {
    @Autowired
    ServicoVerificacao verificacao;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void alterarUsuario(AlterarUsuarioDTO dto){

        if (dto == null) {
            throw new IllegalArgumentException("Digite o Campo que quer mudar");
        }
        String cpfNormalizado = CpfUtils.normalizar((dto.cpf()));


        Usuario usuario = (Usuario) repositorioUsuario.findByCpf(cpfNormalizado);

        if (usuario == null) {
            throw new RuntimeException("Usuario não encontrado");
        }

        if (dto.nome() != null) {
            if (!verificacao.textoObrigatorioValido(dto.nome(), 3)) {
                throw new IllegalArgumentException("Nome inválido");
            }
            usuario.setNome(dto.nome());
        }
        if (dto.email() != null) {
            if (repositorioUsuario.findByEmail(dto.email()) != null){
                throw new IllegalArgumentException("Email já cadastrado");
            }
            if (!verificacao.emailValido(dto.email())){
                throw new IllegalArgumentException("E-mail inválido ou domínio inexistente.");
            }
            usuario.setEmail(dto.email());
        }
        if (dto.login() != null) {
            if (repositorioUsuario.findByLogin(dto.login()) != null) {
                throw new IllegalArgumentException("Login já existe");
            }
            usuario.setLogin(dto.login());
        }
        if (dto.perfil() != null) {
            usuario.setPerfil(dto.perfil());
        }
        if (dto.dataNascimento() != null) {
            if (!verificacao.dataNascimentoValida(dto.dataNascimento())) {
                throw new IllegalArgumentException("Data inválida");
            }
            usuario.setDataNascimento(dto.dataNascimento());
        }

        if (dto.senha() != null) {
            if (!verificacao.senhaValida(dto.senha())) {
                throw new IllegalArgumentException("Senha inválida");
            }
            String senhaCriptografada = passwordEncoder.encode(dto.senha());
            usuario.setSenha(senhaCriptografada);
        }

    }
}
