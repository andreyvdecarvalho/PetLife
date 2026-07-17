const fs = require('fs');

const pagesToFix = [
  { name: 'AppointmentsPage', flexContainer: '.appointments-page__header-row', title: '.appointments-page__title' },
  { name: 'GroomingPage', flexContainer: '.page-grooming__header-row', title: '.page-grooming__title' },
  { name: 'MedicationsPage', flexContainer: '.medications-page__header', title: '.medications-page__title' },
  { name: 'MemoriesPage', flexContainer: '.memories-page__header-content', title: '.memories-page__title' },
  { name: 'PetsPage', flexContainer: '.pets-page__header', title: '.pets-page__title' },
  { name: 'VaccinesPage', flexContainer: '.vaccines-page__header', title: '.vaccines-page__title' },
  { name: 'PetProfilePage', flexContainer: '.pet-profile__header-row', title: '.pet-profile__title' },
  { name: 'PetFormPage', flexContainer: '.pet-form-page__header', title: '.pet-form-page__title' }
];

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

const cssFiles = getFiles('D:/projetos-particular/PetLife/apps/web/src/components');

cssFiles.forEach(file => {
  let content = fs.readFileSync(file, 'utf8');
  let changed = false;

  // 1. Fix back and add buttons oval issue (globally for these pages)
  const btnRegex = /\.([a-zA-Z0-9_-]+)__(back-btn|add-btn|add-photo-btn)(?:\s*,\s*\.([a-zA-Z0-9_-]+)__(back-btn|add-btn|add-photo-btn))*\s*\{([\s\S]*?)\}/g;
  content = content.replace(btnRegex, (match, p1, p2, p3, p4, inner) => {
    if (!inner.includes('flex-shrink: 0')) {
      changed = true;
      let newInner = inner + '\n  flex-shrink: 0;\n  min-width: 40px;\n  min-height: 40px;\n  border-radius: var(--radius-full);\n';
      return match.replace(inner, newInner);
    }
    return match;
  });

  // 2. Fix layout for specific pages
  pagesToFix.forEach(page => {
    if (file.includes(page.name)) {
      // Modify Flex Container
      const flexRegex = new RegExp(`\\${page.flexContainer}\\s*\\{([\\s\\S]*?)\\}`, 'g');
      content = content.replace(flexRegex, (match, inner) => {
        if (!inner.includes('border-bottom: 1px solid var(--color-outline-variant)')) {
          changed = true;
          let newInner = inner + '\n  border-bottom: 1px solid var(--color-outline-variant);\n  padding-bottom: var(--space-xs);\n  margin-bottom: var(--space-md);\n  gap: 16px;\n';
          return match.replace(inner, newInner);
        }
        return match;
      });

      // Modify Title
      const titleRegex = new RegExp(`\\${page.title}\\s*\\{([\\s\\S]*?)\\}`, 'g');
      content = content.replace(titleRegex, (match, inner) => {
        if (inner.includes('width: 100%')) {
          changed = true;
          let newInner = inner
            .replace(/margin-bottom:\s*var\(--space-md\);/g, '')
            .replace(/border-bottom:\s*1px solid var\(--color-outline-variant\);/g, '')
            .replace(/padding-bottom:\s*var\(--space-xs\);/g, '')
            .replace(/width:\s*100%;/g, '')
            .replace(/margin:\s*0;/g, 'margin: 0;\n  flex: 1;');
          return match.replace(inner, newInner);
        }
        return match;
      });
    }
  });
  
  // Also fix ProfileForm component if needed (it's not in the array but the title might be wrong)
  if (file.includes('ProfileForm') || file.includes('PetForm\\styles.css')) {
     // For forms, just change the title if it exists, wait ProfileForm doesn't have a flex header with back/add buttons. 
     // We leave it alone since it's just a block title!
  }

  if (changed) {
    fs.writeFileSync(file, content, 'utf8');
    console.log('Updated', file);
  }
});
