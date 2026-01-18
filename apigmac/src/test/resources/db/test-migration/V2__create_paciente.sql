CREATE TABLE IF NOT EXISTS medico (
    id_usuario UUID PRIMARY KEY,
    especializacao VARCHAR(50) NOT NULL,
    CONSTRAINT fk_medico_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS recepcionista (
    id_usuario UUID PRIMARY KEY,
    CONSTRAINT fk_recepcionista_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS administrador (
    id_usuario UUID PRIMARY KEY,
    CONSTRAINT fk_administrador_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id)
        ON DELETE CASCADE
);
