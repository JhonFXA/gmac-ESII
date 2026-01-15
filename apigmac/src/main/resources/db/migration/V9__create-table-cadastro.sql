CREATE TABLE cadastro(
    id_paciente UUID PRIMARY KEY,
    id_usuario UUID NOT NULL,
    data_cadastro DATE NOT NULL,
    CONSTRAINT fk_cadastro_paciente
                FOREIGN KEY (id_paciente)
                REFERENCES paciente(id),
    CONSTRAINT fk_cadastro_usuario
                FOREIGN KEY (id_usuario)
                REFERENCES usuario(id)
);


