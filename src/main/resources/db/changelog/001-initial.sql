CREATE TABLE url
(
    id        uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    original  TEXT NOT NULL,
    shortened TEXT NOT NULL,
    CONSTRAINT unique_url UNIQUE (original, shortened)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_url ON url (shortened);
