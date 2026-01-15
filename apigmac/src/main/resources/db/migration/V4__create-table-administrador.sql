CREATE TABLE administrador(
    id_usuario UUID PRIMARY KEY,
    CONSTRAINT fk_administrador_usuario
            FOREIGN KEY (id_usuario)
            REFERENCES usuario(id)
            ON DELETE CASCADE
);