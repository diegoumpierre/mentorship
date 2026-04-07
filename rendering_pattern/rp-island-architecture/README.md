# Island Architecture

## How to run

```bash
npm install
npm start
```

Then open http://localhost:3003

## What to notice

- Most of the page is static HTML with zero JavaScript
- Only the interactive "islands" (counter + clock) use JavaScript
- View the source — the static content is all there, fully rendered by the server
- The dashed borders show you exactly which parts are islands (interactive) vs static
- If you disable JavaScript, the static parts still render perfectly
