CREATE TABLE validacao_documentacao (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    data_validacao DATE NOT NULL,
    status_validacao VARCHAR(40) NOT NULL,
    observacao VARCHAR(200) NOT NULL,

    id_paciente UUID NOT NULL,
    id_usuario UUID NOT NULL,
    id_documentacao UUID NOT NULL,

    CONSTRAINT fk_validacao_paciente
        FOREIGN KEY (id_paciente)
        REFERENCES paciente(id),

    CONSTRAINT fk_validacao_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id),

    CONSTRAINT fk_validacao_documentacao
        FOREIGN KEY (id_documentacao)
        REFERENCES documentacao(id)
);
