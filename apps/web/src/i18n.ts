import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources: {
      pt: {
        translation: {
          "login": {
            "title": "Bem-vindo de volta!",
            "subtitle": "Acesse a conta do seu melhor amigo.",
            "email_label": "E-mail",
            "password_label": "Senha",
            "submit": "Entrar"
          }
        }
      },
      en: {
        translation: {
          "login": {
            "title": "Welcome back!",
            "subtitle": "Access your best friend's account.",
            "email_label": "Email",
            "password_label": "Password",
            "submit": "Sign in"
          }
        }
      },
      es: {
        translation: {
          "login": {
            "title": "¡Bienvenido de nuevo!",
            "subtitle": "Accede a la cuenta de tu mejor amigo.",
            "email_label": "Correo electrónico",
            "password_label": "Contraseña",
            "submit": "Entrar"
          }
        }
      }
    },
    fallbackLng: 'pt',
    interpolation: {
      escapeValue: false, // react already safes from xss
    }
  });

export default i18n;
