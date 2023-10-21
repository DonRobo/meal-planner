CREATE TABLE recipe
(
    id            SERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    description TEXT,
    image_url   TEXT,
    link        TEXT,
    prep_time   INT,
    cook_time   INT,
    total_time  INT,
    calories      INTEGER,
    fat           REAL,
    saturated_fat REAL,
    protein       REAL,
    carbs         REAL,
    sugar         REAL,
    salt          REAL,
    vegan         BOOLEAN,
    vegetarian    BOOLEAN,
    steps       jsonb
);

CREATE TABLE ingredient
(
    id            SERIAL PRIMARY KEY,
    name          TEXT UNIQUE NOT NULL,
    image_url     TEXT,
    calories      INTEGER,
    fat           REAL,
    saturated_fat REAL,
    protein       REAL,
    carbs         REAL,
    sugar         REAL,
    salt          REAL,
    vegan         BOOLEAN,
    vegetarian    BOOLEAN
);

CREATE TABLE recipe_ingredient
(
    id            SERIAL PRIMARY KEY,
    recipe_id     INTEGER REFERENCES recipe (id)     NOT NULL,
    ingredient_id INTEGER REFERENCES ingredient (id) NOT NULL,
    quantity      REAL                               NOT NULL,
    unit          TEXT                               NOT NULL
);
