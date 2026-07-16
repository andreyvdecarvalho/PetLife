import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { ToastProvider } from './components/molecules/Toast';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { ForgotPasswordPage } from './pages/ForgotPasswordPage';
import { ResetPasswordPage } from './pages/ResetPasswordPage';
import { DashboardPage } from './pages/DashboardPage';
import { ProfilePage } from './pages/ProfilePage';
import { PetProfilePage } from './pages/PetProfilePage';
import { MedicationsPage } from './pages/MedicationsPage';
import { RoutinePage } from './pages/RoutinePage';
import { MemoriesPage } from './pages/MemoriesPage';
import { PetFormPage } from './pages/PetFormPage';
import { AppointmentsPage } from './pages/AppointmentsPage';
import { OnboardingPage } from './pages/OnboardingPage';
import { GroomingPage } from './pages/GroomingPage';
import { NotificationsPage } from './pages/NotificationsPage';
import { VetSearchPage } from './pages/VetSearchPage';
import { VetDetailPage } from './pages/VetDetailPage';
import { VetFavoritesPage } from './pages/vet-favorites/VetFavoritesPage';
import { VetProfilePage } from './pages/vet-profile/VetProfilePage';
import { PrivateRoute } from './components/atoms/PrivateRoute';
import { useAuth } from './contexts/AuthContext';
import { usePushNotifications } from './application/notification/usePushNotifications';
import './index.css';

const PushNotificationManager: React.FC = () => {
  const { isAuthenticated } = useAuth();
  usePushNotifications(isAuthenticated);
  return null;
};


export const App: React.FC = () => {
  return (
    <ToastProvider>
      <AuthProvider>
        <PushNotificationManager />
        <BrowserRouter>
          <Routes>
            {/* Rotas Pǧblicas */}
            <Route path="/onboarding" element={<OnboardingPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route path="/reset-password" element={<ResetPasswordPage />} />

            {/* Rotas Protegidas */}
            <Route
              path="/"
              element={
                <PrivateRoute>
                  <DashboardPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/pets/:id"
              element={
                <PrivateRoute>
                  <PetProfilePage />
                </PrivateRoute>
              }
            />
            <Route
              path="/pets/new"
              element={
                <PrivateRoute>
                  <PetFormPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/grooming"
              element={
                <PrivateRoute>
                  <GroomingPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/pets/:petId/grooming"
              element={
                <PrivateRoute>
                  <GroomingPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/medications"
              element={
                <PrivateRoute>
                  <MedicationsPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/routine"
              element={
                <PrivateRoute>
                  <RoutinePage />
                </PrivateRoute>
              }
            />
            <Route
              path="/memories"
              element={
                <PrivateRoute>
                  <MemoriesPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/appointments"
              element={
                <PrivateRoute>
                  <AppointmentsPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/veterinarians"
              element={
                <PrivateRoute>
                  <VetSearchPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/veterinarians/:id"
              element={
                <PrivateRoute>
                  <VetDetailPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/veterinarians/favorites"
              element={
                <PrivateRoute>
                  <VetFavoritesPage />
                </PrivateRoute>
              }
            />
            <Route
              path="/veterinarian/dashboard"
              element={
                <PrivateRoute>
                  <VetProfilePage />
                </PrivateRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <PrivateRoute>
                  <ProfilePage />
                </PrivateRoute>
              }
            />
            <Route
              path="/notifications"
              element={
                <PrivateRoute>
                  <NotificationsPage />
                </PrivateRoute>
              }
            />

            {/* Redirecionamento de rotas inexistentes */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ToastProvider>
  );
};

export default App;
