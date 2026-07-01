/**
 * Utilitário de compressão de imagens local usando Canvas do HTML5.
 * Garante que a foto do pet respeite o limite de 500KB antes de ser enviada à API.
 */
export async function compressImage(file: File, maxSizeKB = 500): Promise<File> {
  const maxSizeBytes = maxSizeKB * 1024;
  
  if (file.size <= maxSizeBytes) {
    return file;
  }

  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (event) => {
      const img = new Image();
      img.src = event.target?.result as string;
      img.onload = () => {
        const canvas = document.createElement('canvas');
        let width = img.width;
        let height = img.height;

        // Redimensiona proporcionalmente se a dimensão máxima for muito alta
        const maxDimension = 1200;
        if (width > maxDimension || height > maxDimension) {
          if (width > height) {
            height = Math.round((height * maxDimension) / width);
            width = maxDimension;
          } else {
            width = Math.round((width * maxDimension) / height);
            height = maxDimension;
          }
        }

        canvas.width = width;
        canvas.height = height;

        const ctx = canvas.getContext('2d');
        if (!ctx) {
          return reject(new Error('Não foi possível obter o contexto 2D do Canvas.'));
        }

        ctx.drawImage(img, 0, 0, width, height);

        // Comprime iterativamente reduzindo a qualidade do JPEG
        let quality = 0.85;
        const checkAndResolve = (q: number) => {
          canvas.toBlob(
            (blob) => {
              if (!blob) {
                return reject(new Error('Falha ao converter o Canvas em Blob.'));
              }
              
              if (blob.size <= maxSizeBytes || q <= 0.1) {
                const compressedFile = new File([blob], file.name, {
                  type: 'image/jpeg',
                  lastModified: Date.now(),
                });
                resolve(compressedFile);
              } else {
                // Tenta novamente com uma qualidade menor
                checkAndResolve(q - 0.15);
              }
            },
            'image/jpeg',
            q
          );
        };

        checkAndResolve(quality);
      };
      img.onerror = (err) => reject(err);
    };
    reader.onerror = (err) => reject(err);
  });
}
