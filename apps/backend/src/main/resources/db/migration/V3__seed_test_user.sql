-- Seed do usuário de teste padrão para desenvolvimento
-- Credenciais: teste@petlife.com / Senha123@
INSERT INTO users (name, email, password_hash, plan, email_verified, lgpd_accepted_at)
VALUES (
    'Tutor de Teste',
    'teste@petlife.com',
    '$2a$12$2.UFIXCpSrXQkHhxDLT5zOSe220sUbjLLoXrxsOBxOvshKLBs8BEW',
    'FREE',
    true,
    NOW()
) ON CONFLICT (email) DO NOTHING;
