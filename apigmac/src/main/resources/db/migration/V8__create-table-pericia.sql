CREATE TABLE pericia (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    data_pericia DATE NOT NULL,
    status_pericia VARCHAR(40) NOT NULL,
    id_documentacao UUID NOT NULL,
    id_paciente UUID NOT NULL,
    id_usuario UUID NOT NULL,

    CONSTRAINT fk_pericia_paciente
        FOREIGN KEY (id_paciente)
        REFERENCES paciente(id),

    CONSTRAINT fk_pericia_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id),

    CONSTRAINT fk_pericia_documentacao
            FOREIGN KEY (id_documentacao)
            REFERENCES documentacao(id)
);
