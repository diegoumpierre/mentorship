const express = require('express');
const app = express();

// ISR = Static page that automatically regenerates in the background after a time window.
// This simulates what Next.js does with revalidate.

const REVALIDATE_SECONDS = 10;

let cachedHtml = null;
let lastBuilt = null;

function buildPage() {
  const now = new Date().toLocaleString();
  lastBuilt = Date.now();

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>ISR - Incremental Static Regeneration</title>
  <style>
    body { font-family: system-ui, sans-serif; max-width: 720px; margin: 40px auto; padding: 0 20px; background: #fafafa; color: #333; }
    h1 { color: #2e7d32; }
    .timestamp { background: #e8f5e9; padding: 12px; border-radius: 6px; margin: 20px 0; }
    .explanation { background: #fff; padding: 20px; border-radius: 8px; border: 1px solid #eee; line-height: 1.7; }
    .badge { display: inline-block; background: #2e7d32; color: white; padding: 4px 12px; border-radius: 4px; font-size: 14px; }
    .config { background: #f1f8e9; padding: 10px; border-radius: 6px; font-family: monospace; }
  </style>
</head>
<body>
  <span class="badge">ISR</span>
  <h1>Welcome!</h1>

  <div class="timestamp">
    <strong>Page built at:</strong> ${now}<br>
    <em>This timestamp updates only after the revalidation window expires (${REVALIDATE_SECONDS}s).</em>
  </div>

  <div class="config">
    Revalidation window: ${REVALIDATE_SECONDS} seconds
  </div>

  <div class="explanation">
    <h2>How Incremental Static Regeneration works</h2>
    <p>
      ISR is like SSG with a twist: the page is static, but it has an expiration time.
      When a user visits after the page has expired, they still get the old (cached) page immediately,
      but the server starts rebuilding a new version in the background.
    </p>
    <p>
      The next visitor after the rebuild gets the fresh page. So you get the speed of static
      files with content that stays reasonably up to date — without rebuilding the entire site.
    </p>
    <p>
      Try this: refresh quickly a few times — the timestamp stays the same. Wait ${REVALIDATE_SECONDS}
      seconds, then refresh. The first refresh after the window triggers a rebuild, and the
      <em>following</em> refresh shows the new timestamp.
    </p>
    <p>
      <strong>The good:</strong> Best of both worlds. Fast like SSG, but content eventually updates.
      No full rebuilds needed. Perfect for content that changes, but not every second.
    </p>
    <p>
      <strong>The bad:</strong> The first user after expiration still sees stale content. There's a
      window where the page is outdated. If you need real-time data, this isn't it.
    </p>
  </div>
</body>
</html>`;
}

app.get('/', (req, res) => {
  const now = Date.now();
  const isExpired = !lastBuilt || (now - lastBuilt) > REVALIDATE_SECONDS * 1000;

  if (!cachedHtml) {
    // First request ever — build the page
    cachedHtml = buildPage();
    console.log('Initial build done');
  } else if (isExpired) {
    // Page expired — serve stale, rebuild in background
    console.log('Page expired, serving stale and rebuilding in background...');
    setImmediate(() => {
      cachedHtml = buildPage();
      console.log('Background rebuild done');
    });
  }

  res.send(cachedHtml);
});

app.listen(3002, () => {
  console.log('ISR server running at http://localhost:3002');
  console.log(`Revalidation window: ${REVALIDATE_SECONDS} seconds`);
});
