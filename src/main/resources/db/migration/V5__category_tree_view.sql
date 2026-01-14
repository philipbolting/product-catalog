CREATE RECURSIVE VIEW category_tree
    (id, parent_id, name, slug, sort_order, depth) AS
SELECT
    id, parent_id, name,
    CAST(slug AS VARCHAR) slug,
    TO_CHAR(sort_order,'fm000') sort_order,
    0::int depth
FROM category c
WHERE parent_id is null
UNION ALL
SELECT
    child.id, child.parent_id, child.name,
    parent.slug || ('/' || child.slug) slug,
    parent.sort_order || TO_CHAR(child.sort_order,'fm000') sort_order,
    parent.depth + 1 depth
FROM category child
    INNER JOIN category_tree parent
    ON parent.id = child.parent_id
;
