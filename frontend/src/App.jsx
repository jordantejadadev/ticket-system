import { useState } from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import Tickets from './pages/Tickets';
import Stats from './pages/Stats';
import Login from './pages/Login';
import Users from './pages/Users';

import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/login' element={<Login />} />
        <Route path='/' element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path='/tickets' element={<ProtectedRoute><Tickets /></ProtectedRoute>} />
        <Route path='/stats' element={<ProtectedRoute><Stats /></ProtectedRoute>} />
        <Route path='/users' element={<ProtectedRoute><Users /></ProtectedRoute>} />
      </Routes>      
    </BrowserRouter>
  )
}

export default App;