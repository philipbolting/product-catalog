TRUNCATE TABLE category CASCADE;
INSERT INTO category (id, parent_id, sort_order, slug, name) VALUES
(1, NULL, 1, 'category-1', 'Category 1'),
(2, NULL, 2, 'category-2', 'Category 2'),
(3, 1, 1, 'category-1-1', 'Category 1.1'),
(4, 1, 2, 'category-1-2', 'Category 1.2'),
(5, 2, 1, 'category-2-1', 'Category 2.1'),
(6, 5, 1, 'category-2-1-1', 'Category 2.1.1'),
(7, 5, 2, 'category-2-1-2', 'Category 2.1.2');
