const fs = require('fs');
const assert = require('assert');
const { execSync } = require('child_process');

execSync('node build.js');

const out = fs.readFileSync('dist/index.html', 'utf8');
assert.ok(out.includes('<span class="badge">SSG</span>'));
assert.ok(out.includes('Page built at:'));

console.log('ok');
