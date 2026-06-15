import { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { logout } from "../services/ticketServices";
import { useNavigate } from "react-router-dom";

const DashboardLayout = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user, setUser, loading } = useAuth();
  const navigate = useNavigate();

  return (
      <div className="flex min-h-screen overflow-x-hidden">
        {/* Sidebar */}
        <aside
          className={`fixed md:static top-0 left-0 z-50 h-screen w-64 bg-zinc-900 text-white p-6 
          transform transition-transform duration-300 ${sidebarOpen ? "translate-x-0" : "-translate-x-full"} 
          md:translate-x-0`}
        >
          <h1 className="text-2xl font-bold mb-10">SupportDesk</h1>
          <nav className="space-y-4">
            <Link
              to="/"
              onClick={() => setSidebarOpen(false)}
              className="block hover:text-blue-400 transition-colors duration-200"
            >
              Dashboard
            </Link>
            <Link
              to="/tickets"
              onClick={() => setSidebarOpen(false)}
              className="block hover:text-blue-400 transition-colors duration-200"
            >
              Tickets
            </Link>
            <Link
              to="/stats"
              onClick={() => setSidebarOpen(false)}
              className="block hover:text-blue-400 transition-colors duration-200"
            >
              Estadísticas
            </Link>
            {user?.role === "ADMIN" && (
              <Link
                to="/users"
                onClick={() => setSidebarOpen(false)}
                className="block hover:text-blue-400 transition-colors duration-200"
              >
                Usuarios
              </Link>
            )}
          </nav>
        </aside>

        {sidebarOpen && (
          <div
            onClick={() => setSidebarOpen(false)}
            className="fixed inset-0 bg-black/40 z-40 md:hidden"
          />
        )}

        {/* Main */}
        <main className="flex-1 flex flex-col min-w-0">
          {/* Topbar */}
          <header className="h-16 bg-white border-b flex items-center justify-between px-6">
            <div className="flex items-center gap-4">
              <button
                onClick={() => setSidebarOpen(true)}
                className="md:hidden"
              >
                ☰
              </button>
              <h2 className="text-xl font-semibold">Dashboard</h2>
            </div>

            <div className="flex items-center gap-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">
                  {user?.name?.charAt(0).toUpperCase()}
                </div>
                <div className="hidden sm:block">
                  <p className="text-sm font-semibold">{user?.name}</p>
                  <p className="text-xs text-zinc-500">{user?.role}</p>
                </div>
              </div>

              <button
                onClick={async () => {
                  await logout();
                  setUser(null);
                  navigate("/login");
                }}
                className="text-sm text-red-500 hover:text-red-600 cursor-pointer"
              >
                Logout
              </button>
            </div>
          </header>

          {/* Content */}
          <section className="flex-1 p-4 md:p-6">{children}</section>
        </main>
      </div>
    );
};

export default DashboardLayout;
