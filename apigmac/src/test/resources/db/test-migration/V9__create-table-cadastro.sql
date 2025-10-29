CREATE TABLE cadastro(
    id_paciente UUID PRIMARY KEY,
    id_usuario UUID,
    data_cadastro DATE NOT NULL,
    CONSTRAINT fk3_paciente
                FOREIGN KEY (id_paciente)
                REFERENCES paciente(id),
    CONSTRAINT fk7_usuario
                FOREIGN KEY (id_usuario)
                REFERENCES usuario(id)
);