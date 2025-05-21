import React, {JSX} from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './Layout';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MainPage from './pages/MainPage';
import AddEventPage from './pages/AddEventPage';
import YourWorkoutsPage from './pages/YourWorkoutsPage';
import EditWorkoutPage from './pages/EditWorkoutPage';
import EditWorkoutCalPage from './pages/EditWorkoutCalPage';
import CalendarPage from './pages/CalendarPage';
import TrainingDetailPage from './pages/TrainingDetailPage';
import OneRMCalculatorPage from './pages/OneRMCalculatorPage';
import ProfilePage from './pages/ProfilPage';
import AdminGuard from "./AdminGuard";
import AdminPage from "./pages/AdminPage";
import TrainerGuard from "./TrainerGuard";
import TrainerPanelPage from "./pages/TrainerPanelPage";

function App(): JSX.Element {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                <Route path="/" element={<Layout />}>
                    <Route index element={<Navigate to="/main" />} />
                    <Route path="main" element={<MainPage />} />
                    <Route path="add-workout" element={<AddEventPage />} />
                    <Route path="your-workouts" element={<YourWorkoutsPage />} />
                    <Route path="edit-workout/:id" element={<EditWorkoutPage />} />
                    <Route path="edit-workout-cal/:id" element={<EditWorkoutCalPage />} />
                    <Route path="calendar" element={<CalendarPage />} />
                    <Route path="training-detail/:id" element={<TrainingDetailPage />} />
                    <Route path="1rm-calculator" element={<OneRMCalculatorPage />} />
                    <Route path="profile" element={<ProfilePage />} />
                    <Route path="admin" element={<AdminGuard><AdminPage /></AdminGuard>} />
                    <Route path="trainer-panel" element={<TrainerGuard><TrainerPanelPage /></TrainerGuard>} />
                    <Route path="*" element={<Navigate to="/main" />} />
                </Route>

                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;
