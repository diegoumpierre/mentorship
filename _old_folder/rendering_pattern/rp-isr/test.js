const http = require('http');
const assert = require('assert');
const { spawn } = require('child_process');

const server = spawn('node', ['server.js'], { stdio: ['ignore', 'pipe', 'inherit'] });

function get() {
  return new Promise((resolve, reject) => {
    http.get('http://localhost:3002/', (res) => {
      let body = '';
      res.on('data', (c) => (body += c));
      res.on('end', () => resolve(body));
    }).on('error', reject);
  });
}

function pickTimestamp(html) {
  const m = html.match(/Page built at:<\/strong>\s*([^<]+)/);
  return m && m[1].trim();
}

server.stdout.once('data', async () => {
  try {
    const a = await get();
    const b = await get();
    assert.strictEqual(pickTimestamp(a), pickTimestamp(b), 'cached page should not change inside revalidation window');
    console.log('ok');
    server.kill();
    process.exit(0);
  } catch (e) {
    server.kill();
    throw e;
  }
});
