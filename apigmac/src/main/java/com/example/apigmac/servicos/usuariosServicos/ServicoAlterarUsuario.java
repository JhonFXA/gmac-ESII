package com.example.apigmac.servicos.usuariosServicos;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.entidades.*;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioAdm;
import com.example.apigmac.repositorios.RepositorioMed;
import com.example.apigmac.repositorios.RepositorioRecepicionista;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.utils.ServicoVerificacao;
import com.example.apigmac.utils.CpfUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela alteração de dados cadastrais
 * e gerenciamento de perfil do usuário.
 */
@Service
public class ServicoAlterarUsuario {

    @Autowired
    ServicoVerificacao verificacao;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioRecepicionista repositorioRecepicionista;

    @Autowired
    private RepositorioMed repositorioMed;

    @Autowired
    private RepositorioAdm repositorioAdm;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Atualiza os dados do usuário identificado pelo CPF atual.
     */
    @Transactional
    public void alterarUsuario(AlterarUsuarioDTO dto, String cpfAtual) {

        // Garante que exista ao menos um dado para alteração
        if (dto == null) {
            throw new IllegalArgumentException("Digite o Campo que quer mudar");
        }

        // Normaliza o CPF para garantir consistência na busca
        String cpfNormalizado = CpfUtils.normalizar(cpfAtual);

        // Recupera o usuário a partir do CPF informado
        Usuario usuario = (Usuario) repositorioUsuario.findByCpf(cpfNormalizado);

        // Impede alterações em usuários inexistentes
        if (usuario == null) {
            throw new RuntimeException("Usuario não encontrado");
        }

        // Atualização de CPF
        if (dto.cpf() != null) {
            if (!verificacao.cpfValido(dto.cpf())) {
                throw new IllegalArgumentException("CPF invalido");
            }
            usuario.setCpf(CpfUtils.normalizar(dto.cpf()));
        }

        // Atualização de nome
        if (dto.nome() != null) {
            if (!verificacao.textoObrigatorioValido(dto.nome(), 3)) {
                throw new IllegalArgumentException("Nome inválido");
            }
            usuario.setNome(dto.nome());
        }

        // Atualização de e-mail com verificação de unicidade
        if (dto.email() != null) {
            Usuario existente = (Usuario) repositorioUsuario.findByEmail(dto.email());
            if (existente != null && !existente.getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
            if (!verificacao.emailValido(dto.email())) {
                throw new IllegalArgumentException("E-mail inválido ou domínio inexistente.");
            }
            usuario.setEmail(dto.email());
        }

        // Atualização de login com validação de duplicidade
        if (dto.login() != null) {
            Usuario existente = (Usuario) repositorioUsuario.findByLogin(dto.login());
            if (existente != null && !existente.getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Login já existe");
            }
            usuario.setLogin(dto.login());
        }

        // Atualização de perfil e gerenciamento das entidades específicas
        if (dto.perfil() != null) {
            switch (dto.perfil()) {

                case MEDICO -> {
                    // Converte o perfil para médico, se ainda não for
                    if (usuario.getPerfil() != Perfil.MEDICO) {
                        if (dto.especializacao() != null && !dto.especializacao().isEmpty()) {

                            // Cria e persiste o perfil médico
                            Medico medico = new Medico(usuario, dto.especializacao());
                            repositorioMed.save(medico);

                            // Remove o perfil anterior do usuário
                            switch (usuario.getPerfil()) {
                                case RECEPCIONISTA -> {
                                    Recepcionista recep =
                                            repositorioRecepicionista.findByUsuarioId(usuario.getId());
                                    if (recep != null) {
                                        repositorioRecepicionista.delete(recep);
                                    } else {
                                        throw new IllegalStateException(
                                                "Recepcionista não encontrado para exclusão.");
                                    }
                                }
                                case ADMINISTRADOR -> {
                                    Administrador adm =
                                            repositorioAdm.findByUsuarioId(usuario.getId());
                                    if (adm != null) {
                                        repositorioAdm.delete(adm);
                                    } else {
                                        throw new IllegalStateException(
                                                "Administrador não encontrado para exclusão.");
                                    }
                                }
                                default -> throw new IllegalStateException("Perfil antigo inválido");
                            }

                            usuario.setPerfil(dto.perfil());
                        } else {
                            throw new IllegalArgumentException(
                                    "Especialização do médico é obrigatória.");
                        }
                    }
                }

                case ADMINISTRADOR -> {
                    // Cria e persiste o perfil administrador
                    Administrador adm = new Administrador(usuario);
                    repositorioAdm.save(adm);

                    // Remove o perfil anterior
                    switch (usuario.getPerfil()) {
                        case MEDICO -> {
                            Medico med = repositorioMed.findByUsuarioId(usuario.getId());
                            if (med != null) {
                                repositorioMed.delete(med);
                            } else {
                                throw new IllegalStateException(
                                        "Medico não encontrado para exclusão.");
                            }
                        }
                        case RECEPCIONISTA -> {
                            Recepcionista recep =
                                    repositorioRecepicionista.findByUsuarioId(usuario.getId());
                            if (recep != null) {
                                repositorioRecepicionista.delete(recep);
                            } else {
                                throw new IllegalStateException(
                                        "Recepcionista não encontrado para exclusão.");
                            }
                        }
                        default -> throw new IllegalStateException("Perfil antigo inválido");
                    }

                    usuario.setPerfil(dto.perfil());
                }

                case RECEPCIONISTA -> {
                    // Cria e persiste o perfil recepcionista
                    Recepcionista recepcionista = new Recepcionista(usuario);
                    repositorioRecepicionista.save(recepcionista);

                    // Remove o perfil anterior
                    switch (usuario.getPerfil()) {
                        case MEDICO -> {
                            Medico med = repositorioMed.findByUsuarioId(usuario.getId());
                            if (med != null) {
                                repositorioMed.delete(med);
                            } else {
                                throw new IllegalStateException(
                                        "Medico não encontrado para exclusão.");
                            }
                        }
                        case ADMINISTRADOR -> {
                            Administrador adm =
                                    repositorioAdm.findByUsuarioId(usuario.getId());
                            if (adm != null) {
                                repositorioAdm.delete(adm);
                            } else {
                                throw new IllegalStateException(
                                        "Administrador não encontrado para exclusão.");
                            }
                        }
                        default -> throw new IllegalStateException("Perfil antigo inválido");
                    }

                    usuario.setPerfil(dto.perfil());
                }

                case INATIVO -> {
                    // Apenas altera o estado do usuário
                    usuario.setPerfil(dto.perfil());
                }
            }
        }

        // Atualização da data de nascimento
        if (dto.dataNascimento() != null) {
            if (!verificacao.dataNascimentoValida(dto.dataNascimento())) {
                throw new IllegalArgumentException("Data inválida");
            }
            usuario.setDataNascimento(dto.dataNascimento());
        }

        // Atualização de senha com criptografia
        if (dto.senha() != null) {
            if (!verificacao.senhaValida(dto.senha())) {
                throw new IllegalArgumentException("Senha inválida");
            }
            String senhaCriptografada = passwordEncoder.encode(dto.senha());
            usuario.setSenha(senhaCriptografada);
        }
    }
}
