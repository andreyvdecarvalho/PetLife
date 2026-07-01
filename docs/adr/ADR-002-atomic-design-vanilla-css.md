# ADR 002: Metodologia Atomic Design e Vanilla CSS no Frontend Web

## Status
Aprovado

## Data
30 de Junho de 2026

## Contexto
O design do PetLife foi totalmente projetado através do Stitch (consistindo em 35 telas detalhadas). Para garantir alta fidelidade de design, consistência visual, flexibilidade e performance ideal da aplicação web React 19 + Vite (focada em PWA), é necessário escolher uma arquitetura CSS e um padrão de componentização bem definido.

O uso de frameworks utilitários como TailwindCSS pode gerar acúmulo de classes nos componentes e dificultar a fidelidade a tokens de design personalizados complexos sem sobrecarregar a legibilidade do código HTML/JSX. Ferramentas CSS-in-JS (como styled-components) adicionam overhead de runtime e impedem otimizações de compilação CSS estática.

## Decisão
Decidimos utilizar a metodologia de **Atomic Design** em conjunto com **Vanilla CSS** nativo para a estilização do frontend.

Regras de implementação adotadas:
1. Os componentes devem ser organizados rigidamente nas pastas correspondentes ao nível atômico:
   - `atoms/`: Componentes básicos indivisíveis (ex: botões, inputs, chips). Eles devem herdar o tamanho ou ocupar `width: 100%` e não devem possuir margens externas fixas.
   - `molecules/`: Combinação de átomos (ex: barra de busca, campos com labels e validação).
   - `organisms/`: Componentes complexos que formam seções da página (ex: header, barra de navegação inferior).
   - `templates/`: Estrutura de grid/layout que define o posicionamento dos organismos.
   - `pages/`: Telas completas que renderizam os templates com dados dinâmicos.
2. Cada componente React (`index.tsx`) deve ter o seu próprio arquivo de estilos (`styles.css`) importado diretamente no topo do componente.
3. Não são permitidos frameworks utilitários (TailwindCSS), CSS Modules ou bibliotecas de CSS-in-JS. Apenas seletores CSS padrão com o aninhamento nativo suportado pelos navegadores modernos.
4. Para garantir unicidade de classes e evitar vazamentos de escopo global, cada componente deve usar classes exclusivas prefixadas com seu nível de design (ex: `.atom-button`, `.molecule-searchbar`).
5. É proibido o uso de valores de estilo (cores, fontes, espaçamentos) hardcoded no CSS do componente. Todos os valores devem ser consumidos de forma estrita a partir de variáveis CSS custom properties definidas globalmente em `src/theme.css`.

## Consequências
### Positivas:
- Fidelidade visual absoluta de 100% com o design system do Stitch.
- Sem overhead de runtime, com CSS estático otimizado gerado pelo Vite.
- Componentes altamente reutilizáveis, modulares e independentes de frameworks externos de estilo.
- Limpeza e legibilidade excelente do código JSX/TSX.

### Negativas / Custos:
- Maior escrita de código de estilos Vanilla CSS comparado a classes utilitárias rápidas.
- Necessidade de rigor de nomenclatura de classes para evitar conflitos de escopo global.
