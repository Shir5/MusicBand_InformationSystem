import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";
import Auth from "./components/auth/Auth";
import Dashboard from "./components/dashboard/Dashboard";
import PrivateRoute from "./services/PrivateRoute";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/auth" />} />

                <Route path="/auth" element={<Auth />} />

                {/* Защищённый маршрут для Dashboard */}
                <Route
                    path="/dashboard/*"
                    element={
                        <PrivateRoute>
                            <Dashboard />
                        </PrivateRoute>
                    }
                />
            </Routes>
        </Router>
    );
}

export default App;
