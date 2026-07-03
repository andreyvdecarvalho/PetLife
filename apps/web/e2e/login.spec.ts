import { test, expect } from '@playwright/test';

test('deve abrir a pagina de login com sucesso', async ({ page }) => {
  await page.goto('/login');
  const title = await page.title();
  expect(title).toContain('PetLife');
});
