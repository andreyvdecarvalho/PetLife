const fs = require('fs');

const standardCss = `  font-family: var(--font-headline);
  font-size: var(--text-headline-lg-mobile-size);
  font-weight: var(--text-headline-lg-mobile-weight);
  color: var(--color-primary);
  margin: 0;
  margin-bottom: var(--space-md);
  text-align: left;
  border-bottom: 1px solid var(--color-outline-variant);
  padding-bottom: var(--space-xs);
  width: 100%;
}

@media (min-width: 768px) {
  .REPLACE_CLASS {
    font-size: var(--text-headline-lg-size);
    font-weight: var(--text-headline-lg-weight);
  }`;

function getFiles(dir) {
  let results = [];
  const list = fs.readdirSync(dir);
  list.forEach(file => {
    file = dir + '/' + file;
    const stat = fs.statSync(file);
    if (stat && stat.isDirectory()) { 
      results = results.concat(getFiles(file));
    } else if (file.endsWith('.css')) {
      results.push(file);
    }
  });
  return results;
}

const files = getFiles('D:/projetos-particular/PetLife/apps/web/src/components');
// Also include pages dir
const pageFiles = getFiles('D:/projetos-particular/PetLife/apps/web/src/pages');
const allFiles = files.concat(pageFiles);

allFiles.forEach(file => {
  if (file.includes('DashboardPage') || file.includes('OnboardingPage')) return;
  
  let content = fs.readFileSync(file, 'utf8');
  let changed = false;
  
  // Replace __title
  const titleRegex = /\.([a-zA-Z0-9_-]+(?:__title|-title|title))\s*\{([\s\S]*?)\}/g;
  
  // Actually, we want to replace the specific classes we touched before.
  // We know they are usually .[page]__title
  // Let's do a more precise replacement.
  
  const regex2 = /\.([a-zA-Z0-9_-]+__title)\s*\{([^}]*text-align:\s*left;[^}]*)\}/g;
  
  content = content.replace(regex2, (match, className) => {
    changed = true;
    let css = standardCss.replace('REPLACE_CLASS', className);
    return '.' + className + ' {\n' + css + '\n}';
  });

  // Since we might have added media queries before, or not, let's just use the regex to find the title block we inserted last time.
  // Last time we inserted exactly:
  /*
  font-family: var(--font-headline);
  font-size: var(--text-headline-md-size);
  font-weight: var(--text-headline-md-weight);
  color: var(--color-primary);
  margin: 0;
  margin-bottom: 24px;
  text-align: left;
  border-bottom: 1px solid var(--color-outline-variant);
  padding-bottom: 12px;
  width: 100%;
  */
  const oldBlockRegex = /\.([a-zA-Z0-9_-]+__title)\s*\{[\s\S]*?width:\s*100%;\s*\}/g;
  
  let newContent = fs.readFileSync(file, 'utf8');
  newContent = newContent.replace(oldBlockRegex, (match, className) => {
    changed = true;
    let css = standardCss.replace('REPLACE_CLASS', className);
    return '.' + className + ' {\n' + css + '\n}';
  });
  
  // Check if ProfileForm/styles.css has a title we missed because it's named differently
  if (file.includes('ProfileForm') && newContent.includes('.profile-form__title')) {
     newContent = newContent.replace(/\.profile-form__title\s*\{[\s\S]*?width:\s*100%;\s*\}/g, (match) => {
       changed = true;
       let css = standardCss.replace('REPLACE_CLASS', 'profile-form__title');
       return '.profile-form__title {\n' + css + '\n}';
     });
  }

  if (changed) {
    fs.writeFileSync(file, newContent, 'utf8');
    console.log('Updated', file);
  }
});
