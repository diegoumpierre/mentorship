# Rendering Patterns

Five independent examples showing different rendering strategies, all with the same page concept so you can compare them side by side.

| Pattern | Port | Key behavior |
|---------|------|-------------|
| **CSR** | file:// | Content built in the browser by JS. View source = empty. |
| **SSR** | :3001 | Fresh HTML on every request. Timestamp always changes. |
| **SSG** | file:// | HTML generated at build time. Timestamp frozen until rebuild. |
| **ISR** | :3002 | Static page that auto-regenerates after 10s. Best of SSG + SSR. |
| **Islands** | :3003 | Mostly static HTML, only interactive parts get JS. |

## Quick start

Cada pasta `rp-*` é uma POC independente com seu próprio README, `package.json` e código. O prefixo `rp-` (rendering pattern) mantém todas agrupadas.

```bash
# CSR — just open the file
open rp-csr/index.html

# SSG — build then open
cd rp-ssg && npm run build && open dist/index.html && cd ..

# SSR
cd rp-ssr && npm install && npm start    # http://localhost:3001

# ISR
cd rp-isr && npm install && npm start    # http://localhost:3002

# Island Architecture
cd rp-island-architecture && npm install && npm start   # http://localhost:3003
```

## What to pay attention to

1. **View Source** — CSR shows almost nothing, the others show full content
2. **Timestamps** — SSR changes every refresh, SSG never changes, ISR changes after the revalidation window
3. **JavaScript dependency** — Disable JS and see which pages still work
4. **Islands** — Notice how only the interactive parts use JS, the rest is static

---

## Como explicar cada um (pt-BR)

### CSR (Client-Side Rendering)
Imagina que voce vai num restaurante e o garcom te entrega um prato vazio com os ingredientes do lado. Voce mesmo monta o prato na mesa. E isso que o CSR faz: o servidor manda um HTML praticamente vazio e o JavaScript do navegador constroi toda a pagina. Por isso, se voce ver o codigo fonte da pagina, nao tem quase nada la. O lado bom e que depois que carregou, navegar entre telas e muito rapido. O lado ruim e que a primeira vez demora e o Google tem dificuldade de indexar o conteudo.

### SSR (Server-Side Rendering)
Agora imagina que o garcom traz o prato pronto da cozinha. Voce so come. No SSR o servidor monta o HTML completo a cada request e manda pronto pro navegador. Cada vez que voce atualiza a pagina, o servidor cozinha de novo do zero. E otimo pra SEO porque o conteudo ja ta todo no HTML, mas o servidor trabalha mais porque tem que montar a pagina toda vez.

### SSG (Static Site Generation)
Aqui o prato foi preparado ontem e guardado na geladeira. Quando voce pede, o garcom so pega e entrega — sem cozinhar nada. O build gera os arquivos HTML uma vez e pronto, o servidor so serve o arquivo estatico. E absurdamente rapido e barato de hospedar (pode jogar numa CDN). O problema e que se o conteudo muda, voce tem que rodar o build de novo.

### ISR (Incremental Static Regeneration)
E como se o prato da geladeira tivesse uma validade. Enquanto ta valido, todo mundo recebe o mesmo prato (rapido). Quando vence, o proximo cliente ainda recebe o prato velho, mas a cozinha ja comeca a preparar um novo em background. O cliente seguinte pega o prato fresco. Voce tem a velocidade do SSG com a atualizacao do SSR, so que com um pequeno delay.

### Island Architecture
Imagina uma pagina de jornal impressa. O texto, as fotos, os titulos — tudo estatico, nao precisa de JavaScript. Mas no meio da pagina tem um widget de votacao e um relogio ao vivo. So esses pedacinhos interativos recebem JavaScript. O resto e HTML puro. Isso e a arquitetura de ilhas: um mar de conteudo estatico com pequenas "ilhas" de interatividade. Manda muito menos JavaScript pro navegador e a pagina carrega muito mais rapido.
