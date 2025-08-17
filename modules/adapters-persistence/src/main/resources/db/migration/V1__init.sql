create table cliente (
  id uuid primary key,
  nome varchar(200) not null,
  cnpj varchar(20),
  slug varchar(100) not null unique,
  logo_url varchar(500),
  config_json jsonb,
  criado_em timestamptz default now()
);

create table usuario (
  id uuid primary key,
  cliente_id uuid not null references cliente(id),
  nome varchar(200) not null,
  email varchar(200) not null,
  hash_senha varchar(255) not null,
  status varchar(20) not null,
  mfa boolean default false,
  criado_em timestamptz default now(),
  unique (cliente_id, email)
);

create table papel (
  id serial primary key,
  nome varchar(50) not null unique
);

create table usuario_papel (
  usuario_id uuid references usuario(id),
  papel_id int references papel(id),
  primary key (usuario_id, papel_id)
);

create table aluno (
  id uuid primary key,
  cliente_id uuid not null references cliente(id),
  nome varchar(200) not null,
  cpf varchar(20),
  email varchar(200),
  ra varchar(50),
  telefone varchar(30),
  criado_em timestamptz default now(),
  unique (cliente_id, email),
  unique (cliente_id, cpf)
);

create table evento (
  id uuid primary key,
  cliente_id uuid not null references cliente(id),
  titulo varchar(200) not null,
  descricao text,
  local varchar(200),
  capacidade_total int,
  carga_horaria int,
  regra_presenca_min_pct int default 75,
  inicio timestamptz,
  fim timestamptz,
  status varchar(30),
  criado_em timestamptz default now()
);

create table dia_evento (
  id uuid primary key,
  evento_id uuid not null references evento(id) on delete cascade,
  data date not null,
  hora_inicio time not null,
  hora_fim time not null,
  sala varchar(100),
  capacidade int
);

create table inscricao (
  id uuid primary key,
  aluno_id uuid not null references aluno(id),
  evento_id uuid not null references evento(id),
  status varchar(20) not null,
  qr_seed varchar(64) not null,
  criado_em timestamptz default now(),
  unique (aluno_id, evento_id)
);

create table presenca (
  id uuid primary key,
  inscricao_id uuid not null references inscricao(id),
  dia_evento_id uuid not null references dia_evento(id),
  checkin_em timestamptz,
  checkout_em timestamptz,
  origem varchar(20) not null,
  unique (inscricao_id, dia_evento_id)
);

create table certificado (
  id uuid primary key,
  inscricao_id uuid not null references inscricao(id),
  emitido_em timestamptz,
  url_pdf varchar(500),
  verificador_codigo varchar(100) not null unique,
  status varchar(20) not null
);

create table audit_log (
  id uuid primary key,
  cliente_id uuid not null references cliente(id),
  usuario_id uuid,
  entidade varchar(100) not null,
  entidade_id varchar(100) not null,
  acao varchar(50) not null,
  diff_json jsonb,
  criado_em timestamptz default now()
);

-- Índices úteis
create index idx_usuario_cliente on usuario(cliente_id);
create index idx_aluno_cliente on aluno(cliente_id);
create index idx_evento_cliente on evento(cliente_id);
create index idx_dia_evento_evento on dia_evento(evento_id);
create index idx_inscricao_evento on inscricao(evento_id);
create index idx_presenca_dia on presenca(dia_evento_id);