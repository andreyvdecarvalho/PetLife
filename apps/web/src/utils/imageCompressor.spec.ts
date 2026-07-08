import { describe, it, expect, vi } from 'vitest';
import { compressImage } from './imageCompressor';

describe('imageCompressor utility', () => {
  it('should return the original file immediately if its size is below the limit', async () => {
    const file = new File(['small image content'], 'pet.jpg', { type: 'image/jpeg' });
    
    // file size is 20 bytes, limit is 500KB (512000 bytes)
    const result = await compressImage(file, 500);
    expect(result).toBe(file);
  });

  it('should reject if FileReader fails', async () => {
    const file = new File(['large image content'.repeat(100000)], 'pet.jpg', { type: 'image/jpeg' });

    const readAsDataURLMock = vi.fn().mockImplementation(function (this: any) {
      setTimeout(() => {
        if (this.onerror) {
          this.onerror(new Error('FileReader error'));
        }
      }, 0);
    });

    const originalFileReader = globalThis.FileReader;
    globalThis.FileReader = class {
      readAsDataURL = readAsDataURLMock;
    } as any;

    await expect(compressImage(file, 1)).rejects.toThrow('FileReader error');

    globalThis.FileReader = originalFileReader;
  });
});
