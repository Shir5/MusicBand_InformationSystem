import React from "react";
import { Navigate } from "react-router-dom";
import { isTokenValid } from "./isTokenValid";

const PrivateRoute = ({ children }) => {
    return isTokenValid() ? children : <Navigate to="/auth" replace />;
};

export default PrivateRoute;
