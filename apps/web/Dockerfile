# ==============================================================================
# Stage 1 — DEPS: Instalação de dependências (monorepo-aware)
# ==============================================================================
# Contexto é a RAIZ do monorepo para ter acesso ao pnpm-lock.yaml e
# pnpm-workspace.yaml. Usa `pnpm deploy` para produzir um diretório isolado
# apenas com as deps do workspace "web".
# ==============================================================================
FROM node:22-alpine AS deps

# Ativa o pnpm via corepack
RUN corepack enable && corepack prepare pnpm@10.34.4 --activate

WORKDIR /monorepo

# Copia arquivos de configuração do monorepo (lock + workspace + npmrc)
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml .npmrc ./

# Copia o package.json do workspace web
COPY apps/web/package.json ./apps/web/package.json

# Instala TODAS as dependências do workspace web (incluindo devDependencies para build)
RUN --mount=type=cache,target=/root/.local/share/pnpm/store \
    pnpm install --frozen-lockfile --prefer-offline --filter web...

# ==============================================================================
# Stage 2 — BUILD: Compilação TypeScript + Vite
# ==============================================================================
# Copia o código-fonte do web sobre as deps já instaladas.
# Gera o bundle otimizado em /monorepo/apps/web/dist.
# ==============================================================================
FROM deps AS build

# Copia o código-fonte do workspace web
COPY apps/web/ ./apps/web/

# Build arg para injetar a URL da API em build time
ARG VITE_API_URL=http://localhost:8080/api/v1
ENV VITE_API_URL=${VITE_API_URL}

# TypeScript check + Vite build (produção)
RUN pnpm --filter web run build

# ==============================================================================
# Stage 3 — RUNTIME: Nginx Alpine (~7MB)
# ==============================================================================
# Serve os arquivos estáticos gerados pelo Vite.
# Configuração customizada do Nginx com:
#   - Compressão gzip
#   - Cache headers para assets com hash
#   - SPA fallback (try_files → index.html)
#   - Security headers
# ==============================================================================
FROM nginx:1.27-alpine AS runtime

# Labels de metadados OCI
LABEL org.opencontainers.image.title="PetLife Web" \
      org.opencontainers.image.description="Frontend React 19 + Vite — PetLife" \
      org.opencontainers.image.source="https://github.com/andreyvdecarvalho/PetLife"

# Remove a config padrão do Nginx
RUN rm /etc/nginx/conf.d/default.conf

# Configuração otimizada do Nginx
COPY apps/web/nginx.conf /etc/nginx/conf.d/default.conf

# Copia os arquivos estáticos do build
COPY --from=build /monorepo/apps/web/dist /usr/share/nginx/html

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget -qO- http://localhost:80/ || exit 1

EXPOSE 80

# Nginx roda em foreground
CMD ["nginx", "-g", "daemon off;"]
