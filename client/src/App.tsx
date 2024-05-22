import React from 'react';
import { Outlet } from 'react-router-dom';
import { axiosConfig } from '@/config/axios';
import Header from '@/organisms/Header';

import useAuth from './hooks/useAuth';

axiosConfig();

function App() {
  useAuth();

  return (
    <>
      <Header />
      <main>
        <Outlet />
      </main>
    </>
  );
}

export default App;
