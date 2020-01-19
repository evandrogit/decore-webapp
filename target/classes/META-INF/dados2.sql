INSERT INTO usuarios (id, nome, funcao, contato, foto, login, senha) VALUES (1,'T�cito','CEO','92 9-9999-9999',null,'tacito', '$2a$10$0yEvoqhIbxgkzMo7t9l35.nwGS53pM2zk1TLYD2MQkU7acTHx3K/.');
INSERT INTO grupos (id, nome, descricao) VALUES (1, 'ADMINISTRADOR', 'Administrador'), (2, 'USUARIO_AVANCADO','Usu�rio Avan�ado'), (3, 'USUARIO_COMUM','Usu�rio Comum'); 
INSERT INTO usuario_grupo (usuario_id, grupo_id) VALUES (1, 1);

INSERT INTO categoria_produtos (id, nome) VALUES (1, 'Porta'), (2, 'Janela'), (3, 'Fechadura');
INSERT INTO fornecedores (id, nome) VALUES (1, 'Acer'), (2, 'Samsung');

INSERT INTO tipos_vendas (id, descricao) VALUES (1, 'Olx'), (2, 'Face');

INSERT INTO tipos_lancamentos (id, descricao) VALUES (1, 'Despesa Vari�vel'), (2, 'Avaria'), (3, 'Receita');
INSERT INTO destino_lancamentos (id, descricao) VALUES (1, 'Empresa'), (2, 'T�cito');
INSERT INTO categoria_lancamentos (id, nome) VALUES (1, 'Prolabore'), (2, 'Servi�os'), (3, 'Outros');