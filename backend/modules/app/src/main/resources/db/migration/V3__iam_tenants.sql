-- IAM tenancy tables (v3)

create table if not exists ug_tenants (
  id uuid primary key,
  slug varchar(120) not null,
  name varchar(200) not null,
  created_at timestamptz not null default now()
);

create unique index if not exists uk_ug_tenants_slug on ug_tenants (slug);

create table if not exists ug_user_profiles (
  id uuid primary key,
  subject varchar(200) not null,
  email varchar(200) not null,
  display_name varchar(200),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create unique index if not exists uk_ug_user_profiles_subject on ug_user_profiles (subject);
create unique index if not exists uk_ug_user_profiles_email on ug_user_profiles (email);

create table if not exists ug_tenant_memberships (
  id uuid primary key,
  tenant_id uuid not null references ug_tenants(id),
  user_id uuid not null references ug_user_profiles(id),
  roles varchar(400) not null,
  created_at timestamptz not null default now()
);

create unique index if not exists uk_ug_tenant_memberships_tenant_user
  on ug_tenant_memberships (tenant_id, user_id);
