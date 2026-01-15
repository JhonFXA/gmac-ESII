CREATE TABLE documentacao (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    caminho VARCHAR(100) NOT NULL,

    id_paciente UUID NOT NULL,

    CONSTRAINT fk_documentacao_paciente
        FOREIGN KEY (id_paciente)
        REFERENCES paciente(id)
        ON DELETE CASCADE
);
