CREATE TYPE user_timezone AS ENUM (
    'AMERICA_SAO_PAULO',
    'AMERICA_MANAUS',
    'AMERICA_BELEM',
    'AMERICA_FORTALEZA',
    'AMERICA_RECIFE',
    'AMERICA_CUIABA',
    'AMERICA_CAMPO_GRANDE',
    'AMERICA_RIO_BRANCO'
);

ALTER TABLE users 
ADD COLUMN nickname VARCHAR(100),
ADD COLUMN phone VARCHAR(20);

-- Migrate existing timezone from VARCHAR to ENUM
ALTER TABLE users ALTER COLUMN timezone DROP DEFAULT;
ALTER TABLE users 
ALTER COLUMN timezone TYPE user_timezone 
USING CASE 
    WHEN timezone = 'America/Sao_Paulo' THEN 'AMERICA_SAO_PAULO'::user_timezone
    WHEN timezone = 'America/Manaus' THEN 'AMERICA_MANAUS'::user_timezone
    WHEN timezone = 'America/Belem' THEN 'AMERICA_BELEM'::user_timezone
    WHEN timezone = 'America/Fortaleza' THEN 'AMERICA_FORTALEZA'::user_timezone
    WHEN timezone = 'America/Recife' THEN 'AMERICA_RECIFE'::user_timezone
    WHEN timezone = 'America/Cuiaba' THEN 'AMERICA_CUIABA'::user_timezone
    WHEN timezone = 'America/Campo_Grande' THEN 'AMERICA_CAMPO_GRANDE'::user_timezone
    WHEN timezone = 'America/Rio_Branco' THEN 'AMERICA_RIO_BRANCO'::user_timezone
    ELSE 'AMERICA_SAO_PAULO'::user_timezone
END;

ALTER TABLE users 
ALTER COLUMN timezone SET DEFAULT 'AMERICA_SAO_PAULO'::user_timezone;
