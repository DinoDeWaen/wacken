-- The database image has the bootstrap auth.uid() helper. Supabase Auth normally
-- upgrades it; mirror the deployed helper so PostgREST JSON JWT claims are tested.
CREATE OR REPLACE FUNCTION auth.uid() RETURNS uuid LANGUAGE sql STABLE AS $$
 SELECT coalesce(nullif(current_setting('request.jwt.claim.sub',true),''),
   nullif(current_setting('request.jwt.claims',true),'')::jsonb->>'sub')::uuid
$$;
