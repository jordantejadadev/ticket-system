import { motion } from "framer-motion";

const ConfirmModal = ({ title, message, onConfirm, onCancel }) => {
  
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.2 }}
      className="fixed inset-0 bg-black/40 flex items-center justify-center p-4 z-50"
    >
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 10 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 10 }}
        transition={{ duration: 0.2 }}
        className="bg-white rounded-2xl w-full max-w-md shadow-xl p-6"
      >
        <h2 className="text-xl font-bold mb-3">{title}</h2>
        <p className="text-zinc-600 mb-6">{message}</p>
        <div className="flex justify-end gap-3">
          <button
            onClick={onCancel}
            className="border hover:cursor-pointer px-4 py-2 rounded-xl"
          >
            Cancelar
          </button>
          <button
            onClick={onConfirm}
            className="bg-red-500 hover:bg-red-600 hover:cursor-pointer text-white px-4 p-2 rounded-xl"
          >
            Eliminar
          </button>
        </div>
      </motion.div>
    </motion.div>
  );
};

export default ConfirmModal;
