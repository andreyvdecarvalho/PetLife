import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { compressImage } from './imageCompressor';

describe('imageCompressor utility', () => {
  let originalFileReader: any;
  let originalImage: any;
  let originalCreateElement: any;

  beforeEach(() => {
    originalFileReader = global.FileReader;
    originalImage = global.Image;
    originalCreateElement = document.createElement;
  });

  afterEach(() => {
    global.FileReader = originalFileReader;
    global.Image = originalImage;
    document.createElement = originalCreateElement;
    vi.restoreAllMocks();
  });

  it('should return the original file immediately if its size is below the limit', async () => {
    const file = new File(['small image content'], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 100 * 1024 }); // 100KB
    
    const result = await compressImage(file, 500);
    expect(result).toBe(file);
  });

  it('should reject if FileReader fails', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    global.FileReader = vi.fn(() => mockFileReader) as any;

    const compressPromise = compressImage(file, 500);
    mockFileReader.onerror(new Error('FileReader error'));

    await expect(compressPromise).rejects.toThrow('FileReader error');
  });

  it('should reject if Image fails to load', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    global.FileReader = vi.fn(() => mockFileReader) as any;

    const mockImage = { src: '', onload: null as any, onerror: null as any };
    global.Image = vi.fn(() => mockImage) as any;

    const compressPromise = compressImage(file, 500);
    mockFileReader.onload({ target: { result: 'data:image/jpeg;base64,abc' } } as any);
    mockImage.onerror(new Error('Image error'));

    await expect(compressPromise).rejects.toThrow('Image error');
  });

  it('should reject if canvas 2d context cannot be created', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    global.FileReader = vi.fn(() => mockFileReader) as any;

    const mockImage = { src: '', width: 800, height: 600, onload: null as any, onerror: null as any };
    global.Image = vi.fn(() => mockImage) as any;

    const mockCanvas = { width: 0, height: 0, getContext: vi.fn(() => null) };
    document.createElement = vi.fn((tag) => tag === 'canvas' ? mockCanvas : originalCreateElement.call(document, tag)) as any;

    const compressPromise = compressImage(file, 500);
    mockFileReader.onload({ target: { result: 'data' } } as any);
    mockImage.onload();

    await expect(compressPromise).rejects.toThrow('Não foi possível obter o contexto 2D do Canvas.');
  });

  it('should compress image iteratively and resize using canvas', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    global.FileReader = vi.fn(() => mockFileReader) as any;

    const mockImage = { src: '', width: 2000, height: 1000, onload: null as any, onerror: null as any };
    global.Image = vi.fn(() => mockImage) as any;

    const mockContext = { drawImage: vi.fn() };
    const mockToBlob = vi.fn((callback, type, quality) => {
      if (quality === 0.85) {
        // First try: fake a blob that is still too large
        callback(null); // Wait, if blob is null it should reject, let's test null blob in another test, here return large blob
      }
    });

    const mockToBlobCorrect = vi.fn((callback, type, quality) => {
      if (quality > 0.5) {
        const largeBlob = new Blob(['large'], { type: 'image/jpeg' });
        Object.defineProperty(largeBlob, 'size', { value: 600 * 1024 });
        callback(largeBlob);
      } else {
        const smallBlob = new Blob(['small'], { type: 'image/jpeg' });
        Object.defineProperty(smallBlob, 'size', { value: 400 * 1024 });
        callback(smallBlob);
      }
    });

    const mockCanvas = {
      width: 0,
      height: 0,
      getContext: vi.fn(() => mockContext),
      toBlob: mockToBlobCorrect,
    };
    document.createElement = vi.fn((tag) => tag === 'canvas' ? mockCanvas : originalCreateElement.call(document, tag)) as any;

    const compressPromise = compressImage(file, 500);
    mockFileReader.onload({ target: { result: 'data:image/jpeg;base64,abc' } } as any);
    mockImage.onload();

    const result = await compressPromise;
    expect(result).toBeInstanceOf(File);
    expect(result.name).toBe('pet.jpg');
    // Verify resize logic: maxDimension = 1200. width=2000, height=1000 -> width=1200, height=600
    expect(mockCanvas.width).toBe(1200);
    expect(mockCanvas.height).toBe(600);
    expect(mockToBlobCorrect).toHaveBeenCalledTimes(4); // 0.85, 0.70, 0.55, 0.40
  });

  it('should reject if canvas.toBlob returns null', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    global.FileReader = vi.fn(() => mockFileReader) as any;

    const mockImage = { src: '', width: 500, height: 500, onload: null as any, onerror: null as any };
    global.Image = vi.fn(() => mockImage) as any;

    const mockContext = { drawImage: vi.fn() };
    const mockCanvas = {
      width: 0,
      height: 0,
      getContext: vi.fn(() => mockContext),
      toBlob: vi.fn((cb) => cb(null)),
    };
    document.createElement = vi.fn((tag) => tag === 'canvas' ? mockCanvas : originalCreateElement.call(document, tag)) as any;

    const compressPromise = compressImage(file, 500);
    mockFileReader.onload({ target: { result: 'data' } } as any);
    mockImage.onload();

    await expect(compressPromise).rejects.toThrow('Falha ao converter o Canvas em Blob.');
  });
});
