CREATE TABLE documentacao(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    data_pericia DATE NOT NULL,
    status_pericia VARCHAR(45) NOT NULL,
    id_paciente UUID NOT NULL,
    id_usuario UUID,
    CONSTRAINT fk_paciente
                    FOREIGN KEY (id_paciente)
                    REFERENCES paciente(id),
    CONSTRAINT fk_usuario
                        FOREIGN KEY (id_usuario)
                        REFERENCES usuario(id)
);