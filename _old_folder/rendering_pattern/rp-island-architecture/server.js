const express = require('express');
const app = express();

app.get('/', (req, res) => {
  const now = new Date().toLocaleString();

  // The page is mostly static HTML rendered on the server.
  // Only the "islands" (interactive bits) get JavaScript.
  // Everything else is plain HTML — zero JS overhead.

  const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Island Architecture</title>
  <style>
    body { font-family: system-ui, sans-serif; max-width: 720px; margin: 40px auto; padding: 0 20px; background: #fafafa; color: #333; }
    h1 { color: #e65100; }
    .timestamp { background: #fff3e0; padding: 12px; border-radius: 6px; margin: 20px 0; }
    .explanation { background: #fff; padding: 20px; border-radius: 8px; border: 1px solid #eee; line-height: 1.7; }
    .badge { display: inline-block; background: #e65100; color: white; padding: 4px 12px; border-radius: 4px; font-size: 14px; }
    .island { border: 2px dashed #e65100; padding: 16px; border-radius: 8px; margin: 20px 0; position: relative; }
    .island-label { position: absolute; top: -10px; left: 12px; background: #e65100; color: white; padding: 2px 8px; border-radius: 3px; font-size: 12px; }
    .static-zone { border: 2px solid #ccc; padding: 16px; border-radius: 8px; margin: 20px 0; position: relative; }
    .static-label { position: absolute; top: -10px; left: 12px; background: #999; color: white; padding: 2px 8px; border-radius: 3px; font-size: 12px; }
    button { background: #e65100; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 14px; }
    button:hover { background: #bf360c; }
    #counter { font-size: 24px; margin: 0 12px; }
  </style>
</head>
<body>
  <span class="badge">Islands</span>
  <h1>Welcome!</h1>

  <div class="static-zone">
    <span class="static-label">Static HTML (no JS)</span>
    <div class="timestamp">
      <strong>Page rendered at:</strong> ${now}<br>
      <em>This part is pure server-rendered HTML. No JavaScript involved.</em>
    </div>
  </div>

  <div class="island">
    <span class="island-label">Interactive Island</span>
    <p>This is an "island" — a small interactive component surrounded by static HTML.</p>
    <button onclick="decrement()">-</button>
    <span id="counter">0</span>
    <button onclick="increment()">+</button>
    <p><small>Only this small piece of the page needs JavaScript.</small></p>
  </div>

  <div class="island">
    <span class="island-label">Interactive Island</span>
    <p>Another island — a live clock that updates every second:</p>
    <div id="clock" style="font-size: 20px; font-family: monospace;"></div>
    <p><small>Each island hydrates independently. The rest of the page stays static.</small></p>
  </div>

  <div class="static-zone">
    <span class="static-label">Static HTML (no JS)</span>
    <div class="explanation">
      <h2>How Island Architecture works</h2>
      <p>
        Look at this page: most of it is static HTML that came from the server — the welcome
        message, this explanation text, the timestamp. None of that needs JavaScript.
      </p>
      <p>
        The only parts that got JavaScript are the two dashed-border "islands" above: the counter
        and the live clock. These small interactive components are independently hydrated with
        just the JS they need.
      </p>
      <p>
        In a traditional SPA, the entire page would be controlled by a JavaScript framework.
        With islands, you ship JS only for the parts that actually need interactivity.
      </p>
      <p>
        <strong>The good:</strong> Way less JavaScript sent to the browser. Static parts load
        instantly. Each island is independent — one breaking doesn't affect the others.
        Frameworks like Astro use this pattern.
      </p>
      <p>
        <strong>The bad:</strong> Sharing state between islands is harder. You need to think about
        which parts are static and which are interactive. More architectural decisions upfront.
      </p>
    </div>
  </div>

  <!-- Only the islands get JavaScript -->
  <script>
    // Island 1: Counter
    let count = 0;
    function increment() { count++; document.getElementById('counter').textContent = count; }
    function decrement() { count--; document.getElementById('counter').textContent = count; }

    // Island 2: Live clock
    function updateClock() {
      document.getElementById('clock').textContent = new Date().toLocaleTimeString();
    }
    updateClock();
    setInterval(updateClock, 1000);
  </script>
</body>
</html>`;

  res.send(html);
});

app.listen(3003, () => {
  console.log('Island Architecture server running at http://localhost:3003');
});
