import DashboardLayout from "../layouts/DashboardLayout";
// import { createUser } from "../services/userServices";
import { useUsers } from "../hooks/useUsers";
import CreateUserModal from "../components/users/CreateUserModal";
import { isAdmin } from "../services/authService";
// import { useAuth } from "../hooks/useAuth";
import { useAuth } from "../context/AuthContext";
import { useEffect, useState } from "react";
import ConfirmModal from "../components/ConfirmModal";
import { AnimatePresence } from "framer-motion";

const Users = () => {
  const { user } = useAuth();
  const [selectedUser, setSelectedUser] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const {
    users,
    page,
    totalPages,
    setPage,
    name,
    setName,
    email,
    setEmail,
    password,
    setPassword,
    role,
    setRole,
    handleCreateUser,
    openModal,
    setOpenModal,
    removeUser,
    setEditingUser,
    handleUpdateUser,
    editingUser,
    resetForm,
    error,
    setError,
    loadingSubmit,
    search,
    setSearch,
    loading,
  } = useUsers();

  return (
    <DashboardLayout>
      <h1 className="text-2xl font-bold mb-6">Usuarios</h1>

      <div className="flex items-center justify-between mb-6">
        <input
          type="text"
          placeholder="Buscar usuario..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="border border-zinc-300 rounded-lg px-4 py-2 w-80"
        />
        {isAdmin(user) && (
          <button
            onClick={() => {
              resetForm();
              setOpenModal(true);
            }}
            className="bg-zinc-900 hover:bg-zinc-800 text-white px-4 py-2 rounded-lg cursor-pointer"
          >
            + Nuevo Usuario
          </button>
        )}
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl shadow-sm overflow-x-auto">
        <table className="w-full min-w-[600px]">
          <thead className="bg-zinc-100">
            <tr>
              <th className="text-left p-4">ID</th>
              <th className="text-left p-4">Nombre</th>
              <th className="text-left p-4">Email</th>
              <th className="text-left p-4">Rol</th>
              {isAdmin(user) && <th className="text-left p-4">Acciones</th>}
            </tr>
          </thead>
          <tbody>
            {loading
              ? Array.from({ length: 5 }).map((_, index) => (
                  <tr key={index} className="border-t animate-pulse">
                    <td className="p-4">
                      <div className="h-4 bg-zinc-200 rounded w-10"></div>
                    </td>
                    <td className="p-4">
                      <div className="h-4 bg-zinc-200 rounded w-32"></div>
                    </td>
                    <td className="p-4">
                      <div className="h-4 bg-zinc-200 rounded w-48"></div>
                    </td>
                    <td className="p-4">
                      <div className="h-4 bg-zinc-200 rounded w-20"></div>
                    </td>
                  </tr>
                ))
              : users.map((item) => (
                  <tr key={item.id} className="border-t">
                    <td className="p-4">{item.id}</td>
                    <td className="p-4">{item.name}</td>
                    <td className="p-4">{item.email}</td>
                    <td className="p-4">{item.role}</td>
                    {isAdmin(user) && (
                      <td className="p-4">
                        <div className="flex gap-2">
                          <button
                            onClick={() => {
                              setEditingUser(item);
                              setOpenModal(true);
                            }}
                            className="border px-3 py-1 rounded-lg cursor-pointer"
                          >
                            Editar
                          </button>
                          <button
                            onClick={() => {
                              setSelectedUser(item);
                              setShowDeleteModal(true);
                            }}
                            className="bg-red-500 text-white px-3 py-1 rounded-lg cursor-pointer"
                          >
                            Eliminar
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="flex items-center justify-center gap-4 mt-6">
        <button
          onClick={() => setPage((prev) => prev - 1)}
          disabled={page === 0}
          className="px-4 py-2 border rounded-lg disabled:opacity-50"
        >
          Anterior
        </button>
        <span>
          Página {page + 1} de {totalPages}
        </span>
        <button
          onClick={() => setPage((prev) => prev + 1)}
          disabled={page + 1 >= totalPages}
          className="px-4 py-2 border rounded-lg disabled:opacity-50"
        >
          Siguiente
        </button>
      </div>
      <AnimatePresence>
        {openModal && (
          <CreateUserModal
            setOpenModal={setOpenModal}
            name={name}
            setName={setName}
            email={email}
            setEmail={setEmail}
            password={password}
            setPassword={setPassword}
            role={role}
            setRole={setRole}
            handleCreateUser={handleCreateUser}
            handleUpdateUser={handleUpdateUser}
            editingUser={editingUser}
            setEditingUser={setEditingUser}
            resetForm={resetForm}
            error={error}
            setError={setError}
            loadingSubmit={loadingSubmit}
          />
        )}
      </AnimatePresence>
      <AnimatePresence>
        {showDeleteModal && (
          <ConfirmModal
            title="Eliminar usuario"
            message={`¿Seguro que deseas eliminar a ${selectedUser?.name}?`}
            onCancel={() => {
              setShowDeleteModal(false);
              setSelectedUser(null);
            }}
            onConfirm={async () => {
              await removeUser(selectedUser?.id);
              setShowDeleteModal(false);
              setSelectedUser(null);
            }}
          />
        )}
      </AnimatePresence>
    </DashboardLayout>
  );
};

export default Users;
