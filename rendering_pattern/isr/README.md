# ISR - Incremental Static Regeneration

## How to run

```bash
npm install
npm start
```

Then open http://localhost:3002

## What to notice

- The timestamp stays the same when you refresh quickly (it's cached/static)
- Wait 10 seconds, then refresh — the page is still the old one (stale-while-revalidate)
- Refresh again — NOW you see the new timestamp (background rebuild happened)
- Check the server logs to see when rebuilds happen
