const fs = require('fs');
const path = require('path');

function walk(dir) {
  let results = [];
  const list = fs.readdirSync(dir);
  list.forEach(function(file) {
    file = dir + '/' + file;
    const stat = fs.statSync(file);
    if (stat && stat.isDirectory()) { 
      results = results.concat(walk(file));
    } else { 
      if (file.endsWith('.ts') || file.endsWith('.tsx')) {
        results.push(file);
      }
    }
  });
  return results;
}

const srcDir = path.join(__dirname, 'src');
const files = walk(srcDir);

files.forEach(file => {
  let content = fs.readFileSync(file, 'utf8');
  let newContent = content
    .replace(/catch\s*\(\s*err\s*:\s*any\s*\)/g, 'catch (err: unknown)')
    .replace(/catch\s*\(\s*e\s*:\s*any\s*\)/g, 'catch (e: unknown)')
    .replace(/catch\s*\(\s*error\s*:\s*any\s*\)/g, 'catch (error: unknown)')
    .replace(/err\.response\?\.data\?\.message/g, '(err as any).response?.data?.message')
    .replace(/err\ instanceof\ Error\ \?\ err\.message\ :\ '([^']+)'/g, 'err instanceof Error ? err.message : \'$1\'');
  
  if (content !== newContent) {
    fs.writeFileSync(file, newContent, 'utf8');
    console.log('Updated', file);
  }
});
