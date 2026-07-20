const fs = require('fs');

const filePaths = [
  'D:/projetos-particular/PetLife/apps/web/src/components/organisms/ConsultationForm/index.tsx',
  'D:/projetos-particular/PetLife/apps/web/src/components/organisms/PetForm/index.tsx',
  'D:/projetos-particular/PetLife/apps/web/src/components/organisms/VaccineForm/index.tsx',
  'D:/projetos-particular/PetLife/apps/web/src/components/pages/PetsPage/index.tsx'
];

filePaths.forEach(f => {
  if (fs.existsSync(f)) {
    let content = fs.readFileSync(f, 'utf8');
    content = content.replace(/variant=["']outline["']/g, 'variant="secondary"');
    fs.writeFileSync(f, content);
  }
});
console.log('Variants fixed!');
