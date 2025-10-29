CREATE TABLE recepcionista(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    CONSTRAINT fk_usuario
            FOREIGN KEY (id_usuario)
            REFERENCES usuario(id)
);