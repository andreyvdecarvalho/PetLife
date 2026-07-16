-- V16: Altera colunas de foto para TEXT para suportar Base64 Data URLs em desenvolvimento.
-- Em produção, essas colunas devem armazenar URLs de S3/MinIO (que são curtas),
-- mas o campo TEXT garante compatibilidade futura e de dev.
ALTER TABLE pets
    ALTER COLUMN photo_url TYPE TEXT;
