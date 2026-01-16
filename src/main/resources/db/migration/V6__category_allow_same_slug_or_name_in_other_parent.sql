ALTER TABLE category DROP CONSTRAINT category_slug_key;
ALTER TABLE category DROP CONSTRAINT category_name_key;
ALTER TABLE category ADD UNIQUE(parent_id, slug);
ALTER TABLE category ADD UNIQUE(parent_id, name);