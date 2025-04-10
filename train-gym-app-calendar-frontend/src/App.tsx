import React, {JSX} from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MainPage from './pages/MainPage';
import AddEventPage from "./pages/AddEventPage";
import YourWorkoutsPage from "./pages/YourWorkoutsPage";

function App(): JSX.Element {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/main" element={<MainPage />} />
                <Route path="/add-workout" element={<AddEventPage />} />
                <Route path="/your-workouts" element={<YourWorkoutsPage />} />
            </Routes>
        </Router>
    );
}

export default App;
