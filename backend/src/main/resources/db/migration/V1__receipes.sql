CREATE TABLE nutrition_data
(
    id            SERIAL PRIMARY KEY,
    calories      INTEGER,
    fat           REAL,
    saturated_fat REAL,
    protein       REAL,
    carbs         REAL,
    sugar         REAL,
    salt          REAL,
    UNIQUE (calories, fat, saturated_fat, protein, carbs, sugar, salt)
);

CREATE TABLE recipe
(
    id                SERIAL PRIMARY KEY,
    name              TEXT                                   NOT NULL,
    description       TEXT,
    image_url         TEXT,
    link              TEXT,
    prep_time         INT,
    cook_time         INT,
    total_time        INT,
    nutrition_data_id INTEGER REFERENCES nutrition_data (id) NOT NULL
);

CREATE TABLE ingredient
(
    id                SERIAL PRIMARY KEY,
    name              TEXT UNIQUE                            NOT NULL,
    vegetarian        BOOLEAN,
    vegan             BOOLEAN,
    image_url         TEXT,
    nutrition_data_id INTEGER REFERENCES nutrition_data (id) NOT NULL
);

CREATE TABLE recipe_ingredient
(
    id            SERIAL PRIMARY KEY,
    recipe_id     INTEGER REFERENCES recipe (id)     NOT NULL,
    ingredient_id INTEGER REFERENCES ingredient (id) NOT NULL,
    quantity      REAL                               NOT NULL,
    unit          TEXT                               NOT NULL
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
