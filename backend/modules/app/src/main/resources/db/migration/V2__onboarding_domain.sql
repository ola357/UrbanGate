-- Onboarding domain schema (v2)

create table if not exists ug_estates (
  id uuid primary key,
  name varchar(200) not null,
  code varchar(50) not null,
  status varchar(30) not null,
  created_at timestamptz not null default now()
);

create unique index if not exists uk_ug_estates_code on ug_estates (code);

create table if not exists ug_residents (
  id uuid primary key,
  estate_id uuid not null references ug_estates(id),
  first_name varchar(100) not null,
  last_name varchar(100) not null,
  phone varchar(30),
  email varchar(200),
  apartment varchar(100),
  status varchar(30) not null,
  keycloak_user_id varchar(100),
  activated_at timestamptz null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint uk_ug_residents_estate_phone unique (estate_id, phone),
  constraint uk_ug_residents_estate_email unique (estate_id, email)
);

create index if not exists idx_ug_residents_estate_id
  on ug_residents (estate_id);

create table if not exists ug_activation_codes (
  id uuid primary key,
  estate_id uuid not null references ug_estates(id),
  resident_id uuid not null references ug_residents(id),
  code varchar(50) not null,
  expires_at timestamptz not null,
  used_at timestamptz null,
  created_at timestamptz not null default now()
);

create unique index if not exists uk_ug_activation_codes_code on ug_activation_codes (code);
create index if not exists idx_ug_activation_codes_resident on ug_activation_codes (resident_id);

create table if not exists ug_login_attempts (
  id uuid primary key,
  resident_id uuid not null references ug_residents(id),
  attempted_at timestamptz not null default now(),
  success boolean not null,
  failure_reason varchar(200),
  ip_address varchar(64),
  user_agent varchar(255)
);

create index if not exists idx_ug_login_attempts_resident on ug_login_attempts (resident_id);
create index if not exists idx_ug_login_attempts_attempted_at on ug_login_attempts (attempted_at);

create table if not exists ug_password_resets (
  id uuid primary key,
  resident_id uuid not null references ug_residents(id),
  code varchar(64) not null,
  expires_at timestamptz not null,
  consumed_at timestamptz null,
  attempts int not null default 0,
  created_at timestamptz not null default now()
);

create index if not exists idx_ug_password_resets_resident on ug_password_resets (resident_id);
create index if not exists idx_ug_password_resets_expires_at on ug_password_resets (expires_at);
