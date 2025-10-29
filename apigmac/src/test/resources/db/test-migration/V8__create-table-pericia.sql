CREATE TABLE pericia(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    data_pericia DATE NOT NULL,
    status_pericia VARCHAR(40) NOT NULL,
    id_paciente UUID,
    id_usuario UUID,
    id_documentacao UUID,
    CONSTRAINT fk2_paciente
                        FOREIGN KEY (id_paciente)
                        REFERENCES paciente(id),
    CONSTRAINT fk6_usuario
                        FOREIGN KEY (id_usuario)
                        REFERENCES usuario(id),
    CONSTRAINT fk_documentacao
                        FOREIGN KEY (id_documentacao)
                        REFERENCES documentacao(id)
);