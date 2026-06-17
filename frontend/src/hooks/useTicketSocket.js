import { useEffect } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const useTicketSocket = (onNewTicket) => {
  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      onConnect: () => {
        console.log("WebSocket conectado");
        client.subscribe("/topic/tickets", (message) => {
          const newTicket = JSON.parse(message.body);

          onNewTicket(newTicket);
        });
      },
      reconnectDelay: 5000,
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [onNewTicket]);
};

export default useTicketSocket;
