CREATE TABLE planned_meal
(
    id       SERIAL PRIMARY KEY,
    day      date  NOT NULL,
    ordering int   NOT NULL,
    data     jsonb NOT NULL
);
