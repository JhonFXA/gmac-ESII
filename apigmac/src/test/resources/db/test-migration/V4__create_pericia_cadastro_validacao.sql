CREATE TABLE IF NOT EXISTS pericia (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    data_pericia DATE NOT NULL,
    status_pericia VARCHAR(40) NOT NULL,

    paciente_id UUID NOT NULL,
    usuario_id UUID NOT NULL,

    CONSTRAINT fk_pericia_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id),

    CONSTRAINT fk_pericia_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS cadastro (
    paciente_id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    data_cadastro DATE NOT NULL,

    CONSTRAINT fk_cadastro_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id),

    CONSTRAINT fk_cadastro_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS validacao_documentacao (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    data_validacao DATE NOT NULL,
    status_validacao VARCHAR(40) NOT NULL,

    paciente_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    documentacao_id UUID NOT NULL,

    CONSTRAINT fk_validacao_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id),

    CONSTRAINT fk_validacao_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id),

    CONSTRAINT fk_validacao_documentacao
        FOREIGN KEY (documentacao_id)
        REFERENCES documentacao(id)
);

ALTER TABLE documentacao
ADD COLUMN IF NOT EXISTS status_documentacao VARCHAR(20) NOT NULL,
ADD COLUMN IF NOT EXISTS data_envio DATE NOT NULL;

ALTER TABLE paciente
ADD COLUMN IF NOT EXISTS nome VARCHAR(100) NOT NULL;
