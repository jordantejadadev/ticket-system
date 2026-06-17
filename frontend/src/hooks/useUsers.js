import { useState, useEffect } from "react";
import {
  loadUsers,
  createUser,
  deleteUserById,
  updateUser,
} from "../services/userServices";

export const useUsers = () => {
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("SUPPORT");
  const [openModal, setOpenModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [error, setError] = useState({});
  const [loadingSubmit, setLoadingSubmit] = useState(false);

  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);

  const resetForm = () => {
    setName("");
    setEmail("");
    setPassword("");
    setRole("SUPPORT");
    setEditingUser(null);
    setError({});
  };

  // const fetchUsers = async () => {
  //   try {
  //     const data = await loadUsers(page);
  //     setUsers(data.content);
  //     setTotalPages(data.totalPages);
  //   } catch (error) {
  //     console.error(error);
  //   }
  // };

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await loadUsers(page, search);
      setUsers(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  // useEffect(() => {
  //   fetchUsers();
  // }, [page]);

  useEffect(() => {
    const timeout = setTimeout(() => {
      fetchUsers();
    }, 500);

    return () => clearTimeout(timeout);
  }, [page, search]);

  useEffect(() => {
    if (editingUser) {
      setName(editingUser.name);
      setEmail(editingUser.email);
      setRole(editingUser.role);
      setPassword("");
    }
  }, [editingUser]);

  const handleCreateUser = async (e) => {
    e.preventDefault();

    setLoadingSubmit(true);
    try {
      await createUser({ name, email, password, role });
      resetForm();
      await fetchUsers();
      setOpenModal(false);
    } catch (error) {
      setError(error.errors || {});
    } finally {
      setLoadingSubmit(false);
    }
  };

  const removeUser = async (id) => {
    await deleteUserById(id);
    await fetchUsers();
  };

  const handleUpdateUser = async (e) => {
    e.preventDefault();

    try {
      const payload = {
        name,
        email,
        role,
      };

      if (password.trim()) {
        payload.password = password;
      }

      await updateUser(editingUser.id, payload);

      await fetchUsers();

      resetForm();
      setOpenModal(false);
    } catch (error) {
      setError(error.errors || {});
    }
  };

  return {
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
    editingUser,
    setEditingUser,
    handleUpdateUser,
    resetForm,
    error,
    setError,
    loadingSubmit,
    search,
    setSearch,
    loading    
  };
};
