import { motion } from "framer-motion";

const CreateUserModal = ({
  setOpenModal,

  name,
  setName,

  email,
  setEmail,

  password,
  setPassword,

  role,
  setRole,

  handleCreateUser,

  handleUpdateUser,
  editingUser,
  setEditingUser,

  resetForm,
  error,
  setError,

  loadingSubmit,
  loading
}) => {
  const isEditing = !!editingUser;

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.2 }}
      className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 px-4"
    >
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 10 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 10 }}
        transition={{ duration: 0.2 }}
        className="bg-white rounded-2xl p-6 w-full max-w-lg"
      >
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-bold">
            {isEditing ? "Editar usuario" : "Nuevo Usuario"}
          </h2>
          <button
            onClick={() => {
              setOpenModal(false);
              setEditingUser(null);
              resetForm();
            }}
            className="text-zinc-500 hover:text-zinc-900 cursor-pointer"
          >
            X
          </button>
        </div>
        <form
          onSubmit={isEditing ? handleUpdateUser : handleCreateUser}
          className="space-y-4"
        >
          <input
            type="text"
            placeholder="Nombre"
            value={name}
            onChange={(e) => {
              setName(e.target.value);
              if (error.name) {
                setError((prev) => ({
                  ...prev,
                  name: "",
                }));
              }
            }}
            className={`w-full border rounded-lg px-4 py-2 ${error.name ? "border-red-500" : "border-zinc-300"}`}
          />
          {error?.name && <p className="text-red-500 text-sm">{error.name}</p>}
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
              if (error.email) {
                setError((prev) => ({
                  ...prev,
                  email: "",
                }));
              }
            }}
            className={`w-full border rounded-lg px-4 py-2 ${error.email ? "border-red-500" : "border-zinc-300"}`}
          />
          {error?.email && (
            <p className="text-red-500 text-sm">{error.email}</p>
          )}
          <input
            type="password"
            placeholder={
              isEditing ? "Nueva contraseña (opcional)" : "Contraseña"
            }
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
              if (error.password) {
                setError((prev) => ({
                  ...prev,
                  password: "",
                }));
              }
            }}
            className={`w-full border rounded-lg px-4 py-2 ${error.password ? "border-red-500" : "border-zinc-300"}`}
          />
          {error?.password && (
            <p className="text-red-500 text-sm">{error.password}</p>
          )}
          <select
            value={role}
            onChange={(e) => setRole(e.target.value)}
            className="w-full border border-zinc-300 rounded-lg px-4 py-2 bg-white"
          >
            <option value="ADMIN">ADMIN</option>
            <option value="SUPPORT">SUPPORT</option>
          </select>
          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={() => {
                setOpenModal(false);
                setEditingUser(null);
                resetForm();
              }}
              className="border px-4 py-2 rounded-lg cursor-pointer"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loadingSubmit}
              className="bg-zinc-900 hover:bg-zinc-800 text-white px-4 py-2 rounded-lg cursor-pointer"
            >
              {loadingSubmit
                ? "Guardando..."
                : isEditing
                  ? "Guardar cambios"
                  : "Crear"}
            </button>
          </div>
        </form>
      </motion.div>
    </motion.div>
  );
};

export default CreateUserModal;
