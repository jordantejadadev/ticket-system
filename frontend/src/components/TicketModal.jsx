import { useEffect, useState } from "react";
import StatusBadge from "./StatusBadge";
import { X } from "lucide-react";
import { motion } from "framer-motion";
import { useAuth } from "../context/AuthContext";

const TicketModal = ({ ticket, onClose, onSave, onDelete }) => {
  const [nuevoEstado, setNuevoEstado] = useState("");
  // const [user, setUser] = useState(null);

  useEffect(() => {
    if(ticket) {
      setNuevoEstado(ticket.estado);
    }
  }, [ticket]);

  // useEffect(() => {
  //   const fetchUser = async () => {
  //     const data = await getMe();
  //     setUser(data);
  //   };

  //   fetchUser();
  // }, []);

  const { user } = useAuth();

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.2 }}
      className="fixed inset-0 bg-black/40 flex items-center justify-center p-4 backdrop-blur-sm"
    >
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 10 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 10 }}
        transition={{ duration: 0.2 }}
        className="bg-white rounded-2xl w-full max-w-2xl shadow-xl"
      >
        {/* Header */}
        <div className="flex justify-between items-center border-b p-6">
          <div>
            <h2 className="text-2xl font-bold">Ticket #{ticket.id}</h2>
            <p className="text-zinc-500">{ticket.emailRemitente}</p>
          </div>
          <button
            onClick={onClose}
            className="text-zinc-500 hover:text-black text-xl"
          >
            <X
              size={20}
              className="rounded-lg transition duration-200 ease-in text-zinc-500 hover:text-black hover:cursor-pointer"
            />
          </button>
        </div>

        {/* Body */}
        <div className="p-6 space-y-6 max-h-[70vh] overflow-y-auto">
          <div>
            <p className="text-sm text-zinc-500 mb-1">Asunto</p>
            <p className=" text-lg break-words">{ticket.asunto}</p>
          </div>

          <div>
            <p className="text-sm text-zinc-500 mb-1">Estado actual</p>
            <StatusBadge estado={ticket.estado} />
          </div>

          <div>
            <p className="text-sm text-zinc-500 mb-2">Cambiar estado</p>
            <select
              value={nuevoEstado}
              onChange={(e) => setNuevoEstado(e.target.value)}
              className="border rounded-xl px-4 py-2 w-full"
            >
              <option value="ABIERTO">ABIERTO</option>
              <option value="EN_PROGRESO">EN PROGRESO</option>
              <option value="CERRADO">CERRADO</option>
            </select>
          </div>

          <div>
            <p className="text-sm text-zinc-500 mb-2">Descripción</p>
            <div className="bg-zinc-100 rounded-xl p-4 text-zinc-700 whitespace-pre-wrap break-words">
              {ticket.descripcion}
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="border-t p-6 flex justify-between">
          {user?.role === "ADMIN" && (
            <button
              onClick={onDelete}
              className="bg-red-500 hover:bg-red-600 hover:cursor-pointer text-white px-5 py-2 rounded-xl"
            >
              Eliminar
            </button>
          )}

          <div className="flex gap-3">
            <button
              onClick={onClose}
              className="border px-5 py-2 rounded-xl hover:cursor-pointer"
            >
              Cancelar
            </button>
            <button
              onClick={() => onSave(ticket.id, nuevoEstado)}
              className="bg-blue-600 hover:bg-blue-700 hover:cursor-pointer text-white px-5 py-2 rounded-xl"
            >
              Guardar
            </button>
          </div>
        </div>
      </motion.div>
    </motion.div>
  );
};

export default TicketModal;
