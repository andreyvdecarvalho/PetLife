import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { compressImage } from './imageCompressor';

describe('imageCompressor utility', () => {
  let originalFileReader: any;
  let originalImage: any;
  let originalCreateElement: any;

  beforeEach(() => {
    originalFileReader = globalThis.FileReader;
    originalImage = globalThis.Image;
    originalCreateElement = document.createElement;
  });

  afterEach(() => {
    globalThis.FileReader = originalFileReader;
    globalThis.Image = originalImage;
    document.createElement = originalCreateElement;
    vi.restoreAllMocks();
  });

  it('should return the original file immediately if its size is below the limit', async () => {
    const file = new File(['small image content'], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 100 * 1024 }); // 100KB
    
    const result = await compressImage(file, 500);
    expect(result).toBe(file);
  });

  it('should return original file if FileReader fails', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    globalThis.FileReader = vi.fn(() => mockFileReader) as any;

    const compressPromise = compressImage(file, 500);
    mockFileReader.onerror(new Error('FileReader error'));

    const result = await compressPromise;
    expect(result).toBe(file);
  });

  it('should return original file if Image fails to load', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    globalThis.FileReader = vi.fn(() => mockFileReader) as any;

    const mockImage = { src: '', onload: null as any, onerror: null as any };
    globalThis.Image = vi.fn(() => mockImage) as any;

    const compressPromise = compressImage(file, 500);
    mockFileReader.onload({ target: { result: 'data:image/jpeg;base64,abc' } } as any);
    mockImage.onerror(new Error('Image error'));

    const result = await compressPromise;
    expect(result).toBe(file);
  });

  it('should return original file if canvas 2d context cannot be created', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    globalThis.FileReader = vi.fn(() => mockFileReader) as any;

    const mockImage = { src: '', width: 800, height: 600, onload: null as any, onerror: null as any };
    globalThis.Image = vi.fn(() => mockImage) as any;

    const mockCanvas = { width: 0, height: 0, getContext: vi.fn(() => null) };
    document.createElement = vi.fn((tag) => tag === 'canvas' ? mockCanvas : originalCreateElement.call(document, tag)) as any;

    const compressPromise = compressImage(file, 500);
    mockFileReader.onload({ target: { result: 'data' } } as any);
    mockImage.onload();

    const result = await compressPromise;
    expect(result).toBe(file);
  });

  it('should compress image iteratively and resize using canvas', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    globalThis.FileReader = vi.fn(() => mockFileReader) as any;

    const mockImage = { src: '', width: 2000, height: 1000, onload: null as any, onerror: null as any };
    globalThis.Image = vi.fn(() => mockImage) as any;

    const mockContext = { drawImage: vi.fn() };
    const mockToBlob = vi.fn((callback, type, quality) => {
      if (quality === 0.85) {
        callback(null); 
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
    expect(mockCanvas.width).toBe(1200);
    expect(mockCanvas.height).toBe(600);
    expect(mockToBlobCorrect).toHaveBeenCalledTimes(4); // 0.90, 0.75, 0.60, 0.45 - wait, initial is 0.9.
  });

  it('should return original file if canvas.toBlob returns null', async () => {
    const file = new File(['large image content'.repeat(10000)], 'pet.jpg', { type: 'image/jpeg' });
    Object.defineProperty(file, 'size', { value: 1000 * 1024 });

    const mockFileReader = { readAsDataURL: vi.fn(), onload: null as any, onerror: null as any };
    globalThis.FileReader = vi.fn(() => mockFileReader) as any;

    const mockImage = { src: '', width: 500, height: 500, onload: null as any, onerror: null as any };
    globalThis.Image = vi.fn(() => mockImage) as any;

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

    const result = await compressPromise;
    expect(result).toBe(file);
  });
});
