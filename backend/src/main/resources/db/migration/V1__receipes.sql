CREATE TABLE recipe
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    description TEXT,
    image_url   TEXT,
    link        TEXT,
    prep_time   TEXT,
    cook_time   TEXT,
    total_time  TEXT
);

CREATE TABLE ingredient
(
    id         SERIAL PRIMARY KEY,
    name       TEXT UNIQUE NOT NULL,
    vegetarian BOOLEAN,
    vegan      BOOLEAN,
    image_url  TEXT
);

CREATE TABLE nutrition_info
(
    id            SERIAL PRIMARY KEY,
    calories      INTEGER,
    fat           REAL,
    saturated_fat REAL,
    protein       REAL,
    carbohydrates REAL,
    sugar         REAL,
    salt          REAL
);

CREATE TABLE recipe_nutrition_info
(
    recipe_id         INTEGER REFERENCES recipe (id)         NOT NULL,
    nutrition_info_id INTEGER REFERENCES nutrition_info (id) NOT NULL,
    PRIMARY KEY (recipe_id, nutrition_info_id)
);

CREATE TABLE ingredient_nutrition_info
(
    ingredient_id     INTEGER REFERENCES ingredient (id)     NOT NULL,
    nutrition_info_id INTEGER REFERENCES nutrition_info (id) NOT NULL,
    PRIMARY KEY (ingredient_id, nutrition_info_id)
);

CREATE TABLE recipe_ingredient
(
    id            SERIAL PRIMARY KEY,
    recipe_id     INTEGER REFERENCES recipe (id)     NOT NULL,
    ingredient_id INTEGER REFERENCES ingredient (id) NOT NULL,
    unit          TEXT                               NOT NULL,
    quantity      TEXT                               NOT NULL
);

CREATE TABLE recipe_step
(
    id          SERIAL PRIMARY KEY,
    recipe_id   INTEGER REFERENCES recipe (id) NOT NULL,
    step_number INTEGER                        NOT NULL,
    description TEXT                           NOT NULL,
    image_url   TEXT,
    UNIQUE (recipe_id, step_number)
);
