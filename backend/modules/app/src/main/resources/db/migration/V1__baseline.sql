-- Baseline schema for UrbanGate (v1)
-- This creates a shared Outbox table used by the Outbox pattern.

create table if not exists ug_outbox (
  id uuid primary key,
  aggregate_type varchar(100) not null,
  aggregate_id varchar(100) not null,
  event_type varchar(200) not null,
  payload jsonb not null,
  occurred_at timestamptz not null,
  published_at timestamptz null
);

create index if not exists idx_ug_outbox_unpublished
  on ug_outbox (published_at)
  where published_at is null;
