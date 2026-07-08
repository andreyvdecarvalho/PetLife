import { describe, it, expect } from 'vitest';
import { isValidEmail, isValidPassword, isNotEmpty, hasMinLength } from './validators';

describe('validators utilities', () => {
  describe('isValidEmail', () => {
    it('should validate correct emails', () => {
      expect(isValidEmail('test@example.com')).toBe(true);
      expect(isValidEmail('user.name@domain.co.uk')).toBe(true);
    });

    it('should invalidate incorrect emails', () => {
      expect(isValidEmail('invalid-email')).toBe(false);
      expect(isValidEmail('test@example')).toBe(false);
      expect(isValidEmail('@example.com')).toBe(false);
    });
  });

  describe('isValidPassword', () => {
    it('should validate strong passwords meeting requirements', () => {
      expect(isValidPassword('StrongPass1')).toBe(true);
      expect(isValidPassword('P4sswordTest')).toBe(true);
    });

    it('should invalidate passwords failing requirements', () => {
      expect(isValidPassword('short')).toBe(false); // short
      expect(isValidPassword('NoDigits')).toBe(false); // no digits
      expect(isValidPassword('nodigitsandlowercase')).toBe(false); // no caps or digits
      expect(isValidPassword('12345678')).toBe(false); // no uppercase
    });
  });

  describe('isNotEmpty', () => {
    it('should return true for non-empty string', () => {
      expect(isNotEmpty('hello')).toBe(true);
    });

    it('should return false for empty or spaces only string', () => {
      expect(isNotEmpty('')).toBe(false);
      expect(isNotEmpty('   ')).toBe(false);
    });
  });

  describe('hasMinLength', () => {
    it('should return true if length is greater than or equal to min', () => {
      expect(hasMinLength('abc', 3)).toBe(true);
      expect(hasMinLength('abcd', 3)).toBe(true);
    });

    it('should return false if length is less than min', () => {
      expect(hasMinLength('ab', 3)).toBe(false);
      expect(hasMinLength('  ab  ', 3)).toBe(false); // trimmed to 'ab' (length 2)
    });
  });
});
