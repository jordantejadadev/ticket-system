import { useEffect, useState } from "react";
import DashboardLayout from "../layouts/DashboardLayout";
import StatusBadge from "../components/StatusBadge";
import TicketModal from "../components/TicketModal";
import toast from "react-hot-toast";
import ConfirmModal from "../components/ConfirmModal";
import { AnimatePresence } from "framer-motion";
import {
  getTickets,
  updateTicketEstado,
  deleteTicketById,
} from "../services/ticketServices";
import useTickets from "../hooks/useTickets";

const Tickets = () => {
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const {
    tickets,
    page,
    setPage,
    totalPages,
    estado,
    setEstado,
    search,
    setSearch,
    loading,
    changeTicketEstado,
    removeTicket,
    handleMarkAsSeen,
  } = useTickets();

  const updateEstado = async (id, estado) => {
    try {
      await changeTicketEstado(id, estado);
      toast.success("Estado actualizado");
      setSelectedTicket(null);
    } catch (error) {
      toast.error("Error actualizando estado");
      console.error(error);
    }
  };

  const deleteTicket = async (id) => {
    try {
      await removeTicket(id);
      toast.success("Ticket eliminado");
      setSelectedTicket(null);
    } catch (error) {
      console.error(error);
      toast.error("Error eliminando ticket");
    }
  };

  return (
    <DashboardLayout>
      <h1 className="text-2xl font-bold mb-6">Tickets</h1>

      <div className="mb-4 flex flex-col gap-4 md:flex-row md:items-center">
        {/* estado */}
        <select
          value={estado}
          onChange={(e) => {
            setEstado(e.target.value);
            setPage(0);
          }}
          className="border border-zinc-300 rounded-lg px-4 py-2 bg-white"
        >
          <option value="">Todos</option>
          <option value="ABIERTO">Abierto</option>
          <option value="EN_PROGRESO">En progreso</option>
          <option value="CERRADO">Cerrado</option>
        </select>

        {/* search */}
        <input
          type="text"
          placeholder="Buscar asunto..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(0);
          }}
          className="border border-zinc-300 rounded-lg px-4 py-2 w-full md:w-72"
        />
      </div>
      <div className="bg-white rounded-xl shadow-sm overflow-hidden flex flex-col h-[450px]">
        {/* SOLO la tabla hace scroll */}
        <div className="flex-1 overflow-auto">
          <table className="w-full min-w-[700px]">
            <thead className="bg-zinc-100">
              <tr>
                <th className="text-left p-4">ID</th>
                <th className="text-left p-4">Asunto</th>
                <th className="text-left p-4">Estado</th>
                <th className="text-left p-4">Remitente</th>
                <th className="text-left p-4">Fecha</th>
              </tr>
            </thead>

            <tbody>
              {loading ? (
                [...Array(5)].map((_, index) => (
                  <tr key={index} className="border-t">
                    <td className="p-4">
                      <div className="h-4 w-10 bg-zinc-200 rounded animate-pulse"></div>
                    </td>

                    <td className="p-4">
                      <div className="h-4 w-40 bg-zinc-200 rounded animate-pulse"></div>
                    </td>

                    <td className="p-4">
                      <div className="h-6 w-24 bg-zinc-200 rounded-full animate-pulse"></div>
                    </td>

                    <td className="p-4">
                      <div className="h-4 w-32 bg-zinc-200 rounded animate-pulse"></div>
                    </td>

                    <td className="p-4">
                      <div className="h-4 w-32 bg-zinc-200 rounded animate-pulse"></div>
                    </td>
                  </tr>
                ))
              ) : tickets.length > 0 ? (
                tickets.map((ticket) => (
                  <tr
                    key={ticket.id}
                    onClick={async () => {
                      const updatedTicket = await handleMarkAsSeen(ticket.id);
                      setSelectedTicket(updatedTicket || ticket);
                    }}
                    className="border-t cursor-pointer hover:bg-zinc-50"
                  >
                    <td className="p-4 whitespace-nowrap">{ticket.id}</td>

                    <td className="p-4 whitespace-nowrap">
                      <div className="flex items-center gap-2">
                        {!ticket.seen && (
                          <span className="bg-red-500 text-white text-xs px-2 py-1 rounded-full">
                            NUEVO
                          </span>
                        )}
                        <div
                          className="max-w-[140px] md:max-w-[220px] truncate"
                          title={ticket.asunto}
                        >
                          {ticket.asunto}
                        </div>
                      </div>
                    </td>

                    <td className="p-4 whitespace-nowrap">
                      <StatusBadge estado={ticket.estado} />
                    </td>

                    <td className="p-4 whitespace-nowrap">
                      <div
                        className="max-w-[180px] md:max-w-[260px] truncate"
                        title={ticket.emailRemitente}
                      >
                        {ticket.emailRemitente}
                      </div>
                    </td>

                    <td className="p-4 whitespace-nowrap">
                      {new Date(ticket.createdAt).toLocaleString("es-PE", {
                        dateStyle: "short",
                        timeStyle: "short",
                      })}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5} className="text-center py-16 text-zinc-500">
                    <span className="text-4xl">📭</span>
                    No se encontraron tickets
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* PAGINACION FUERA DEL SCROLL */}
        <div className="flex items-center justify-center gap-4 p-4 border-t bg-white">
          <button
            onClick={() => setPage((prev) => prev - 1)}
            disabled={page === 0}
            className="px-4 py-2 border rounded-lg cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Anterior
          </button>

          <span>
            Página {page + 1} de {totalPages}
          </span>

          <button
            onClick={() => setPage((prev) => prev + 1)}
            disabled={page + 1 >= totalPages}
            className="px-4 py-2 border rounded-lg cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Siguiente
          </button>
        </div>
        <AnimatePresence>
          {selectedTicket && (
            <TicketModal
              ticket={selectedTicket}
              onClose={() => setSelectedTicket(null)}
              onSave={updateEstado}
              onDelete={() => setShowDeleteModal(true)}
            />
          )}
        </AnimatePresence>

        <AnimatePresence>
          {showDeleteModal && (
            <ConfirmModal
              title="Eliminar ticket"
              message="Esta accion no se puede deshacer."
              onCancel={() => setShowDeleteModal(false)}
              onConfirm={() => {
                deleteTicket(selectedTicket.id);
                setShowDeleteModal(false);
              }}
            />
          )}
        </AnimatePresence>
      </div>
    </DashboardLayout>
  );
};

export default Tickets;
