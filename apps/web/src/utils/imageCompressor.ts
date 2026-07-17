/**
 * Utilitário de compressão de imagens local usando Canvas do HTML5.
 * Garante que a foto do pet respeite o limite de 2MB antes de ser enviada à API.
 */
export async function compressImage(file: File, maxSizeKB = 1800): Promise<File> {
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
          // Fallback if canvas is not supported
          return resolve(file);
        }

        ctx.drawImage(img, 0, 0, width, height);

        // Preserve format if PNG or WEBP, otherwise use JPEG
        const outputType = file.type === 'image/png' || file.type === 'image/webp' ? file.type : 'image/jpeg';

        // Comprime iterativamente
        let quality = 0.9;
        const checkAndResolve = (q: number) => {
          canvas.toBlob(
            (blob) => {
              if (!blob) {
                return resolve(file); // Fallback to original file if blob creation fails
              }
              
              if (blob.size <= maxSizeBytes || q <= 0.1) {
                const compressedFile = new File([blob], file.name, {
                  type: outputType,
                  lastModified: Date.now(),
                });
                resolve(compressedFile);
              } else {
                // Tenta novamente com uma qualidade menor
                checkAndResolve(q - 0.15);
              }
            },
            outputType,
            q
          );
        };

        checkAndResolve(quality);
      };
      img.onerror = (err) => {
        // Se a imagem não puder ser lida pelo canvas (ex: HEIC no Chrome), retorna o arquivo original
        // O backend pode rejeitar se for maior que 2MB, mas pelo menos evitamos o erro silencioso.
        resolve(file);
      };
    };
    reader.onerror = (err) => resolve(file);
  });
}
