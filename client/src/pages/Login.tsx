import React from 'react';
import { useRecoilValue } from 'recoil';
import { Navigate } from 'react-router-dom';
import LoginForm from '@/organisms/LoginForm';
import userSessionAtom from '@/recoil/atoms/userSession';

const Login = () => {
  const userSession = useRecoilValue(userSessionAtom);

  if (userSession) return <Navigate to="/" />;

  return (
    <>
      <LoginForm />
    </>
  );
};

export default Login;
