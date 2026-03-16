import { StrictMode} from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import {BrowserRouter, Routes, Route } from "react-router-dom";
import Sidebar from "./components/Sidebar.tsx";
import HomePage from "./pages/HomePage.tsx";
import Statistics from "./pages/Statistics.tsx";
import Rules from "./pages/Rules.tsx";
import Settings from "./pages/Settings.tsx";
import About from "./pages/About.tsx";

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <BrowserRouter>
            <div className="bg-[#09090b] flex flex-row p-4 gap-4 h-screen">
                <Sidebar />

                <Routes>
                    <Route path="/" element={<HomePage/>} />
                    <Route path="/stats" element={<Statistics/>} />
                    <Route path="/rules" element={<Rules/>} />
                    <Route path="/settings" element={<Settings/>} />
                    <Route path="/about" element={<About/>} />
                </Routes>
            </div>
        </BrowserRouter>
    </StrictMode>,
)
