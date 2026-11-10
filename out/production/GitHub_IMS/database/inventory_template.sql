-- SQLite schema used by inventory_sample.db and DatabaseManager.java.
CREATE TABLE products (
    product_id TEXT PRIMARY KEY NOT NULL
        CHECK(length(product_id) = 7 AND product_id NOT GLOB '*[^0-9]*'),
    product_name TEXT NOT NULL COLLATE NOCASE UNIQUE
        CHECK(length(trim(product_name)) > 0),
    category TEXT NOT NULL CHECK(length(trim(category)) > 0),
    supplier TEXT NOT NULL CHECK(length(trim(supplier)) > 0),
    unit_quantity INTEGER NOT NULL CHECK(unit_quantity >= 0),
    unit_price TEXT NOT NULL CHECK(CAST(unit_price AS REAL) >= 0)
);
