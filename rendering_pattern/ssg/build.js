const fs = require('fs');

const buildTime = new Date().toLocaleString();

// This script runs ONCE at build time and generates a static HTML file.
// The timestamp is baked in and never changes until you rebuild.

const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SSG - Static Site Generation</title>
  <style>
    body { font-family: system-ui, sans-serif; max-width: 720px; margin: 40px auto; padding: 0 20px; background: #fafafa; color: #333; }
    h1 { color: #1565c0; }
    .timestamp { background: #e3f2fd; padding: 12px; border-radius: 6px; margin: 20px 0; }
    .explanation { background: #fff; padding: 20px; border-radius: 8px; border: 1px solid #eee; line-height: 1.7; }
    .badge { display: inline-block; background: #1565c0; color: white; padding: 4px 12px; border-radius: 4px; font-size: 14px; }
  </style>
</head>
<body>
  <span class="badge">SSG</span>
  <h1>Welcome!</h1>

  <div class="timestamp">
    <strong>Page built at:</strong> ${buildTime}<br>
    <em>This timestamp was set when the site was built. It won't change until you rebuild.</em>
  </div>

  <div class="explanation">
    <h2>How Static Site Generation works</h2>
    <p>
      This page was generated ahead of time by a build script. When you run "npm run build",
      the script creates a complete HTML file with all the content baked in. After that, the
      server just serves this pre-built file — no processing, no rendering, nothing.
    </p>
    <p>
      Refresh all you want — the timestamp stays the same. It was frozen at build time.
      To update it, you need to run the build again.
    </p>
    <p>
      <strong>The good:</strong> Blazing fast. The server just sends a file, like serving an image.
      Great for SEO since all content is in the HTML. You can host this on a CDN for pennies.
    </p>
    <p>
      <strong>The bad:</strong> Content is frozen until the next build. If your data changes
      frequently, you'll be constantly rebuilding. Not great for pages that need real-time info.
    </p>
  </div>
</body>
</html>`;

fs.mkdirSync('dist', { recursive: true });
fs.writeFileSync('dist/index.html', html);
console.log(`Static site built at ${buildTime}`);
console.log('Open dist/index.html in your browser');
