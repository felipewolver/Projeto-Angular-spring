CREATE TABLE pessoa (

	codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL,
	ativo BOOLEAN NOT NULL, 
	logradouro VARCHAR(100),
	numero VARCHAR(30),
	complemento VARCHAR(30),
	bairro VARCHAR(30),
	cep VARCHAR(30),
	cidade VARCHAR(30),
	estado VARCHAR(30)
	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	
	INSERT INTO pessoa (nome, ativo, logradouro, numero, complemento, bairro, cep, cidade, estado) values ('Joao', true, 'rua 01', '01', null, 'bairro01', '24535-553', 'cidade01', 'RJ');
	INSERT INTO pessoa (nome, ativo, logradouro, numero, complemento, bairro, cep, cidade, estado) values ('Pedro', true, 'rua 02', '02', null, 'bairro02', '24535-553', 'cidade02', 'RJ');
	INSERT INTO pessoa (nome, ativo, logradouro, numero, complemento, bairro, cep, cidade, estado) values ('Maria', true, 'rua 03', '03', null, 'bairro03', '24535-553', 'cidade03', 'SP');
	INSERT INTO pessoa (nome, ativo, logradouro, numero, complemento, bairro, cep, cidade, estado) values ('Tiago', true, 'rua 04', '04', null, 'bairro04', '24535-553', 'cidade04', 'ES');
	INSERT INTO pessoa (nome, ativo, logradouro, numero, complemento, bairro, cep, cidade, estado) values ('Rafael', true, 'rua 05', '05', null, 'bairro05', '24535-553', 'cidade05', 'MG');
	