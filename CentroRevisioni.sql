CREATE DATABASE CentroRevisioni;
USE CentroRevisioni;

CREATE TABLE revisioni(
    targa VARCHAR(16) NOT NULL,
    anno_immatricolazione INT NOT NULL,
    data_revisione DATE NOT NULL,
    mese_scadenza INT NOT NULL,
    anno_scadenza INT NOT NULL,
    esito CHAR(1) NOT NULL,
    PRIMARY KEY (targa, data_revisione)
);
