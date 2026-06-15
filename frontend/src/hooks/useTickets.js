// CON SET_INTERVAL

// import { useCallback, useEffect, useRef, useState } from "react";
// import {
//   getTickets,
//   updateTicketEstado,
//   deleteTicketById,
//   markTicketAsSeen,
// } from "../services/ticketServices";
// import toast from "react-hot-toast";

// const useTickets = () => {
//   const [tickets, setTickets] = useState([]);
//   const [page, setPage] = useState(0);
//   const [totalPages, setTotalPages] = useState(0);
//   const [estado, setEstado] = useState("");
//   const [search, setSearch] = useState("");
//   const [debouncedSearch, setDebouncedSearch] = useState("");
//   const [loading, setLoading] = useState(false);
//   const previousCount = useRef(0);
//   const firstLoad = useRef(true);
//   const isPollingRef = useRef(false);

//   useEffect(() => {
//     const timer = setTimeout(() => {
//       setDebouncedSearch(search);
//     }, 500);

//     return () => clearTimeout(timer);
//   }, [search]);

//   useEffect(() => {
//     if (firstLoad.current) {
//       firstLoad.current = false;
//       previousCount.current = tickets.filter((ticket) => !ticket.seen).length;
//       return;
//     }

//     const unseenTickets = tickets.filter((ticket) => !ticket.seen);

//     if (isPollingRef.current && unseenTickets.length > previousCount.current) {
//       toast.success("Nuevo ticket recibido");
//     }

//     previousCount.current = unseenTickets.length;

//     isPollingRef.current = false;
//   }, [tickets]);

//   const loadTickets = useCallback(
//     async (showLoading = true) => {
//       if (showLoading) {
//         setLoading(true);
//       }

//       try {
//         const data = await getTickets({
//           page,
//           estado,
//           search: debouncedSearch,
//         });
//         setTickets(data.content);
//         setTotalPages(data.totalPages);
//       } catch (error) {
//         console.error(error);
//       } finally {
//         if (showLoading) {
//           setLoading(false);
//         }
//       }
//     },
//     [page, estado, debouncedSearch],
//   );

//   useEffect(() => {
//     loadTickets();
//   }, [loadTickets]);

//   useEffect(() => {
//     const interval = setInterval(() => {
//       isPollingRef.current = true;
//       loadTickets(false);
//     }, 5000);

//     return () => clearInterval(interval);
//   }, [loadTickets]);

//   const changeTicketEstado = async (id, estado) => {
//     await updateTicketEstado(id, estado);
//     await loadTickets();
//   };

//   const removeTicket = async (id) => {
//     await deleteTicketById(id);
//     await loadTickets();
//   };

//   const handleMarkAsSeen = async (id) => {
//     try {
//       const updatedTicket = await markTicketAsSeen(id);

//       setTickets((prev) =>
//         prev.map((ticket) => (ticket.id === id ? updatedTicket : ticket)),
//       );
//       return updatedTicket;
//     } catch (error) {
//       console.error(error);
//     }
//   };

//   return {
//     tickets,
//     page,
//     setPage,
//     totalPages,
//     estado,
//     setEstado,
//     search,
//     setSearch,
//     loading,
//     changeTicketEstado,
//     removeTicket,
//     handleMarkAsSeen,
//   };
// };

// export default useTickets;

// CON WEBSOCKET
import { useCallback, useEffect, useState } from "react";
import {
  getTickets,
  updateTicketEstado,
  deleteTicketById,
  markTicketAsSeen,
} from "../services/ticketServices";
import useTicketSocket from "./useTicketSocket";
import toast from "react-hot-toast";

const useTickets = () => {
  const [tickets, setTickets] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [estado, setEstado] = useState("");
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 500);

    return () => clearTimeout(timer);
  }, [search]);

  const loadTickets = useCallback(
    async (showLoading = true) => {
      if (showLoading) {
        setLoading(true);
      }

      try {
        const data = await getTickets({
          page,
          estado,
          search: debouncedSearch,
        });
        setTickets(data.content);
        setTotalPages(data.totalPages);
      } catch (error) {
        console.error(error);
      } finally {
        if (showLoading) {
          setLoading(false);
        }
      }
    },
    [page, estado, debouncedSearch],
  );

  useEffect(() => {
    loadTickets();
  }, [loadTickets]);

  const handleNewTicket = useCallback((newTicket) => {
    setTickets((prev) => [newTicket, ...prev]);

    toast.success("Nuevo ticket recibido");
  }, []);

  useTicketSocket(handleNewTicket);

  const changeTicketEstado = async (id, estado) => {
    await updateTicketEstado(id, estado);
    await loadTickets();
  };

  const removeTicket = async (id) => {
    await deleteTicketById(id);
    await loadTickets();
  };

  const handleMarkAsSeen = async (id) => {
    try {
      const updatedTicket = await markTicketAsSeen(id);

      setTickets((prev) =>
        prev.map((ticket) => (ticket.id === id ? updatedTicket : ticket)),
      );
      return updatedTicket;
    } catch (error) {
      console.error(error);
    }
  };

  return {
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
  };
};

export default useTickets;
