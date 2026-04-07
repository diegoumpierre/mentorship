const http = require('http');
const assert = require('assert');
const { spawn } = require('child_process');

const server = spawn('node', ['server.js'], { stdio: ['ignore', 'pipe', 'inherit'] });

server.stdout.once('data', () => {
  http.get('http://localhost:3003/', (res) => {
    let body = '';
    res.on('data', (c) => (body += c));
    res.on('end', () => {
      try {
        assert.strictEqual(res.statusCode, 200);
        assert.ok(body.includes('id="counter"'));
        assert.ok(body.includes('id="clock"'));
        assert.ok(body.includes('Page rendered at:'));
        console.log('ok');
        server.kill();
        process.exit(0);
      } catch (e) {
        server.kill();
        throw e;
      }
    });
  });
});
