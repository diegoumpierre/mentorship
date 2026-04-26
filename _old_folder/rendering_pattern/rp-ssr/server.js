const express = require('express');
const app = express();

app.get('/', (req, res) => {
  const now = new Date().toLocaleString();

  // The HTML is fully built on the server before being sent to the browser.
  // Every request generates a fresh page with the current timestamp.
  const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SSR - Server-Side Rendering</title>
  <style>
    body { font-family: system-ui, sans-serif; max-width: 720px; margin: 40px auto; padding: 0 20px; background: #fafafa; color: #333; }
    h1 { color: #6a1b9a; }
    .timestamp { background: #f3e5f5; padding: 12px; border-radius: 6px; margin: 20px 0; }
    .explanation { background: #fff; padding: 20px; border-radius: 8px; border: 1px solid #eee; line-height: 1.7; }
    .badge { display: inline-block; background: #6a1b9a; color: white; padding: 4px 12px; border-radius: 4px; font-size: 14px; }
  </style>
</head>
<body>
  <span class="badge">SSR</span>
  <h1>Welcome!</h1>

  <div class="timestamp">
    <strong>Page rendered at:</strong> ${now}<br>
    <em>This timestamp was generated on the server, right when you requested the page.</em>
  </div>

  <div class="explanation">
    <h2>How Server-Side Rendering works</h2>
    <p>
      When you opened this page, your browser sent a request to the server. The server ran this
      code, built the complete HTML (including the timestamp above), and sent it back ready to display.
    </p>
    <p>
      Refresh the page and you'll see a new timestamp every time — because the server builds
      a fresh page on each request. There's no "Loading..." state; the content arrives fully formed.
    </p>
    <p>
      <strong>The good:</strong> Search engines see the full content immediately. Users see content
      faster because there's no waiting for JavaScript to build the page.
    </p>
    <p>
      <strong>The bad:</strong> Every request hits the server, which means more server load.
      Navigation between pages requires full page reloads (unless you add client-side hydration).
    </p>
  </div>
</body>
</html>`;

  res.send(html);
});

app.listen(3001, () => {
  console.log('SSR server running at http://localhost:3001');
});
